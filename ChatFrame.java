import java.awt.BorderLayout;
import java.awt.*;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.net.URL;
import java.awt.TrayIcon;
import java.awt.Color;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.applet.*;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
/**
 * This class is the main window that will be displayed if the user has already selected
 * their subjects and entered their UPI.
 * 
 * <p>It will contain a <tt>JTabbedPanel</tt> which will have 4 tabs, one for each subject.
 * Note: This will need to be changed later on as not all people take 4 papers
 * 
 * <p>Initially no layout manager will be used for simplicity.
 * 
 * @author Sam Kavanagh
 * @author Hayden Harrison
 * @version 1.0 Dec 29, 2011
 * 
 */
public class ChatFrame extends JFrame implements ActionListener, WindowListener{
	
	/**
	 *  The tabbed pane which will contain each of our subjects chat areas
	 */
	protected static JTabbedPane tabbedPane;

	/**
	 * The following JPanels will be added to the above <tt>JTabbedPane</tt> using
	 * their respective methods, each one will contain its own components so they
	 * can each be referenced individually, which means code will inevitably be repeated.
	 */
	private	JPanel panel1;
	private	JPanel panel2;
	private	JPanel panel3;
	private	JPanel panel4;
	
	private JMenuBar menuBar;
	private JMenuItem exit, editInfo;
	
	private EditSubjects es;
	//all the componenet variables

	public static JTextPane textPane1, textPane2, textPane3, textPane4;
	public static StyledDocument doc1, doc2, doc3, doc4;
	public static Style style;
	public static Color textColor;
	public static Font font;
	
	private JButton button1, button2, button3, button4, buttonClear1, buttonClear2, buttonClear3, buttonClear4 ;
	private JTextField textField1, textField2, textField3, textField4;
	private JScrollPane scrollPane1, scrollPane2, scrollPane3, scrollPane4, userScroll1, userScroll2, userScroll3, userScroll4;
	private JLabel title1, title2, title3, title4;
	private JList userList1, userList2, userList3, userList4;
	public static DefaultListModel userListModel1, userListModel2, userListModel3, userListModel4;

	public static TrayIcon trayIcon;
	protected SystemTray tray;
	
	private BufferedReader subjectsReader;
	public static String[] userInfo;
	public static boolean[] stopFlash; //used to tell flashTab method when to stop

	
	private GridBagConstraints c;
	/**
	 * The actual ChatClient object
	 */
	private ChatClient chatClient;

