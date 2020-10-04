package smtpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;

public class Client {

  private enum States{
    Auth,
    User,
    Pass,
    Rcpt,
    Mail,
    Data,
    Init,
    Body,
    Sent
  }

  private final String host;
  private final int port;
  private final Socket socket;
  private States state;
  private String response;
  private final BufferedReader in;
  private final PrintWriter writer;
  private final String username;
  private final String password;
  private String lastResponse;

  public Client(String host, int port, String username, String password) throws IOException {
    this.host = host;
    this.port = port;
    this.username = Base64.getEncoder().encodeToString(username.getBytes());
    this.password = Base64.getEncoder().encodeToString(password.getBytes());
    state = States.Init;
    response = "Log:\n";
    socket = new Socket(this.host, this.port);
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    writer = new PrintWriter(socket.getOutputStream(), true);
  }

  public String getResponse() {
    return response;
  }

  public void close() throws IOException {
    if (socket.isConnected()) {
      writer.println("QUIT");
      socket.close();
      writer.close();
      in.close();

    }
  }

  public String getLastResponse() {
    return lastResponse;
  }

  public void sendEmail(Email email) throws IOException {
    String   responseLine,
             responseCode;
    do {
      if (state == States.Sent) //нужно для повторной отправки сообщений
        if (lastResponse.startsWith("250")) {
          state = States.Mail;
          responseCode = "235";
        } else return;
      else {
        responseLine = in.readLine();
        this.response += responseLine + "\n";

        lastResponse = responseLine;

        responseCode = responseLine.substring(0, 3);
      }
      if (state == States.Init && responseCode.equals("220")) {
        writer.println("HELO localhost");

        state = States.Auth;

      } else if (state == States.Auth && responseCode.equals("250")) {

        writer.println("AUTH LOGIN");

        state = States.User;

      } else if (state == States.User && responseCode.equals("334")) {

        writer.println(username);

        state = States.Pass;

      } else if (state == States.Pass && responseCode.equals("334")) {

        writer.println(password);

        state = States.Mail;

      } else if (state == States.Mail && responseCode.equals("235")) {

        writer.println("MAIL FROM: <" + email.getFrom() + ">");

        state = States.Rcpt;

      } else if (state == States.Rcpt && responseCode.equals("250")) {

        writer.println("RCPT TO: <" + email.getTo() + ">");

        state = States.Data;

      } else if (state == States.Data && responseCode.equals("250")) {

        writer.println("DATA");

        state = States.Body;

      } else if (state == States.Body && responseCode.equals("354")) {

        writer.println(email.getContent());
        writer.println(".");
        lastResponse = in.readLine();
        response+=lastResponse;
        state = States.Sent;
        return;
      } else {
        return;
      }
    } while (!socket.isClosed());

  }

}
