import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.TrayIcon;
import java.awt.Color;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;

public class ChatClientReceiver extends ChatClient implements Runnable{
	private Socket socket = null;
	private DataInputStream streamIn = null;
	private String serverMessage;
	private boolean active = true;
	private final static String newLine = "\n";
	public String curMsg = "";
	public boolean[] flashing;
	private Color foreground, defaultColor, privateForeground;
	private final int  MESSAGE_STATUS = 2, MESSAGE_UPI = 1, MESSAGE = 0;


	
	public ChatClientReceiver(Socket chatSocket){
		this.socket = chatSocket;
		foreground = Color.BLUE;
		defaultColor = Color.BLACK;
		privateForeground = Color.RED;
		flashing = new boolean[4];
		for(int i = 0; i < flashing.length; i ++){flashing[i] = false;}		
	}
	
	public void run(){
		try{
			streamIn = new DataInputStream(socket.getInputStream());
		}catch(IOException e){System.out.println("Error getting inputstream from server");}
		
		while(active){
			try{
				serverMessage = streamIn.readLine();
			}catch(IOException e){
				active = false;
			}
			if(serverMessage != null){
				curMsg = serverMessage;
				updateGUI();
			}
		}
		try{
			socket.close();
			streamIn.close();
		}catch(IOException e){System.out.println("Error closing socket");}
	}
	