	/**
	 * The method used to initiate the GUI components.
	 * A constructor cannot be used due to the fact that ChatClient and
	 * ChatClientReceiver inherit this class, and subclasses implicitly call
	 * their parent classes constructor.
	 *  
	 * <p>Will initialise frame and call each papers respective panel
	 * initialising method.
	 */
	public void init() {
		stopFlash = new boolean[4];
		for(int i = 0; i < stopFlash.length; i ++){stopFlash[i] = false;}		
		setTitle("Chat Program");
	
		setVisible(false);
		setSize(900, 600);
		setLocationRelativeTo(null);
		textColor = Color.BLACK;
		
		
		addWindowListener(this);
		//Set look and feel
		try {
		    UIManager.setLookAndFeel(
		        UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
		  System.out.println("Unable to load native look and feel");
		} 
		initTrayIcon();
		userInfo = new String[5]; //array to hold subjects.txt info
		try{
			subjectsReader = new BufferedReader(new FileReader("subjects.txt"));
			for(int i = 0; i < userInfo.length; i++){
				userInfo[i] = subjectsReader.readLine();		//read the subjects info into the array
			}
			subjectsReader.close();
		}catch(IOException e){
			ConfigFrame cfgFrame = new ConfigFrame();
			this.dispose();										//if the file isnt found go back to the config window so they can enter details
			JOptionPane.showMessageDialog(this,"Couldn't find subjects file, please enter in your details.","Oops!", JOptionPane.WARNING_MESSAGE);
		}
		
		
		/** 
		 * topPanel is the highest level panel, to which the tabbedPane will be added
		 */
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		getContentPane().add(topPanel);
		
		
		//set up menu bar
		menuBar = new JMenuBar();
		JMenu settingsMenu = new JMenu("File");
		menuBar.add(settingsMenu);
		editInfo = new JMenuItem("Edit profile");
		settingsMenu.add(editInfo);
		exit = new JMenuItem("Exit");
		settingsMenu.add(exit);
		exit.addActionListener(this);
		editInfo.addActionListener(this);
		setJMenuBar(menuBar);
		
		//Create the tabs
		createTab1();
		createTab2();
		createTab3();
		createTab4();
	
		//Create the tabbed pane
		tabbedPane = new JTabbedPane();
		tabbedPane.add("      " + userInfo[1] + "      ", panel1);
		tabbedPane.add("      " + userInfo[2]+ "      ", panel2);
		tabbedPane.add("      " +userInfo[3]+ "      ", panel3);
		tabbedPane.add("      " +userInfo[4]+ "      ", panel4);
		topPanel.add(tabbedPane, BorderLayout.CENTER);
	
		//If the user changes tabs, set the text colour back to black
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				stopFlash[tabbedPane.getSelectedIndex()] = true;
				tabbedPane.setForegroundAt(tabbedPane.getSelectedIndex(), Color.BLACK);	
				
			}	
		});
					
		setExtendedState(Frame.MAXIMIZED_BOTH);	
		setVisible(true);
	}

	/**
	 * Method to create tab for paper 1
	 */
	
	private void createTab1() {
		panel1 = new JPanel();
		c = new GridBagConstraints();
		panel1.setLayout(new GridBagLayout()); //No layout manager initially
		
		/**
		 * title will need to be updated to reflect corresponding
		 * subject from subjects document
		 */
		setConstraints(0,0,0,0,1,1,GridBagConstraints.NORTHWEST,0,new Insets(10,4,10,10));
		title1 = new JLabel(userInfo[1] + " chat");
		panel1.add(title1, c);
		
		/**
		 * The text area where messages received and sent messages will 
		 * be stored
		 */
		setConstraints(1.0, 0.8, 0, 1, 4, 4,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,  new Insets(0,5,5,5));
		
				
		textPane1 = new JTextPane();
		textPane1.setEditable(false);
		doc1 = textPane1.getStyledDocument();
		//set up the styles
		style = textPane1.addStyle("Message", null);
		StyleConstants.setFontSize(style, 13);
		StyleConstants.setFontFamily(style, "Helvetica");
		//StyleConstants.setForeground(style, textColor);
		
		style = textPane1.addStyle("Private", style);
		StyleConstants.setBold(style, true);
		
		style = textPane1.addStyle("Status", style);
		StyleConstants.setForeground(style, Color.RED);
		
		
		scrollPane1 = new JScrollPane(textPane1); 
		panel1.add(scrollPane1, c);
		/**
		 * list where users online are displayed
		 */
		setConstraints(0.3, 0.3, 4, 1, 0, 4,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,  new Insets(0,5,5,5));
		
			
		//List to store the users online
		userListModel1 = new DefaultListModel(); //this is used to edit the data in the list userListModel1.addElement();
		userList1 = new JList(userListModel1);
		userScroll1 = new JScrollPane(userList1);
		panel1.add(userScroll1, c);
				
		/**
		 * Area for user to enter messages, action listener will need
		 * to be added.
		 */
		setConstraints(0.5, 0.2, 0, 4, 1, 4,GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,  new Insets(0,5,5,5));
		textField1 = new JTextField(30);
		textField1.addActionListener(this);
		panel1.add(textField1, c);
					
		/**
		 * Buttons (alternative) means of sending messages, action
		 * listener will need to be added
		 */
		setConstraints(0, 0, GridBagConstraints.RELATIVE, 5, 1, 1,GridBagConstraints.SOUTH, GridBagConstraints.NONE,  new Insets(0,5,5,5));
			
		button1 = new JButton("Send");
		button1.addActionListener(this);
		panel1.add(button1, c);
				
		buttonClear1 = new JButton("Clear");
		buttonClear1.addActionListener(this);
		panel1.add(buttonClear1, c);
			
	}
	
	/**
	 * Method to create tab for paper 2
	 */
	private void createTab2() {
		panel2 = new JPanel();
		c = new GridBagConstraints();
		panel2.setLayout(new GridBagLayout()); 
		
		/**
		 * title will need to be updated to reflect corresponding
		 * subject from subjects document
		 */
		setConstraints(0,0,0,0,1,1,GridBagConstraints.NORTHWEST,0,new Insets(10,4,10,10));
		title2 = new JLabel(userInfo[2] + " chat");
		panel2.add(title2, c);
		
		/**
		 * The text area where messages received and sent messages will 
		 * be stored
		 */
		setConstraints(1.0, 0.8, 0, 1, 4, 4,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,  new Insets(0,5,5,5));
		textPane2 = new JTextPane();
		textPane2.setEditable(false);
		doc2 = textPane2.getStyledDocument();
		scrollPane2 = new JScrollPane(textPane2); 
		panel2.add(scrollPane2, c);
		/**
		 * list where users online are displayed
		 */
		setConstraints(0.3, 0.3, 4, 1, 0, 4,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,  new Insets(0,5,5,5));
		//List to store the users online
		userListModel2 = new DefaultListModel(); //this is used to edit the data in the list userListModel1.addElement();
		userList2 = new JList(userListModel2);
		userScroll2 = new JScrollPane(userList2);
		panel2.add(userScroll2, c);
				
		/**
		 * Area for user to enter messages, action listener will need
		 * to be added.
		 */
		setConstraints(0.5, 0.2, 0, 4, 1, 4,GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,  new Insets(0,5,5,5));
		textField2 = new JTextField(30);
		textField2.addActionListener(this);
		panel2.add(textField2, c);
					
		/**
		 * Buttons (alternative) means of sending messages, action
		 * listener will need to be added
		 */
		setConstraints(0, 0, GridBagConstraints.RELATIVE, 5, 1, 1,GridBagConstraints.SOUTH, GridBagConstraints.NONE,  new Insets(0,5,5,5));
		button2 = new JButton("Send");
		button2.addActionListener(this);
		panel2.add(button2, c);
			
		buttonClear2 = new JButton("Clear");
		buttonClear2.addActionListener(this);
		panel2.add(buttonClear2, c);
	}
	
	/**
	 * Method to create tab for paper 3
	 */
	private void createTab3() {
		panel3 = new JPanel();
		c = new GridBagConstraints();
		panel3.setLayout(new GridBagLayout()); 
		
		/**
		 * title will need to be updated to reflect corresponding
		 * subject from subjects document
		 */
		setConstraints(0,0,0,0,1,1,GridBagConstraints.NORTHWEST,0,new Insets(10,4,10,10));
		title3 = new JLabel(userInfo[3] + " chat");
		panel3.add(title3, c);
		
		/**
		 * The text area where messages received and sent messages will 
		 * be stored
		 */
		setConstraints(1.0, 0.8, 0, 1, 4, 4,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,  new Insets(0,5,5,5));
		textPane3 = new JTextPane();
		textPane3.setEditable(false);
		doc3 = textPane3.getStyledDocument();
		scrollPane3 = new JScrollPane(textPane3); 
		panel3.add(scrollPane3, c);
		/**
		 * list where users online are displayed
		 */
		setConstraints(0.3, 0.3, 4, 1, 0, 4,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,  new Insets(0,5,5,5));
		//List to store the users online
		userListModel3 = new DefaultListModel(); //this is used to edit the data in the list userListModel1.addElement();
		userList3 = new JList(userListModel3);
		userScroll3 = new JScrollPane(userList3);
		panel3.add(userScroll3, c);
				
		/**
		 * Area for user to enter messages, action listener will need
		 * to be added.
		 */
		setConstraints(0.5, 0.2, 0, 4, 1, 4,GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,  new Insets(0,5,5,5));
		textField3 = new JTextField(30);
		textField3.addActionListener(this);
		panel3.add(textField3, c);
					
		/**
		 * Buttons (alternative) means of sending messages, action
		 * listener will need to be added
		 */
		setConstraints(0, 0, GridBagConstraints.RELATIVE, 5, 1, 1,GridBagConstraints.SOUTH, GridBagConstraints.NONE,  new Insets(0,5,5,5));
		button3 = new JButton("Send");
		button3.addActionListener(this);
		panel3.add(button3, c);
				
		buttonClear3 = new JButton("Clear");
		buttonClear3.addActionListener(this);
		panel3.add(buttonClear3, c);
	}
	
	/**
	 * Method to create tab for paper 4
	 */
	private void createTab4() {
		panel4 = new JPanel();
		c = new GridBagConstraints();
		panel4.setLayout(new GridBagLayout());
		
		/**
		 * title will need to be updated to reflect corresponding
		 * subject from subjects document
		 */
		setConstraints(0,0,0,0,1,1,GridBagConstraints.NORTHWEST,0,new Insets(10,4,10,10));
		title4 = new JLabel(userInfo[4] + " chat");
		panel4.add(title4, c);
		
		/**
		 * The text area where messages received and sent messages will 
		 * be stored
		 */
		setConstraints(1.0, 0.8, 0, 1, 4, 4,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,  new Insets(0,5,5,5));
		textPane4 = new JTextPane();
		textPane4.setEditable(false);
		doc4 = textPane4.getStyledDocument();
		scrollPane4 = new JScrollPane(textPane4); 
		panel4.add(scrollPane4, c);
		
		/**
		 * list where users online are displayed
		 */
		setConstraints(0.3, 0.3, 4, 1, 0, 4,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,  new Insets(0,5,5,5));
		//List to store the users online
		userListModel4= new DefaultListModel(); //this is used to edit the data in the list userListModel1.addElement();
		userList4 = new JList(userListModel4);
		userScroll4 = new JScrollPane(userList4);
		panel4.add(userScroll4, c);
				
		/**
		 * Area for user to enter messages, action listener will need
		 * to be added.
		 */
		setConstraints(0.5, 0.2, 0, 4, 1, 4,GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,  new Insets(0,5,5,5));
		textField4 = new JTextField(30);
		textField4.addActionListener(this);
		panel4.add(textField4, c);
					
		/**
		 * Buttons (alternative) means of sending messages, action
		 * listener will need to be added
		 */
		setConstraints(0, 0, GridBagConstraints.RELATIVE, 5, 1, 1,GridBagConstraints.SOUTH, GridBagConstraints.NONE,  new Insets(0,5,5,5));
		button4 = new JButton("Send");
		button4.addActionListener(this);
		panel4.add(button4, c);
				
		buttonClear4 = new JButton("Clear");
		buttonClear4.addActionListener(this);
		panel4.add(buttonClear4, c);
	}
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == button1 || e.getSource() == textField1) {
			if(textField1.getText().trim().length() > 0 && textField1.getText().length() < 400){					//check if field contains only whitespace and si less than 400chars
				chatClient.sendMessage(userInfo[1] + "|" + userInfo[0] + ":  " + textField1.getText());
			}
			textField1.setText(""); //Clear the textfield
		} else if (e.getSource() == button2 || e.getSource() == textField2) {
			if(textField2.getText().trim().length() > 0 && textField2.getText().length() < 400){	
				chatClient.sendMessage(userInfo[2] + "|" + userInfo[0] + ":  " + textField2.getText());
			}
			textField2.setText(""); //Clear the textfield
		} else if (e.getSource() == button3 || e.getSource() == textField3) {
			if(textField3.getText().trim().length() > 0 && textField3.getText().length() < 400){	
				chatClient.sendMessage(userInfo[3] + "|" + userInfo[0] + ":  " +textField3.getText());
			}
			textField3.setText(""); //Clear the textfield
		} else if (e.getSource() == button4 || e.getSource() == textField4) {
			if(textField4.getText().trim().length() > 0 && textField4.getText().length() < 400){	
				chatClient.sendMessage(userInfo[4] + "|" + userInfo[0] + ":  " + textField4.getText());
			}
			textField4.setText(""); //Clear the textfield
		} else if (e.getSource() == exit){
			System.exit(-1);
		} else if (e.getSource() == editInfo){  //Editing subjects
			es = new EditSubjects();
			
			Thread thread = new Thread(){
				public void run(){
					try{
						while(!es.getDone()){sleep(100);}
						editSubjects();
					} catch (Exception b){
						System.out.println("Error changing subjects");
					}
				}
			};thread.start();
			
			
		//Clearing buttons	
		} else if (e.getSource() == buttonClear1){
			textPane1.setText("");
		} else if (e.getSource() == buttonClear2){
			textPane2.setText("");
		} else if (e.getSource() == buttonClear3){
			textPane3.setText("");
		} else if (e.getSource() == buttonClear4){
			textPane4.setText("");
		}
	}
	
	/**
	 * After the GUI has been initialised, this method is called which creates
	 * the actual chat object (which has all the underlying functionality)
	 */
	public void go(){
		chatClient = new ChatClient();
		chatClient.connect();
		//Send what subjects the client is taking
		chatClient.sendMessage("joinMessage|" + userInfo[0] + "/" + userInfo[1] + "/" + userInfo[2] + "/" + userInfo[3] + "/" + userInfo[4]);
		repaint();
	}
	
	/**
	* Re opens the subjects file and changes the names of tabs and labels
	*/
	public void editSubjects() throws IOException{
		subjectsReader = new BufferedReader(new FileReader("subjects.txt"));
			for(int i = 0; i < userInfo.length; i++){
				userInfo[i] = subjectsReader.readLine();		//read the subjects info into the array
			}
			subjectsReader.close();
		for(int i = 1; i < userInfo.length; i++){
			tabbedPane.setTitleAt(i - 1, "      " + userInfo[i] + "      " );
		}
		
		title1.setText(userInfo[1] + " chat");
		title2.setText(userInfo[2] + " chat");
		title3.setText(userInfo[3] + " chat");
		title4.setText(userInfo[4] + " chat");
		//send the message to update subjects user is taking
		chatClient.sendMessage("updateMessage|" + userInfo[0] + "/" + userInfo[1] + "/" + userInfo[2] + "/" + userInfo[3] + "/" + userInfo[4]);
	}
	
	/**
	* Set up the tray icon
	*/
	public void initTrayIcon() {
		if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        this.trayIcon = new TrayIcon(createImage("images/bulb.gif", "A bulb"));
        trayIcon.setToolTip("Do you remember when, we used to sing????");
		tray = SystemTray.getSystemTray();

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
	}

	//Obtain the image URL
    protected static Image createImage(String path, String description) {
        URL imageURL = ChatFrame.class.getResource(path);
         
        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }

    /**
    * Take a message and display it in a baloon window thingy.
    */
    protected void displayMsg(String msg) { 
    	try {
    		trayIcon.displayMessage("New Message", msg, TrayIcon.MessageType.NONE);
    	} catch(Exception e) {
    		System.out.println("An error occured in printing out an alert");
    		e.printStackTrace();
    	}
    }
	//method that plays sound when upi is mentioned in method, called by ChatClientReceiver
	public void playSound(){
		URL url = getClass().getResource("sounds/msg.wav");
		AudioClip clip = Applet.newAudioClip(url);
		clip.play();
	}

	/**
	* Accessor method
	* @return stopFlash
	*/
	protected boolean getstopFlash(int index) {
		return stopFlash[index];
	}
	//method for setting gridBagConstraints
	private void setConstraints(double wx, double wy, int gx, int gy, int gw, int gh, int a, int fill,  Insets insets){
		c.weightx = wx;
		c.weighty = wy;
		c.gridx = gx;
		c.gridy = gy;
		c.gridwidth = gw;
		c.gridheight = gh;
		c.anchor = a;
		c.fill = fill;
		c.insets = insets;
	}
	

	//WindowListener methods
	public void windowActivated(WindowEvent e){}
	public void windowDeactivated(WindowEvent e){}	
	public void windowClosed(WindowEvent e){} 
	public void windowClosing(WindowEvent e){
		//send a message to the server with the users info to allow the userwindow to update properly and to display a leaving message
		chatClient.sendMessage("quitMessage|" + userInfo[0] + "/" + userInfo[1] + "/" + userInfo[2] + "/" + userInfo[3] + "/" + userInfo[4]);
		this.dispose();
		System.exit(-1);
	}
	public void windowDeiconified(WindowEvent e){}
	public void windowIconified(WindowEvent e){}
	public void windowOpened(WindowEvent e){}
	
}
