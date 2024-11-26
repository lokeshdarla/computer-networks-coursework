package CRC;

import java.io.*;
import java.net.*;

public class CRCServer {
  public static String xor(String a, String b) {
    String result = "";
    for (int i = 1; i < a.length(); i++) {
      result += (a.charAt(i) == b.charAt(i)) ? "0" : "1";
    }
    return result;
  }

  public static String mod2div(String dividend, String divisor) {
    int pick = divisor.length();
    String tmp = dividend.substring(0, pick);

    while (pick < dividend.length()) {
      if (tmp.charAt(0) == '1') {
        tmp = xor(divisor, tmp) + dividend.charAt(pick);
      } else {
        tmp = xor("0".repeat(divisor.length()), tmp) + dividend.charAt(pick);
      }
      pick += 1;
    }

    if (tmp.charAt(0) == '1') {
      tmp = xor(divisor, tmp);
    } else {
      tmp = xor("0".repeat(divisor.length()), tmp);
    }

    return tmp;
  }

  public static void main(String[] args) {
    int port = 3000;

    try (ServerSocket serverSocket = new ServerSocket(port)) {
      System.out.println("Server is listening on port " + port);

      while (true) {
        Socket socket = serverSocket.accept();
        System.out.println("Client connected");

        InputStream input = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        OutputStream output = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);

        String data = reader.readLine(); // Receive transmitted data
        String divisor = reader.readLine(); // Receive divisor

        System.out.println("Received Data: " + data);
        System.out.println("Received Divisor: " + divisor);

        String remainder = mod2div(data, divisor);

        if (remainder.chars().allMatch(ch -> ch == '0')) {
          writer.println("Data is error-free");
        } else {
          writer.println("Error detected in data");
        }
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
