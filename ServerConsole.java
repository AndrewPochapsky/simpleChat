import java.io.*;
import java.util.Scanner;

import common.*;

public class ServerConsole implements ChatIF {

  private final static int DEFAULT_PORT = 5555;

  private final EchoServer server;
  private final Scanner fromConsole;

  public ServerConsole(int port) {
    server = new EchoServer(port, this);
    fromConsole = new Scanner(System.in);

    try
    {
      server.listen(); //Start listening for connections
    }
    catch (Exception ex)
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }

  @Override public void display(String message) {
    server.sendToAllClients("SERVER MSG>" + message);
  }

  private void accept() {
    try
    {
      String message;
      while (true)
      {
        message = fromConsole.nextLine();
        display(message);
      }
    }
    catch (Exception ex)
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }

  public static void main(String[] args)
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }

    ServerConsole console = new ServerConsole(port);
    console.accept();
  }
}
