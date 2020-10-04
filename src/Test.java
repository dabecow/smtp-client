import java.io.IOException;
import smtpclient.Client;
import smtpclient.Email;
import view.ConsoleClient;
import view.IConsoleClient;

public class Test {

  public static void main(String[] args) throws IOException {

    IConsoleClient iConsoleClient = new ConsoleClient();

    boolean isConnected;
    do {
      isConnected = iConsoleClient.connectToServer();
    } while (!isConnected);

    iConsoleClient.runMessageInterface();
  }
}