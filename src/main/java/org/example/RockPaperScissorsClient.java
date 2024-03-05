package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class RockPaperScissorsClient implements Runnable {
    Gamestate gamestate = new Gamestate();

    private String clientIP = "127.0.0.1";
    private Integer port = 9999;
    private Socket clientSocket;
    private InputStream is;
    private Referee gameReferee = new Referee();
    private final ObjectMapper objectmapper = new ObjectMapper();
    private OutputStream os;


    public void startConnection(String clientIP, Integer socketNr) throws IOException {
        clientSocket = new Socket(clientIP, socketNr) ;
        is = clientSocket.getInputStream();
        os = clientSocket.getOutputStream();
    }

    public String sendGamestate(@org.jetbrains.annotations.NotNull Gamestate gamestate) throws IOException, InterruptedException {
        byte[] bytes = gamestate.gamestateToBytes(gamestate);
        os.write(bytes);
        byte[] response = is.readNBytes(is.available());
        System.out.println("Servers Turn...");
        while(response.length == 0){
            response = is.readNBytes(is.available());
            Thread.sleep(10);
            System.out.println("Server is choosing");
        }
        return new String(response);
    }

    private String makeMove(){
        String[] moves = {"Spock", "Echse", "Schere", "Stein", "Papier"};
        for(int i = 0; i<5; i++){
            System.out.println("[ " + i + " ] " + moves[i]);
        }
        Scanner sc = new Scanner(System.in);
        String input = sc.next();
        if(!gameReferee.isValidInput(input)){
            System.out.println("Invalid input, try again! ");
            return moves[Integer.parseInt(makeMove())];
        }
        return moves[Integer.parseInt(input)];
    }


    //TODO Input in RUN Method because invalid Input is Handled incorrectly
    @Override
    public void run() {
        try {
            startConnection(clientIP, port);
            while(!this.gamestate.getGameOver()) {
                Pair<Integer, String> firstMove = new Pair<>(0, makeMove());
                gamestate = this.gamestate.updateGamestate(firstMove);
                String response = sendGamestate(gamestate);
                if(response != null){
                    this.gamestate = objectmapper.readValue(response, Gamestate.class);
                    gamestate = gameReferee.determineWinner(gamestate);
                }
                System.out.println(clientSocket.isConnected());
            }
            System.out.println("Gameover");
            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
