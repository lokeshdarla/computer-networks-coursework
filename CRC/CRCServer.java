package CRC;

import java.io.*;
import java.net.*;
import java.util.zip.CRC32;

public class CRCServer {
  public static void main(String[] args) {
    int port = 3000;

    try (ServerSocket serverSocket = new ServerSocket(port)) {
      System.out.println("Server started. Waiting for a client...");

      Socket socket = serverSocket.accept();
      System.out.println("Client connected!");

      DataInputStream input = new DataInputStream(socket.getInputStream());
      DataOutputStream output = new DataOutputStream(socket.getOutputStream());

      String data = input.readUTF(); // Receive data from the client
      long clientCRC = input.readLong(); // Receive the CRC checksum

      // Calculate CRC checksum on the server
      CRC32 crc = new CRC32();
      crc.update(data.getBytes());
      long serverCRC = crc.getValue();

      System.out.println("Data received: " + data);
      System.out.println("Client CRC: " + clientCRC);
      System.out.println("Server CRC: " + serverCRC);

      // Verify checksum
      if (serverCRC == clientCRC) {
        output.writeUTF("Data received successfully. CRC matches!");
      } else {
        output.writeUTF("Data corruption detected. CRC mismatch!");
      }

      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