	public void updateGUI() {
		SwingUtilities.invokeLater(new Runnable() {	
			public void run() {

				boolean containedUPI = false; //Whether the message contained the users UPI
				
				String msg = ChatClientReceiver.this.curMsg;
				String subject = msg.substring(0, msg.indexOf('|'));
				msg = msg.substring(msg.indexOf('|') + 1);
					
				//Check if the message contains the users UPI, if it does - display a notification
				String msgWithoutSender = msg.substring(msg.indexOf(':') + 1); //Remove the sender preceding the msg ('upi123:')
				if(!subject.equals("userJoinedMessage")){
					if(containsUPI(msgWithoutSender)) {
						displayMsg(msg);
						playSound();
						containedUPI = true;
					}
				}
				
				//Update the applicable textArea 
				if(subject.equals(ChatFrame.userInfo[1])) {
					if(containedUPI){append(doc1,  msg + "\n", MESSAGE_UPI);}else{ //change style depending on whether upi is in message
					append(doc1,  msg + "\n", MESSAGE);}
					

					//Set tab colour
					if(tabbedPane.getSelectedIndex() != 0) { //If the tab isn't already open
						if(!flashing[0]) { tabbedPane.setForegroundAt(0, Color.BLUE);}
						if(containedUPI) { flashTab(0); }
					}


				} else if(subject.equals(ChatFrame.userInfo[2])) {
					if(containedUPI){append(doc2,  msg + "\n", MESSAGE_UPI);}else{ //change style depending on whether upi is in message
					append(doc2,  msg + "\n", MESSAGE);}
					
				
					//Set tab colour
					if(tabbedPane.getSelectedIndex() != 1) { //If the tab isn't already open
						if(!flashing[1]) {tabbedPane.setForegroundAt(1, Color.BLUE);  }
						if(containedUPI) { flashTab(1); }
					}

				} else if(subject.equals(ChatFrame.userInfo[3])) {
					if(containedUPI){append(doc3,  msg + "\n", MESSAGE_UPI);}else{ //change style depending on whether upi is in message
						append(doc3,  msg + "\n", MESSAGE);}
					
					
					//Set tab colour
					if(tabbedPane.getSelectedIndex() != 2) { //If the tab isn't already open
						if(!flashing[2]) { tabbedPane.setForegroundAt(2, Color.BLUE); }
						if(containedUPI) { flashTab(2); }
					}

				} else if(subject.equals(ChatFrame.userInfo[4])) {
					if(containedUPI){append(doc4,  msg + "\n", MESSAGE_UPI);}else{ //change style depending on whether upi is in message
						append(doc4,  msg + "\n", MESSAGE);}
					
					
					//Set tab colour
					if(tabbedPane.getSelectedIndex() != 3) { //If the tab isn't already open
						if(!flashing[3]) { tabbedPane.setForegroundAt(3, Color.BLUE); }
						if(containedUPI) { flashTab(3); }
					}


				} else if (subject.equals("quitMessage")){ //If it is a special quit message
					System.out.println("Received message: " + msg);				
					String tempUserInfo[] = msg.split("\\/"); //Store disconnecting users subjects
					
					ArrayList<StyledDocument> docList = new ArrayList<StyledDocument>();
					docList.add(null);
					docList.add(doc1);
					docList.add(doc2);
					docList.add(doc3);
					docList.add(doc4);
					//displays the quit message	
					for(int i=1; i<userInfo.length; i++) {
						String curUserInfoElement = userInfo[i];
						for(int j=1; j<tempUserInfo.length; j++) {
							String curTempUserInfoElement = tempUserInfo[j];
							if(curTempUserInfoElement.equals(curUserInfoElement)) { 
								append(docList.get(i), tempUserInfo[0]  + " has left the room." + "\n", MESSAGE_STATUS);
							}
						}
					}
				} else if (subject.equals("joinMessage")){
					//make an array of the object that updates the chat windows
					ArrayList<DefaultListModel> chatWindowList = new ArrayList<DefaultListModel>();
					chatWindowList.add(null); //******What does do?********// makes it so the size is the same as userInfo array, easier to add shiz
					chatWindowList.add(userListModel1);
					chatWindowList.add(userListModel2);
					chatWindowList.add(userListModel3);
					chatWindowList.add(userListModel4);
					
					for(int b = 1; b < chatWindowList.size(); b++){
						chatWindowList.get(b).removeAllElements();  //clear the chat windows for updating
					}
					
					
					String subjectsArray[] = msg.split("%");  //splits the string into the subject followed by the people taking it
					for(int i = 0; i < subjectsArray.length; i++){
						String peopleInSubject[] = subjectsArray[i].split("\\/"); //splits up the users
						
						for(int j = 1; j < userInfo.length; j++){
							if(peopleInSubject[0].equals(userInfo[j])){
								for(int k = 1; k < peopleInSubject.length; k++){
									chatWindowList.get(j).addElement(peopleInSubject[k]);
								}
							}
						}
										
					}
					
				
				}else if(subject.equals("userJoinedMessage")){
					System.out.println("Received message: " + msg);				
					String tempUserInfo[] = msg.split("\\/"); //Store disconnecting users subjects
					
					ArrayList<StyledDocument> docList = new ArrayList<StyledDocument>();
					docList.add(null);
					docList.add(doc1);
					docList.add(doc2);
					docList.add(doc3);
					docList.add(doc4);
					//displays the quit message	
					for(int i=1; i<userInfo.length; i++) {
						String curUserInfoElement = userInfo[i];
						for(int j=1; j<tempUserInfo.length; j++) {
							String curTempUserInfoElement = tempUserInfo[j];
							if(curTempUserInfoElement.equals(curUserInfoElement)) { 
								append(docList.get(i), tempUserInfo[0]  + " has joined the room." + "\n", MESSAGE_STATUS);
							}
						}
					}
				}
				//end of else if
					
			
			}
			
		});		
		
	}
	//method for making tabs flash
	private void flashTab(final int tabIndex) {
		Thread thread = new Thread(){
			public void run(){
			if(flashing[tabIndex]){return;} //if tab is already flashing dont flash again
				flashing[tabIndex] = true; 
				Color c = privateForeground; //variable used to switch colors
				try{
					for(int i = 0; ; i ++){
						if(getstopFlash(tabIndex)){stopFlash[tabIndex] = false; flashing[tabIndex] = false; return;} //if the tab is selected stop flashing
						if(i % 2 == 0){c = privateForeground;} else{c = defaultColor;} //switches colors
						tabbedPane.setForegroundAt(tabIndex, c);
						Thread.sleep(1000); 
					}
				} catch (Exception e){ System.out.println("Exception occured in flashTab"); e.printStackTrace();}
			}
		};
		thread.start();
	}
	

		
	
	private boolean containsUPI(String msg){

		if(msg.charAt(0) == '%') { return false; } //If its a join/quit message then ignore it

		if(msg.indexOf(userInfo[0]) != -1){
			return true;
		} else{
			return false;
		}
	}	
	
	public void append(StyledDocument doc, String text,  int m){
		try{
			if(m == 0){
				doc.insertString(doc.getLength(), text, textPane1.getStyle("Message"));
			}else if(m == 1){
				doc.insertString(doc.getLength(), text, textPane1.getStyle("Private"));
			}else if (m == 2){
				doc.insertString(doc.getLength(), text, textPane1.getStyle("Status"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
