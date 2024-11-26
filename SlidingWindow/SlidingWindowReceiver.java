package SlidingWindow;

import java.io.*;
import java.net.*;
import java.util.*;

public class SlidingWindowReceiver {
  public static void main(String[] args) throws IOException {
    int port = 3000;

    try (ServerSocket serverSocket = new ServerSocket(port)) {
      System.out.println("Receiver is listening on port " + port);

      while (true) {
        Socket socket = serverSocket.accept();
        System.out.println("Sender connected");

        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
          int expectedFrame = 0; // Expected frame sequence number
          Set<Integer> receivedFrames = new HashSet<>();

          String frame;
          while ((frame = reader.readLine()) != null) {
            int frameNum = Integer.parseInt(frame);
            System.out.println("Received frame: " + frameNum);

            if (frameNum == expectedFrame) {
              System.out.println("Frame " + frameNum + " accepted.");
              writer.println(frameNum); // Send acknowledgment
              expectedFrame++;
            } else if (frameNum > expectedFrame) {
              System.out.println("Frame " + frameNum + " out of order. Expecting: " + expectedFrame);
            } else {
              System.out.println("Duplicate frame received: " + frameNum);
              writer.println(frameNum); // Send acknowledgment for duplicate
            }
          }
        }
      }
    }
  }
}
