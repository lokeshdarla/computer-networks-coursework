package CRC;

import java.io.*;
import java.net.*;

public class CRCClient {
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

      String data = "11010011101100"; // Input data
      String divisor = "1011"; // Generator

      // Append CRC to data
      int divisorLength = divisor.length();
      String appendedData = data + "0".repeat(divisorLength - 1);
      String remainder = mod2div(appendedData, divisor);
      String transmittedData = data + remainder;

      // Send correct data
      System.out.println("Sending correct data...");
      System.out.println("Transmitted data (correct): " + transmittedData);
      writer.println(transmittedData); // Send correct data
      writer.println(divisor); // Send divisor
      String response = reader.readLine(); // Receive response
      System.out.println("Server Response (correct data): " + response);

      // Introduce error in data
      String erroneousData = introduceError(transmittedData, 5); // Flip 5th bit
      System.out.println("Sending erroneous data...");
      System.out.println("Transmitted data (erroneous): " + erroneousData);
      writer.println(erroneousData); // Send incorrect data
      writer.println(divisor); // Send divisor
      response = reader.readLine(); // Receive response
      System.out.println("Server Response (erroneous data): " + response);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
