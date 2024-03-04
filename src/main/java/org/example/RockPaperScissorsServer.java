package org.example;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javatuples.Pair;

import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class RockPaperScissorsServer implements Runnable {
    private Socket clientSocket;
    private Integer port = 9999;
    private ServerSocket serverSocket;
    private InputStream is;
    private OutputStream os;
    private Gamestate gamestate = null;

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

    private Gamestate onCall(Gamestate gamestate) {
        Pair<Integer, String> move = new Pair<>(1, makeMove());
        gamestate = gamestate.updateGamestate(move);
        gamestate = gamestate.updateGamestate(gamestate.getTurnNo() + 1);
        return gamestate;
    }

    private void handleCallback(ObjectMapper objectMapper, String data) throws IOException {
        this.gamestate = objectMapper.readValue(data, Gamestate.class);
        gamestate = onCall(gamestate);
        os.write(gamestate.gamestateToBytes(gamestate));
        os.flush();
    }

    @Override
    public void run() {
        try {
            StartServer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while (true) {
                int availableBytes = is.available();

                if (availableBytes > 0) {
                    byte[] buffer = new byte[availableBytes];
                    int bytesRead = is.read(buffer);
                    if (bytesRead > 0) {
                        String data = new String(buffer, 0, bytesRead);
                        ObjectMapper objectMapper = new ObjectMapper();

                        handleCallback(objectMapper, data);

                        byte[] responseBuffer = new byte[1024];
                        int responseBytesRead = is.read(responseBuffer);
                        if (responseBytesRead > 0) {
                            String response = new String(responseBuffer, 0, responseBytesRead);
                            handleCallback(objectMapper, new String(responseBuffer, 0, responseBytesRead));
                        }
                    }
                }

                Thread.sleep(10);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}