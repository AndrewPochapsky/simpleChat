import java.io.*;
import java.util.Scanner;

import common.*;

public class ServerConsole implements ChatIF {

  private final static int DEFAULT_PORT = 5555;

  private final EchoServer server;
  private final Scanner fromConsole;
  private boolean serverClosed = false;

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
    if (message.startsWith("#")) {
      try {
        handleCommand(message);
      } catch (IOException e) {
        System.out.println("Error executing command");
      }
    } else {
      server.sendToAllClients("SERVER MSG> " + message);
    }
  }

  private void handleCommand(String message) throws IOException {
    String[] commandParts = message.split(" ");
    String command = commandParts[0];
    switch (command) {
      case "#quit":
        server.close();
        System.exit(0);
        break;
      case "#stop":
        server.stopListening();
        break;
      case "#close":
        serverClosed = true;
        server.close();
        break;
      case "#setport":
        if (!serverClosed) {
          System.out.println("Server must be closed to set port");
          return;
        }
        try {
          String port = commandParts[1];
          server.setPort(Integer.parseInt(port));
          System.out.println("Port set");
        } catch (IndexOutOfBoundsException e) {
          System.out.println("Port not specified");
        } catch (NumberFormatException e) {
          System.out.println("Port not a valid number");
        }
        break;
      case "#start":
        if (server.isListening()) {
          System.out.println("Server must be stopped to start");
          return;
        }
        serverClosed = false;
        server.listen();
        break;
      case "#getport":
        System.out.println(server.getPort());
        break;
      default:
        System.out.println("Command not recognized");
    }
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
