package org.example;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javatuples.Pair;

import java.net.*;
import java.io.*;
import java.util.Random;

public class RockPaperScissorsServer implements Runnable {
    private Socket clientSocket;
    private Integer port = 9999;
    private ServerSocket serverSocket;
    private InputStream is;
    private OutputStream os;

    private final ObjectMapper objectmapper = new ObjectMapper();

    private Gamestate gamestate = new Gamestate();

    public void StartServer() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Waiting");
        clientSocket = serverSocket.accept();

        is = clientSocket.getInputStream();
        os = clientSocket.getOutputStream();
    }

    private String makeMove() {
        String[] moves = {"Spock", "Echse", "Schere", "Stein", "Papier"};
        Random r = new Random();
        return moves[r.nextInt(5)];
    }


    private String readInputStream() throws IOException, InterruptedException {
        byte[] response = is.readNBytes(is.available());
        while(response.length == 0){
            response = is.readNBytes(is.available());
            Thread.sleep(100);
        }
        return new String(response);
    }

    private String sendGamestate(Gamestate gamestate) throws IOException, InterruptedException {
        byte[] responseBuffer = this.gamestate.asString().getBytes();
        os.write(responseBuffer);
        byte[] response = is.readNBytes(is.available());
        while(response.length == 0){
            response = is.readNBytes(is.available());
            Thread.sleep(10);
        }
        return new String(response);
    }

    @Override
    public void run() {
        try {
            StartServer();
            String response = readInputStream();
            System.out.println(response);
            while(!this.gamestate.getGameOver()) {
                if(response != null){
                    this.gamestate = this.objectmapper.readValue(response, Gamestate.class);
                    Pair<Integer, String> move = new Pair<>(1, makeMove());
                    this.gamestate = this.gamestate.updateGamestate(move);
                    response = sendGamestate(this.gamestate);
                }
                System.out.println(clientSocket.isConnected());
            }
            System.out.println("Gameover");
            is.close();
            os.close();
            serverSocket.close();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}