package ARQ;

import java.io.*;
import java.net.*;

public class ARQClient {
  public static void main(String[] args) {
    String hostname = "localhost";
    int port = 3000;

    try (Socket socket = new Socket(hostname, port)) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

      String[] frames = { "Frame1", "Frame2", "Frame3" }; // Example frames to send
      int currentFrame = 0;

      while (currentFrame < frames.length) {
        System.out.println("Sending: " + frames[currentFrame]);
        writer.println(frames[currentFrame]);

        // Wait for acknowledgment with timeout
        socket.setSoTimeout(2000); // 2-second timeout
        try {
          String ack = reader.readLine();
          if ("ACK".equals(ack)) {
            System.out.println("Acknowledgment received for: " + frames[currentFrame]);
            currentFrame++;
          }
        } catch (SocketTimeoutException e) {
          System.out.println("Timeout! Resending: " + frames[currentFrame]);
        }
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
