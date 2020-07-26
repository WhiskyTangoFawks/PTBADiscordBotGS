package com.whiskytangofox.ptbadiscordbot.DataObjects;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Responses.GetStatResponse;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Responses.SetResourceResponse;
import com.whiskytangofox.ptbadiscordbot.DataStructure.HashMapIgnoreCase;
import com.whiskytangofox.ptbadiscordbot.DataStructure.PatriciaTrieIgnoreCase;
import com.whiskytangofox.ptbadiscordbot.Exceptions.DiscordBotException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.KeyConflictException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.MissingValueException;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.Services.GameSheetService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Playbook {
    public String player;
    public String title;
    public HashMapIgnoreCase<CellReference> stats = new HashMapIgnoreCase<>();
    public HashMapIgnoreCase<CellReference> stat_penalties = new HashMapIgnoreCase<>();
    public HashMapIgnoreCase<Resource> resources = new HashMapIgnoreCase<>();
    public HashMapIgnoreCase<String> moveOverrideDice = new HashMapIgnoreCase<>();
    public PatriciaTrieIgnoreCase<Move> moves = new PatriciaTrieIgnoreCase<>();
    public HashMapIgnoreCase<CellReference> movePenalties = new HashMapIgnoreCase<>();
    public String tab;
    public GameSheetService sheet;

    public Playbook(GameSheetService sheet, String tab) {
        this.tab = tab;
        this.sheet = sheet;
    }

    public boolean isValid() {
        return player != null && !player.isBlank() && !player.contains("<");
    }

    public GetStatResponse getStat(String stat) throws IOException, DiscordBotException {
        String statRef = stats.get(stat).getCellRef();
        String penaltyRef = stat_penalties.get(stat).getCellRef();
        ArrayList<String> list = new ArrayList<>();
        list.add(statRef);
        list.add(penaltyRef);
        List<String> response = sheet.getValues(tab, list);

        Integer intValue;
        try {
            intValue = Integer.parseInt(response.get(0));
        } catch (NumberFormatException e) {
            throw new MissingValueException("Player: " + player + ", Stat:" + stat + ", returned Not A Number, please correct your sheet");
        }
        boolean isDebilitated = Boolean.parseBoolean(response.get(1));
        return new GetStatResponse(stat, intValue, isDebilitated);
    }

    public boolean isStat(String stat) {
        return stats.containsKey(stat);
    }

    public Collection<String> getRegisteredStatsForPlayer() {
        return stats.keySet();
    }

    public boolean isResource(String resource) {
        return resources.containsKey(resource);
    }

    public SetResourceResponse modifyResource(String resource, int mod) throws IOException {
        Resource res = resources.get(resource);
        List<CellReference> cells = res.getList();
        List<String> refs = cells.stream().map(CellReference::getCellRef).collect(Collectors.toList());
        List<String> values = sheet.getValues(tab, refs);
        int oldValue = 0;
        int newValue = 0;
        if (values.size() == 1 && isInteger(values.get(0))) {
            oldValue = Integer.parseInt(values.get(0));
            newValue = oldValue + mod;
            if (res.max != null && newValue > res.max) {
                newValue = res.max;
            } else if (res.min != null && newValue < res.min) {
                newValue = res.min;
            }
            values.set(0, String.valueOf(newValue));
            if (mod != 0) {
                sheet.setValues(tab, refs, values);
            }
        } else if (values.get(0).equalsIgnoreCase("true") || values.get(0).equalsIgnoreCase("false")) {
            oldValue = (int) values.stream().filter(v -> v.equalsIgnoreCase("true")).count();
            if (mod != 0) {
                //for a positive, iterate up, for a negative, iterate down
                boolean isModPos = mod > 0;
                int counter = 0;
                if (isModPos) {
                    for (int i = 0; counter < mod && i < values.size(); i++) {
                        if (values.get(i).equalsIgnoreCase("FALSE")) {
                            values.set(i, "TRUE");
                            counter++;
                        }
                    }
                } else { //modIsNegative
                    for (int i = values.size() - 1; counter < Math.abs(mod) && i >= 0; i--) {
                        if (values.get(i).equalsIgnoreCase("TRUE")) {
                            values.set(i, "FALSE");
                            counter++;
                        }
                    }
                }
                sheet.setValues(tab, refs, values);
            }
            newValue = (int) values.stream().filter(v -> v.equalsIgnoreCase("true")).count();
        }
        return new SetResourceResponse(resource, oldValue, mod, newValue);
    }

    public int getMovePenalty(String move) throws IOException {
        if (movePenalties.containsKey(move)) {
            return Integer.parseInt(sheet.getCellValue(tab, movePenalties.get(move).getCellRef()));
        }
        return 0;
    }

    public boolean isPlaybookMove(String move) throws KeyConflictException {
        return getMove(move) != null;
    }

    public Move getMove(String move) throws KeyConflictException {
        return moves.getClosestMatch(move);
    }

    public String getMoveDice(String move) {
        return moveOverrideDice.get(move);
    }

    boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
