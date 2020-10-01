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
  private DataOutputStream out;

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
    PrintWriter out = new PrintWriter(new DataOutputStream(socket.getOutputStream()));
  }

  public void report(String message){
    System.out.println(message);
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


  public void sendEmail(Email email){
    String responseLine;
    String responseCode;

    try {
//      do {
        responseLine = in.readLine();
        this.response+=responseLine + "\n";

        System.out.println(responseLine);
//      } while (in.readBoolean() && (responseLine.charAt(3) != ' '));

      responseCode = responseLine.substring(0, 3);
      int a = 2;
      if (state == States.Init && responseCode.equals("220")){
        out.writeUTF("EHLO localhost\r\n");
        out.flush();

        state = States.HandShake;
      } else if (state == States.HandShake && responseCode.equals("250")) {
        out.writeUTF("AUTH LOGIN\r\n");
        out.flush();

        state = States.User;
      } else if (state == States.User && responseCode.equals("334")){
        out.writeUTF(Base64.getEncoder().encodeToString(email.getFrom().getBytes()) + "\r\n");
        out.flush();

        state = States.Pass;
      } else if (state == States.Pass && responseCode.equals("334")){
        out.writeUTF(Base64.getEncoder().encodeToString(email.getPassword().getBytes()) + "\r\n");
        out.flush();

        state = States.Mail;
      } else if (state == States.Mail && responseCode.equals("235")){
        out.writeUTF("MAIL FROM:<" + email.getFrom() + ">\r\n");
        out.flush();

        state = States.Rcpt;
      } else if (state == States.Rcpt && responseCode.equals("250")){
        out.writeUTF("RCPT TO:<" + email.getTo() + ">\r\n");
        out.flush();

        state = States.Data;
      } else if (state == States.Data && responseCode.equals("250")){
        out.writeUTF("DATA\r\n");
        out.flush();

        state = States.Body;
      } else if (state == States.Body && responseCode.equals("354")){
        out.writeUTF(email.getBody() + "\r\n");
        out.flush();

        state = States.Quit;
      } else if (state == States.Quit && responseCode.equals("250")){
        out.writeUTF("QUIT\r\n");
        out.flush();

        state = States.Close;
      } else if (state == States.Close){
        return;
      } else {
        state = States.Close;
        isDone = true;
      }

    } catch (IOException e) {
      e.printStackTrace();
    }


  }

}
