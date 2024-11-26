package HamminCode;

import java.io.*;
import java.net.*;

public class HammingClient {
  public static String generateHammingCode(String data) {
    int m = data.length();
    int r = 0;

    // Calculate number of parity bits needed
    while ((m + r + 1) > (1 << r)) {
      r++;
    }

    int n = m + r;
    char[] hammingCode = new char[n];
    int dataIndex = 0;

    // Insert data bits and leave space for parity bits
    for (int i = 1; i <= n; i++) {
      if ((i & (i - 1)) == 0) { // Power of 2
        hammingCode[n - i] = '0'; // Placeholder for parity bits
      } else {
        hammingCode[n - i] = data.charAt(dataIndex++);
      }
    }

    // Calculate parity bits
    for (int i = 0; i < r; i++) {
      int parity = 0;
      int position = (1 << i);

      for (int j = 1; j <= n; j++) {
        if ((j & position) != 0) {
          parity ^= (hammingCode[n - j] - '0');
        }
      }
      hammingCode[n - position] = (char) (parity + '0'); // Set parity bit
    }

    return new String(hammingCode);
  }

  public static String introduceError(String data, int errorIndex) {
    char[] dataChars = data.toCharArray();
    dataChars[errorIndex] = (dataChars[errorIndex] == '0') ? '1' : '0';
    return new String(dataChars);
  }

  public static void main(String[] args) {
    String hostname = "localhost";
    int port = 3000;

    try (Socket socket = new Socket(hostname, port)) {
      OutputStream output = socket.getOutputStream();
      PrintWriter writer = new PrintWriter(output, true);

      InputStream input = socket.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(input));

      String data = "1011"; // Original data

      // Generate Hamming code
      String hammingCode = generateHammingCode(data);
      System.out.println("Generated Hamming Code: " + hammingCode);

      // Send correct data first
      System.out.println("Sending correct data...");
      writer.println(hammingCode);
      System.out.println("Server Response: " + reader.readLine());

      // Introduce error and send erroneous data
      // String erroneousData = introduceError(hammingCode, 5); // Flip 5th bit
      // System.out.println("Sending erroneous data: " + erroneousData);
      // writer.println(erroneousData);

      // // Read server response
      // String serverResponse = reader.readLine();
      // System.out.println("Server Response (Erroneous Data): " + serverResponse);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
