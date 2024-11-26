package CRC;

import java.io.*;
import java.net.*;
import java.util.zip.CRC32;

public class CRCClient {
  public static void main(String[] args) {
    String host = "localhost";
    int port = 3000;
    String data = "Hello, CRC Server!";

    try (Socket socket = new Socket(host, port)) {
      System.out.println("Connected to the server.");

      DataOutputStream output = new DataOutputStream(socket.getOutputStream());
      DataInputStream input = new DataInputStream(socket.getInputStream());

      // Calculate CRC checksum
      CRC32 crc = new CRC32();
      crc.update(data.getBytes());
      long crcValue = crc.getValue();

      // Send data and checksum to the server
      output.writeUTF(data);
      output.writeLong(crcValue);

      // Receive server response
      String response = input.readUTF();
      System.out.println("Server response: " + response);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
