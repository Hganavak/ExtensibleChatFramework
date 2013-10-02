import java.io.*;
import java.net.*;
import java.util.ArrayList;
public class ChatServerParallel {
	protected static final int AMOUNT_OF_SUBJECTS = 11;
	protected static ArrayList<Socket> connections = null;		
	protected static ArrayList<String>[] userList = null;
	
	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = null;
		boolean listening = true;
		connections = new ArrayList<Socket>();
		
		//Java doesn't like arrays of generics, but doesn't cause any problems
		//other than it generates a warning
		userList =  new ArrayList[AMOUNT_OF_SUBJECTS];	
		
		String[] subjects = new String[] {
				"CS101",
				"CS105",
				"CS210",
				"CS215",
				"CS230",
				"CS220",
				"CS225",
				"CS280",
				"MTH108",
				"BIO101",
				"CHM101"
		};
		for(int i = 0;i < userList.length; i++){
			userList[i] = new ArrayList<String>(); //initialize the arraylists, one for each subject, usernames that take
			userList[i].add(subjects[i]);			//the subject will be stored in the corrosponding arraylist
		}//the first element in each arraylist is the name of the subject that the arraylist represents
		
		
		
		try {
			serverSocket = new ServerSocket(41525);
		} catch(IOException e) {
			System.err.println("Port failure");
			System.exit(-1);
		}

		//Listen indefinitely, create a new thread for every connection
		while(listening) {
			Socket tempSocket = serverSocket.accept();
			Runnable chatRunnable = new ChatServerConnection(tempSocket);
			Thread chatThread = new Thread(chatRunnable);
			chatThread.start();			
			System.out.println("New ChatServerConnection created");
			connections.add(tempSocket);
			System.out.println("connections size: " + connections.size());
		}

		serverSocket.close();
	}
}
