package smtpclient;

public class Email {
  private String from;
  private String to;
  private String subject;
  private String body;

  public Email(String from, String to, String subject, String body){
    this.from = from;
    this.to = to;
    this.subject = subject;
    this.body = body;
  }

  public String getBody() {
    return body;
  }

  public String getFrom() {
    return from;
  }

  public String getSubject() {
    return subject;
  }

  public String getTo() {
    return to;
  }

  public String getContent(){
    return "From " + this.from + "\r\nTo: " + this.to + "\r\nSubject: " + this.subject + "\r\n" + this.body;
  }

}
