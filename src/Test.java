import java.io.IOException;
import smtpclient.Client;
import smtpclient.Email;

public class Test {

  public static void main(String[] args) throws IOException {
    Email email = new Email("cio01@ostu.ru", "cio01@ostu.ru", "test", "Hello test message", "cio01p");
    Client client = new Client("mail.oreluniver.ru", 25);
    while (!client.getIsDone())
      client.sendEmail(email);
    System.out.println(client.getResponse());
  }
}