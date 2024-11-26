package SlidingWindow;

import java.io.*;
import java.net.*;
import java.util.*;

public class SlidingWindowSender {
  public static void main(String[] args) throws IOException {
    String hostname = "localhost";
    int port = 3000;
    int windowSize = 4; // Window size (N)
    int totalFrames = 10; // Total number of frames to send

    try (Socket socket = new Socket(hostname, port)) {
      PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      int base = 0; // First frame in the window
      int nextSeqNum = 0; // Next frame to send
      List<Integer> ackedFrames = new ArrayList<>();

      while (base < totalFrames) {
        // Send frames within the window
        while (nextSeqNum < base + windowSize && nextSeqNum < totalFrames) {
          System.out.println("Sending frame: " + nextSeqNum);
          writer.println(nextSeqNum);
          nextSeqNum++;
        }

        // Wait for acknowledgment
        try {
          socket.setSoTimeout(2000); // 2 seconds timeout for ACK
          String ack = reader.readLine();
          int ackNum = Integer.parseInt(ack);

          System.out.println("Acknowledgment received for frame: " + ackNum);
          ackedFrames.add(ackNum);

          // Slide the window
          while (ackedFrames.contains(base)) {
            base++;
          }
        } catch (SocketTimeoutException e) {
          System.out.println("Timeout! Resending frames from base: " + base);
          nextSeqNum = base; // Resend from the first unacknowledged frame
        }
      }

      System.out.println("All frames sent and acknowledged!");
    }
  }
}
