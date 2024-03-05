package org.example;

import java.io.IOException;

public class App {
  public static void main(String[] args) throws IOException {

      RockPaperScissorsServer server = new RockPaperScissorsServer();
      Thread serverThread = new Thread(server, "RPS_SERVER_THREAD");
      serverThread.start();

      RockPaperScissorsClient client = new RockPaperScissorsClient();
      Thread clientThread = new Thread(client, "RPS_CLIENT_THREAD");
      clientThread.start();

  }
}
