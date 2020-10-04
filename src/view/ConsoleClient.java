package view;

import java.io.IOException;
import java.util.Scanner;
import smtpclient.Client;
import smtpclient.Email;

public class ConsoleClient implements IConsoleClient{

  public static final String ANSI_RED = "\u001B[31m";
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String ANSI_PURPLE = "\u001B[35m";
  public static final String ANSI_CYAN = "\u001B[36m";
  public static final String ANSI_RESET = "\u001B[0m";

  private Client client;
  private final Scanner scanner;

  public ConsoleClient(){
    scanner = new Scanner(System.in);
  }

  public boolean connectToServer(){

    String host, username, password;

    System.out.println(ANSI_CYAN + "   _____ __  _____________          ___            __ \n"
        + "  / ___//  |/  /_  __/ __ \\   _____/ (_)__  ____  / /_\n"
        + "  \\__ \\/ /|_/ / / / / /_/ /  / ___/ / / _ \\/ __ \\/ __/\n"
        + " ___/ / /  / / / / / ____/  / /__/ / /  __/ / / / /_  \n"
        + "/____/_/  /_/ /_/ /_/       \\___/_/_/\\___/_/ /_/\\__/  \n"
        + "                                                      \n"
        + "\n\nConnect to the SMTP server (TSL is not supported).");
    System.out.print("Enter host\n>>>");
    host = scanner.nextLine();

    System.out.print("Enter your username\n>>>");
    username = scanner.nextLine();

    System.out.print("Enter your password\n>>>");
    password = scanner.nextLine();

    System.out.println("Standard port is 25.");

    try {
      this.client = new Client(host, 25, username, password);

    } catch (IOException e) {
      System.out.print(ANSI_RED + "\nIO error. Try again.\n" + ANSI_RESET);

      return false;
    }

    System.out.println(ANSI_GREEN + "Success – the socket is ready" + ANSI_RESET);
    return true;
  }

  private void printMenu(){
    System.out.print(ANSI_PURPLE + "\n\n1. Enter new message\n2. Choose new server\n3. Exit\n\n>>>");
  }

  public void runMessageInterface(){

    while (true){

      printMenu();

      try {
        switch (scanner.nextInt()){
          case 1:{

            String  name,
                from, to,
                subject, body;
            scanner.nextLine(); //to fix the bug with \n in nextInt
            System.out.print("Enter your name\n>>>");
            name = scanner.nextLine();
            System.out.print("Enter your email address\n>>>");
            from = scanner.nextLine();
            System.out.print("Enter recipient's email address\n>>>");
            to = scanner.nextLine();
            System.out.print("Enter the subject of the message\n>>>");
            subject = scanner.nextLine();
            System.out.print("Enter your message:\n>>>");
            body = scanner.nextLine();

            Email email = new Email(name, from, to, subject, body);

            try {
              client.sendEmail(email);
            } catch (IOException e) {
              e.printStackTrace();
              System.out.println("Couldn't send the message: IO exception.");
            }

            if (client.getLastResponse().startsWith("250")){
              System.out.println(ANSI_GREEN + "Success" + ANSI_RESET);
            } else {
              System.out.println(ANSI_RED + "\nFailed\n" + ANSI_RESET);
              System.out.println(client.getResponse());
            }
            break;
          }
          case 2:
            connectToServer();
            break;
          case 3:
            try {
              client.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
            return;

          default:
            System.out.println("Wrong input, please, try again.");
          break;
        }

      } catch (Exception e){
        scanner.nextLine(); //to fix the bug with \n in scanner.nextInt()
        System.out.println("Please, try again");
      }


    }
  }
}
