package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.javatuples.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class RockPaperScissorsClient implements Runnable {
    Gamestate gamestate;

    private String clientIP = "127.0.0.1";
    private Integer port = 9999;
    private Socket clientSocket;
    private InputStream is;
    private OutputStream os;


    public void startConnection(String clientIP, Integer socketNr) throws IOException {
        clientSocket = new Socket(clientIP, socketNr) ;
        is = clientSocket.getInputStream();
        os = clientSocket.getOutputStream();
        gamestate = new Gamestate();
    }

    public byte[] sendGamestate(Gamestate gamestate) throws IOException {
        byte[] bytes = gamestate.gamestateToBytes(gamestate);
        assert bytes != null;
        os.write(bytes);
        return is.readAllBytes();
    }


    private String makeMove(){
        String[] moves = {"Spock", "Echse", "Schere", "Stein", "Papier"};
        for(int i = 0; i<5; i++){
            System.out.println("[ " + i + " ] " + moves[i]);
        }
        Scanner input = new Scanner(System.in);
        return moves[Integer.parseInt(input.next())];
    }

    @Override
    public void run() {
        try {
            startConnection(clientIP, port);
            Pair<Integer, String> firstMove = new Pair<>(0, makeMove());
            gamestate = this.gamestate.updateGamestate(firstMove);
            byte[] gs;
            while(this.gamestate.getGameOver() == Boolean.FALSE){
                int availableBytes = is.available();

                if (availableBytes > 0) {
                    byte[] buffer = new byte[availableBytes];
                    int bytesRead = is.read(buffer);
                    if (bytesRead > 0) {
                        String data = new String(buffer, 0, bytesRead);
                        ObjectMapper objectMapper = new ObjectMapper();
                        this.gamestate = objectMapper.readValue(data, Gamestate.class);
                        Pair<Integer, String> newMove = new Pair<>(0, makeMove());
                        gamestate = this.gamestate.updateGamestate(newMove);
                        gs = sendGamestate(gamestate);
                    }
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
