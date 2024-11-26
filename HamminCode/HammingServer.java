package HamminCode;

import java.io.*;
import java.net.*;

public class HammingServer {
  public static int[] calculateSyndrome(String data) {
    int n = data.length();
    int r = (int) Math.ceil(Math.log(n) / Math.log(2));
    int[] syndrome = new int[r];

    for (int i = 0; i < r; i++) {
      int parity = 0;
      int position = (1 << i); // Position of parity bit (2^i)

      for (int j = 1; j <= n; j++) {
        if ((j & position) != 0) { // Check if the j-th bit contributes to the current parity
          parity ^= (data.charAt(n - j) - '0'); // Calculate parity (even parity)
        }
      }
      syndrome[i] = parity;
    }
    return syndrome;
  }

  public static void main(String[] args) {
    int port = 3000;

    try (ServerSocket serverSocket = new ServerSocket(port)) {
      System.out.println("Server is listening on port " + port);

      while (true) {
        Socket socket = serverSocket.accept();
        System.out.println("Client connected");

        try (
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true)) {
          // Read transmitted data
          String receivedData = reader.readLine();
          System.out.println("Received Data: " + receivedData);

          // Calculate syndrome
          int[] syndrome = calculateSyndrome(receivedData);
          int errorPosition = 0;

          for (int i = 0; i < syndrome.length; i++) {
            errorPosition += syndrome[i] * (1 << i);
          }

          // Send results back to the client
          if (errorPosition == 0) {
            writer.println("Data is error-free");
          } else {
            System.out.println("Error detected at position: " + errorPosition);
            writer.println("Error detected at position: " + errorPosition);
          }
        } catch (Exception ex) {
          System.err.println("Error handling client: " + ex.getMessage());
        }
      }
    } catch (IOException ex) {
      System.err.println("Server exception: " + ex.getMessage());
      ex.printStackTrace();
    }
  }
}
