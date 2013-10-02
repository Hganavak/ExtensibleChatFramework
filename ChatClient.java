import java.io.*;
import java.net.*;

import javax.swing.JTextArea;

public class ChatClient extends ChatFrame {
	
	DataOutputStream streamOut = null;
	DataInputStream console = null;
	String serverResponse;
	Socket clientSocket;
	DataOutputStream streamOut1;
	

	/**
	 * Method to initiate the connection with server, must be called before the other methods
	 */
	public void connect() {	
		try {
			clientSocket = new Socket("localhost", 41525);
			streamOut = new DataOutputStream(clientSocket.getOutputStream());
			Runnable chatReceiver = new ChatClientReceiver(clientSocket);
			Thread receiveThread = new Thread(chatReceiver);	//intialise and start the thread that will check/display messages from server
			receiveThread.start();	
			
		} catch(Exception e) {
			System.out.println("An error occured when setting up the connection with server");
		}
		
	}
	
	/**
	 * Method to send a message to the server takes the format:
	 * <code>subject|upi123:  message</code>
	 * @param msg
	 */
	public void sendMessage(String msg) {
		try {
			streamOut.writeUTF(msg);
			streamOut.flush();
			
		} catch(Exception e) {
			System.out.println("An error occured in sending a message");
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to close the current connection and streams
	 */
	public void close() {
		try {
		streamOut.writeUTF("/quit");
		streamOut.flush();
		clientSocket.close(); //Close the socket
		streamOut.close();
		} catch(Exception e) {
			System.out.println("An error occured closing the connection");
			e.printStackTrace();
		}
	}

}
