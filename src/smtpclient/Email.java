package smtpclient;

public class Email {
  private String from;
  private String to;
  private String subject;
  private String body;
  private String password;

  public Email(String from, String to, String subject, String body, String password){
    this.from = from;
    this.to = to;
    this.subject = subject;
    this.body = body;
    this.password = password;
  }

  public String getBody() {
    return body;
  }

  public String getFrom() {
    return from;
  }

  public String getPassword() {
    return password;
  }

  public String getSubject() {
    return subject;
  }

  public String getTo() {
    return to;
  }

  public String getContent(){
    return "From " + this.from + "\nTo: " + this.to + "\nSubject: " + this.subject + "\n\n" + this.body;
  }

}
