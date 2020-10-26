// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************

  /**
   * The interface type variable.  It allows the implementation of
   * the display method in the client.
   */
  ChatIF clientUI;
  private final String loginId;


  //Constructors ****************************************************

  /**
   * Constructs an instance of the chat client.
   *
   * @param loinId The user's login id.
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */

  public ChatClient(String loginId, String host, int port, ChatIF clientUI) {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginId = loginId;
    try {
      openConnection();
      sendToServer("#login " + loginId);
    } catch (IOException e) {
      System.out.println("Cannot open connection. Awaiting command.");
    }
  }


  //Instance methods ************************************************

  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI
   *
   * @param message The message from the UI.
   */
  public void handleMessageFromClientUI(String message)
  {
    if (message.charAt(0) == '#') {
      try {
        handleCommand(message);
      } catch (IOException e) {
        System.out.println("Error handling command");
      }
    } else {
      try
      {
        sendToServer(message);
      }
      catch(IOException e)
      {
        clientUI.display
          ("Could not send message to server.  Terminating client.");
        quit();
      }
    }
  }

  private void handleCommand(String message) throws IOException {
    String[] commandParts = message.split(" ");
    String command = commandParts[0];
    switch (command) {
      case "#quit":
        sendToServer(command);
        quit();
        break;
      case "#logoff":
        sendToServer(command);
        closeConnection();
        break;
      case "#sethost":
        if (isConnected()) {
          System.out.println("Can't set host while connected");
        } else {
          try {
            String host = commandParts[1];
            setHost(host);
            System.out.println("Host set to: " + host);
          } catch (IndexOutOfBoundsException e) {
            System.out.println ("Host not specified");
          }
          break;
        }
      case "#setport":
        if (isConnected()) {
          System.out.println("Can't set port while connected");
        } else {
          try {
            String port = commandParts[1];
            setPort(Integer.parseInt(port));
            System.out.println("Port set to: " + port);
          } catch (IndexOutOfBoundsException e) {
            System.out.println ("Port not specified");
          } catch (NumberFormatException e) {
            System.out.println("Port not a number");
          }
          break;
        }
      case "#login":
        if (isConnected()) {
          System.out.println("Can't login when connected");
        } else {
          openConnection();
          sendToServer("#login " + loginId);
        }
        break;
      case "#gethost":
        System.out.println(getHost());
        break;
      case "#getport":
        System.out.println(getPort());
        break;
      default:
        System.out.println("Command not recognized");
    }
  }

  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }

  @Override protected void connectionClosed() {
    System.out.println("Connection closed");
  }

  @Override protected void connectionException(Exception e) {
    System.out.println("SERVER SHUTTING DOWN! DISCONNECTING!\n"
        + "Abnormal termination of connection.");
  }
}
//End of ChatClient class
