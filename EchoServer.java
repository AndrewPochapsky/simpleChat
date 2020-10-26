// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

import java.io.*;
import ocsf.server.*;
import common.*;

/**
 * This class overrides some of the methods in the abstract
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer
{
  //Class variables *************************************************

  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;

  private final ChatIF serverUI;
  private boolean serverClosed = false;

  //Constructors ****************************************************

  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) {
    super(port);
    this.serverUI = serverUI;
  }

  //Instance methods ************************************************

  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client) {
    String str = (String) msg;
    System.out.println("Message received: " + str + " from "
        + client.getInfo("loginId"));
    if (str.startsWith("#login")) {
      handleLogin(str, client);
    } else if (str.equals("#logoff") || str.equals("#quit")) {
      try {
        client.close();
      } catch (IOException e) {}
    }
    else {
      this.sendToAllClients(client.getInfo("loginId") + "> " + msg);
    }
  }

  private void handleLogin(String str, ConnectionToClient client) {
      String loginId = str.split(" ")[1];
      if (client.getInfo("loginId") != null) {
        System.out.println("Error, already logged in.");
        try {
          client.close();
        } catch (IOException e) {
          System.out.println("Error closing client connection.");
        }
      } else {
        client.setInfo("loginId", loginId);
        String loginMessage = "<" + loginId + "> has logged on";
        System.out.println(loginMessage);
        try {
          client.sendToClient(loginMessage);
        } catch (IOException e) {}
      }
  }

  public void handleCommand(String message) throws IOException {
    String[] commandParts = message.split(" ");
    String command = commandParts[0];
    switch (command) {
      case "#quit":
        close();
        System.exit(0);
        break;
      case "#stop":
        stopListening();
        break;
      case "#close":
        close();
        break;
      case "#setport":
        if (!serverClosed) {
          System.out.println("Server must be closed to set port");
          return;
        }
        try {
          String port = commandParts[1];
          setPort(Integer.parseInt(port));
          System.out.println("Port set to: " + port);
        } catch (IndexOutOfBoundsException e) {
          System.out.println("Port not specified");
        } catch (NumberFormatException e) {
          System.out.println("Port not a valid number");
        }
        break;
      case "#start":
        if (isListening()) {
          System.out.println("Server must be stopped to start");
          return;
        }
        listen();
        break;
      case "#getport":
        System.out.println(String.valueOf(getPort()));
        break;
      default:
        System.out.println("Command not recognized");
    }
  }

  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    serverClosed = false;
    System.out.println
      ("Server listening for connections on port " + getPort());
  }

  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
    this.sendToAllClients("WARNING - server has stopped listening to connections");
  }

  @Override protected void serverClosed() {
    serverClosed = true;
    System.out.println("Server has been closed");
  }

  @Override protected void clientConnected(ConnectionToClient client) {
    System.out.println("A new client is attempting to connect to the server");
  }

  @Override protected void clientDisconnected(ConnectionToClient client) {
    String message = "<" + client.getInfo("loginId") + "> has disconnected";
    System.out.println(message);
    this.sendToAllClients(message);
  }
}
//End of EchoServer class
