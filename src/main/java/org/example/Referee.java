package org.example;

import java.util.*;

public class Referee {
    private HashMap <String, List<String>> rules = new HashMap<>();

    Referee(){
        rules.put("Spock", Arrays.asList("Papier", "Echse"));
        rules.put("Schere", Arrays.asList("Stein", "Spock"));
        rules.put("Stein", Arrays.asList("Papier", "Spock"));
        rules.put("Papier", Arrays.asList("Echse", "Schere"));
        rules.put("Lizard", Arrays.asList("Stein", "Schere"));
    }
    public Gamestate winnerRound(Gamestate gamestate, String[] moves){
        String clientMove = moves[0];
        String serverMove = moves[1];
        String winner = "";
        if(rules.get(clientMove).contains(serverMove)){
            gamestate = gamestate.updateGamestate("Server");
        }else gamestate = gamestate.updateGamestate("Client");

        return gamestate;
    }

    private Boolean wonMatch(Gamestate gamestate){
        return true;
    }


}
