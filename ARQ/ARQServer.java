package ARQ;

import java.io.*;
import java.net.*;

public class ARQServer {
  public static void main(String[] args) {
    int port = 3000;

    try (ServerSocket serverSocket = new ServerSocket(port)) {
      System.out.println("ARQ Server is listening on port " + port);

      while (true) {
        Socket socket = serverSocket.accept();
        System.out.println("Client connected");

        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
          String receivedFrame;
          boolean lastFrameAcked = true; // Initial state

          while ((receivedFrame = reader.readLine()) != null) {
            System.out.println("Received Frame: " + receivedFrame);

            // Simulate error detection (e.g., randomly fail or accept the frame)
            boolean frameAccepted = Math.random() > 0.2; // 80% success rate
            if (frameAccepted) {
              System.out.println("Frame accepted. Sending ACK.");
              writer.println("ACK");
              lastFrameAcked = true;
            } else {
              System.out.println("Frame corrupted. No ACK sent.");
              lastFrameAcked = false;
            }
          }
        }
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
