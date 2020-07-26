package com.whiskytangofox.ptbadiscordbot.Services;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Move;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Responses.GetStatResponse;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Responses.SetResourceResponse;
import com.whiskytangofox.ptbadiscordbot.DataStructure.HashMapIgnoreCase;
import com.whiskytangofox.ptbadiscordbot.Exceptions.DiscordBotException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.KeyConflictException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.PlayerNotFoundException;

import java.io.IOException;
import java.util.Collection;

public class PlaybookService {

    public final GameSheetService sheet;
    public HashMapIgnoreCase<Playbook> playbooks = new HashMapIgnoreCase<>();

    public PlaybookService(GameSheetService sheet) {
        this.sheet = sheet;
    }

    private Playbook getPlaybook(String player) throws PlayerNotFoundException {
        if (!playbooks.containsKey(player)) {
            throw new PlayerNotFoundException("No playbook found registered to " + player);
        }
        return playbooks.get(player);
    }

    public GetStatResponse getStat(String author, String stat) throws IOException, DiscordBotException {
        GetStatResponse response = getPlaybook(author).getStat(stat);
        response.debilityTag = sheet.settings.getProperty("stat_debility_tag");
        return response;
    }

    public boolean isStat(String player, String stat) {
        return playbooks.get(player).isStat(stat);
    }

    public Collection<String> getRegisteredStatsForPlayer(String player) {
        return playbooks.get(player).getRegisteredStatsForPlayer();
    }

    public boolean isResource(String player, String resource) throws PlayerNotFoundException {
        return getPlaybook(player).isResource(resource);
    }

    public SetResourceResponse modifyResource(String player, String resource, int mod) throws PlayerNotFoundException, IOException {
        return getPlaybook(player).modifyResource(resource, mod);
    }

    public int getMovePenalty(String player, String move) throws PlayerNotFoundException, IOException {
        return getPlaybook(player).getMovePenalty(move);
    }

    public boolean isPlaybookMove(String player, String move) throws KeyConflictException {
        if (playbooks.get(player) == null) {
            return false;
        }
        return getMove(player, move) != null;
    }

    public Move getMove(String player, String move) throws KeyConflictException {
        return playbooks.get(player).getMove(move);
    }

    public String getMoveDice(String player, String move) {
        return playbooks.get(player).getMoveDice(move);
    }

    public void put(Playbook book) {
        playbooks.put(book.player, book);
    }

}
