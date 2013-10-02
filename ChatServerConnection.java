import java.io.*;
import java.net.*;
public class ChatServerConnection extends ChatServerParallel implements Runnable {

	private Socket socket = null;
	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;
	private PrintWriter pw	= null;
	private String name = "";

	public ChatServerConnection(Socket socket) {
		this.socket = socket;
	}		

	public void run() {
		try {
			System.out.println("This threads connection: " + socket.getRemoteSocketAddress()); //Output the connection address
	
			//Initialise I/o
			DataOutputStream streamOut = new DataOutputStream(socket.getOutputStream()); 
			DataInputStream streamIn = new DataInputStream(socket.getInputStream());
			this.pw = new PrintWriter(streamOut);

		
			//Read until they type 'quit'
			String inputLine = "";
			while((inputLine = streamIn.readUTF()) != null) {
				System.out.println("Received message: " + inputLine);			
								
				if(inputLine.substring(0, inputLine.indexOf('|')).equals("joinMessage")){  //check if a new user has joined chat
				
					
					joinMethod(inputLine); //places their name in the corrosponding subjects array
					inputLine = "joinMessage|";
					for(int i = 0; i < userList.length; i++){
						inputLine += "%";						//use this to split the string into subjects on client end
						for(int j = 0; j < userList[i].size(); j++){
							inputLine += userList[i].get(j) + "/";
						}
					}
					
				}
				
				if(inputLine.substring(0, inputLine.indexOf('|')).equals("quitMessage")) {
					removeName();
				}
				
				if(inputLine.substring(0, inputLine.indexOf('|')).equals("updateMessage")) {
					removeName();
					joinMethod(inputLine);
					inputLine = "joinMessage|";
					for(int i = 0; i < userList.length; i++){
						inputLine += "%";						//use this to split the string into subjects on client end
						for(int j = 0; j < userList[i].size(); j++){
							inputLine += userList[i].get(j) + "/";
						}
					}
				}
				
				if(inputLine.equals("/quit")) {
					removeName();
					quitMethod();
					break;
				} 
				
				//Output it to every connection
				for(Socket s : connections) {
					//Reassign stream each loop  
					streamOut = new DataOutputStream(s.getOutputStream()); 
					pw = new PrintWriter(streamOut);
					pw.println(inputLine);
					pw.flush();
				}
				//this is to make sure the chatwindow gets updated properly
				if(inputLine.substring(0, inputLine.indexOf('|')).equals("quitMessage")) {
					inputLine = updateChatWindows();
					for(Socket s : connections) {
						//Reassign stream each loop  
						streamOut = new DataOutputStream(s.getOutputStream()); 
						pw = new PrintWriter(streamOut);
						pw.println(inputLine);
						pw.flush();
					}
				}
			}

			socket.close();
			streamIn.close();
			streamOut.close();

		} catch(Exception e) {
			System.err.println("A ChatClient was closed, ending session...");
			removeName();
			quitMethod();
		}
	}
	
	private synchronized void quitMethod(){
		for(int i = 0; i < connections.size(); i++){
			if(socket.toString().equals(connections.get(i).toString())){
				connections.remove(i);
				return;
			}
		}System.out.println("Couldn't find the socket!@!@!");
	}
	
	private synchronized void joinMethod(String inputLine){
		
			String msg = inputLine.substring(inputLine.indexOf('|') + 1); 
			String tempUserInfo[] = msg.split("\\/");		//takes the join message and derives the name and subjects from it
			name = tempUserInfo[0];
			
			//send messsage saying user has joined room
			for(Socket s : connections) {
				String message = "userJoinedMessage|";
				for(int i = 0; i < tempUserInfo.length; i++){
					message += tempUserInfo[i] + "/";
				}
				try{streamOut = new DataOutputStream(s.getOutputStream());}catch(IOException n){} 
				pw = new PrintWriter(streamOut);
				pw.println(message);
				pw.flush();
			}
			
			//check if user takes subject, if they do add their name to that arraylist
			for(int i = 0; i < userList.length; i++){
				for(int j = 1; j < tempUserInfo.length; j++){
					if(tempUserInfo[j].equals(userList[i].get(0))){
						userList[i].add(name);
					}					
				}
			}		
	}

	//executed when someone leave chat, removes their name from the chatwindow list
	private synchronized void removeName(){
		for(int i = 0; i < userList.length; i++){
			for(int j = 1; j < userList[i].size(); j++){
				if(userList[i].get(j).equals(name)){		//go through the subjects arrays and remove the name of the person leaving
					userList[i].remove(j);
				}					
			}
		}
	}
	
	//method to update the chatwindows ;p
	private synchronized String updateChatWindows(){
		String updatedList = "joinMessage|";
		for(int i = 0; i < userList.length; i++){
			updatedList += "%";						//use this to split the string into subjects on client end
			for(int j = 0; j < userList[i].size(); j++){
				updatedList += userList[i].get(j) + "/";
			}
		}
		return updatedList;
	}
}
