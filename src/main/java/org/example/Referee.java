package org.example;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Referee {
    private final HashMap <String, List<String>> rules = new HashMap<>();


    public boolean isValidInput(String in){
        int input = 0;
        try{
            input = Integer.parseInt(in);
        }catch (AssertionError e){
            return false;
        }
        return input <= 4;
    }

    Referee(){
        rules.put("Spock", Arrays.asList("Papier", "Echse"));
        rules.put("Schere", Arrays.asList("Stein", "Spock"));
        rules.put("Stein", Arrays.asList("Papier", "Spock"));
        rules.put("Papier", Arrays.asList("Echse", "Schere"));
        rules.put("Echse", Arrays.asList("Stein", "Schere"));
    }
    public Gamestate determineWinner(Gamestate gamestate){
        String clientMove = gamestate.getMoves()[0];
        String serverMove = gamestate.getMoves()[1];

        //Draw
        if(serverMove.equals(clientMove)) return gamestate;

        if(rules.get(clientMove).contains(serverMove)){
            System.out.println("You won this Turn");
            gamestate = gamestate.updateGamestate("Server");
        }else {
            System.out.println("You Lost this turn");
            gamestate = gamestate.updateGamestate("Client");
        };

        if(gamestate.getTurnNo() >= 5){
            Object hasWon = wonMatch(gamestate);
            if(hasWon != null) gamestate = gamestate.updateGamestate(wonMatch(gamestate));
        }
        gamestate = gamestate.updateGamestate(gamestate.getTurnNo()+1);
        return gamestate;
    }

    private Boolean wonMatch(Gamestate gamestate) {
        List<String> result;
        if ((result = gamestate.getWinner().stream().filter(entry -> Objects.equals(entry, "Client"))
                .collect(Collectors.toList())).size() == 5) {
            System.out.println("You won the Game");
            return true;
        } else if ((result = gamestate.getWinner().stream().filter(entry -> Objects.equals(entry, "Server"))
                .collect(Collectors.toList())).size() == 5) {
            System.out.println("Server won the Game, Sorry");
            return false;
        }
        return null;
    }


}
