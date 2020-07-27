package com.whiskytangofox.ptbadiscordbot.Services.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Resource;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.Services.SheetReaderService;

import java.util.ArrayList;
import java.util.Arrays;

public class ResourceParser implements INoteParser {


    @Override
    public String parse(SheetReaderService service, RangeWrapper sheet, Playbook playbook, String note, int i, int j) {
        String resourceName = null;
        Integer min = null;
        Integer max = null;
        String movePenalty = null;
        ArrayList<String> statPenalty = new ArrayList<>();
        for (String parameter : note.split(";")) {
            String tag = parameter.split("=")[0];
            String value = parameter.split("=")[1];
            if (tag.equalsIgnoreCase(SheetReaderService.PARSER.resource.name())) {
                resourceName = value;
            } else if (tag.equalsIgnoreCase("min")) {
                min = Integer.parseInt(value);
            } else if (tag.equalsIgnoreCase("max")) {
                max = Integer.parseInt(value);
            } else if (tag.equalsIgnoreCase("move_penalty")) {
                movePenalty = value;
            } else if (tag.equalsIgnoreCase("stat_penalty")) {
                Arrays.stream(value.split(",")).forEach(s -> statPenalty.add(s));
            }
        }
        int size = 0;
        if (playbook.resources.containsKey(resourceName)) {
            playbook.resources.get(resourceName).add(new CellReference(i, j));
            size = playbook.resources.get(resourceName).size();
        } else {
            playbook.resources.put(resourceName, new Resource(new CellReference(i, j)));
            size = 1;
        }
        if (min != null) {
            playbook.resources.get(resourceName).min = min;
        }
        if (max != null) {
            playbook.resources.get(resourceName).max = max;
        }
        if (movePenalty != null) {
            playbook.movePenalties.put(movePenalty, new CellReference(i, j));
        }
        if (statPenalty.size() > 0) {
            statPenalty.forEach(s -> playbook.stat_penalties.put(s, new CellReference(i, j)));
        }
        return resourceName + " x" + size;
    }
}
