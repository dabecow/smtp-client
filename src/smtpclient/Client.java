package smtpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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

  public Client(String host, int port){
    this.host = host;
    this.port = port;
    this.conTimeout = 30000;
    this.timeout = 60000;
  }

  public void report(String message){
    System.out.println(message);
  }

  public void close() throws IOException {
    if (socket.isConnected()) {
      socket.close();
      state = States.Close;
    }
  }

  public void sendEmail(Email email){
    state = States.Init;

    try {

      socket = new Socket(this.host, this.port);
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

      BufferedReader reader = new BufferedReader(new InputStreamReader(input));
      
      do {
        response = socket.getInputStream();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }


  }

}
