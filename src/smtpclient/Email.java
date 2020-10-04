package smtpclient;

public class Email {
  private String name;
  private String from;
  private String to;
  private String subject;
  private String body;

  public Email(String name, String from, String to, String subject, String body){
    this.name = name;
    this.from = name + " <" + from + ">";
    this.to = to;
    this.subject = subject;
    this.body = body;
  }

  public String getName() {
    return name;
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
    return "From: " + from + "\r\nTo: " + to + "\r\nSubject: " + subject + "\r\n\n" + body;
  }

}
