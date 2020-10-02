package smtpclient;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
  };

  private String host;
  private int port;
  private Email email;
  private Socket socket;
  private int conTimeout;
  private int timeout;
  private States state;
  private String response;
  private boolean isDone;
  private BufferedReader in;
  private PrintWriter writer;

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
    writer = new PrintWriter(new DataOutputStream(socket.getOutputStream()));
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

  public boolean getIsDone() {
    return isDone;
  }


  public void sendEmail(Email email) throws IOException {
    String responseLine;
    String responseCode;

    do {

      responseLine = in.readLine();
      this.response += responseLine + "\n";

      System.out.println(responseLine);

      responseCode = responseLine.substring(0, 3);

      if (state == States.Init && responseCode.equals("220")) {
        writer.print("HELO Andrew\r\n");
        writer.flush();

        state = States.Auth;

      } else if (state == States.Auth && responseCode.equals("250")) {

        writer.print("AUTH LOGIN\r\n");
        writer.flush();

        state = States.User;

      } else if (state == States.User && responseCode.equals("334")) {

        writer.print(Base64.getEncoder().encodeToString(email.getFrom().getBytes()) + "\r\n");
        writer.flush();

        state = States.Pass;

      } else if (state == States.Pass && responseCode.equals("334")) {

        writer.print(Base64.getEncoder().encodeToString(email.getPassword().getBytes()) + "\r\n");
        writer.flush();

        state = States.Mail;

      } else if (state == States.Mail && responseCode.equals("235")) {

        writer.print("MAIL FROM: " + email.getFrom() + "\r\n");
        writer.flush();

        state = States.Rcpt;

      } else if (state == States.Rcpt && responseCode.equals("250")) {

        writer.print("RCPT TO: " + email.getTo() + "\r\n");
        writer.flush();

        state = States.Data;

      } else if (state == States.Data && responseCode.equals("250")) {

        writer.print("DATA\r\n");
        writer.flush();

        state = States.Body;

      } else if (state == States.Body && responseCode.equals("354")) {

        writer.print(email.getBody() + "\r\n.\r\n");
        writer.flush();

        state = States.Quit;

      } else if (state == States.Quit && responseCode.equals("250")) {

        writer.print("QUIT\r\n");
        writer.flush();

        state = States.Close;

      } else if (state == States.Close) {
        return;
      } else {
        state = States.Close;
        isDone = true;
        close();
      }
    } while (!isDone);



  }

}
