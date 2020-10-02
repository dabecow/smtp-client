package smtpclient;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;

public class Client {

  private enum States{
    Tls,
    HandShake,
    Auth,
    User,
    Pass,
    Rcpt,
    Mail,
    Data,
    Init,
    Body,
    Quit,
    Close
  }

  private final String host;
  private final int port;
//  private Email email;
  private final Socket socket;
  private final int conTimeout;
  private final int timeout;
  private States state;
  private String response;
  private boolean isDone;
  private final BufferedReader in;
  private final PrintWriter writer;

  public Client(String host, int port) throws IOException {
    this.host = host;
    this.port = port;
    this.conTimeout = 30000;
    this.timeout = 60000;
    isDone = false;
    state = States.Init;
    response = "Log:\n";
    socket = new Socket(this.host, this.port);
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
  }

  public String getResponse() {
    return response;
  }

  public void close() throws IOException {
    if (socket.isConnected()) {
      socket.close();
      state = States.Close;
    }
  }

  public void sendEmail(Email email) throws IOException {

    do {

      String responseLine = in.readLine();
      this.response += responseLine + "\n";

      System.out.println(responseLine);

      String responseCode = responseLine.substring(0, 3);

      if (state == States.Init && responseCode.equals("220")) {
        writer.println("HELO Andrew");

        state = States.Auth;

      } else if (state == States.Auth && responseCode.equals("250")) {

        writer.println("AUTH LOGIN");

        state = States.User;

      } else if (state == States.User && responseCode.equals("334")) {

        writer.println(Base64.getEncoder().encodeToString(email.getFrom().getBytes()));

        state = States.Pass;

      } else if (state == States.Pass && responseCode.equals("334")) {

        writer.println(Base64.getEncoder().encodeToString(email.getPassword().getBytes()));

        state = States.Mail;

      } else if (state == States.Mail && responseCode.equals("235")) {

        writer.println("MAIL FROM: " + email.getFrom());

        state = States.Rcpt;

      } else if (state == States.Rcpt && responseCode.equals("250")) {

        writer.println("RCPT TO: " + email.getFrom());
//        System.out.println("RCPT TO:" + email.getTo());

        state = States.Data;

      } else if (state == States.Data && responseCode.equals("250")) {

        writer.println("DATA");

        state = States.Body;

      } else if (state == States.Body && responseCode.equals("354")) {

        writer.print(email.getBody() + "\r\n.\r\n");

        state = States.Quit;

      } else if (state == States.Quit && responseCode.equals("250")) {

        writer.println("QUIT");

        state = States.Close;

      } else if (state == States.Close) {
        return;
      } else {
        writer.close();
        in.close();
        state = States.Close;
        isDone = true;
        close();
      }
    } while (!isDone);



  }

}
