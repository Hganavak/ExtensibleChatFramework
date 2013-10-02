import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.*;
import java.util.*;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.*;
import java.awt.event.*;


public class EditSubjects extends ChatFrame implements ActionListener, WindowListener{
	private JComboBox cmbPaper1, cmbPaper2, cmbPaper3, cmbPaper4, cmbFonts;
	private JTextField txtUid;
	private String[] s;
	private static JTabbedPane tabs;
	private boolean done;
	private JPanel topPanel,subjectsPanel, themesPanel;
	private JLabel lblTitle, lblUid, lblPaper1, lblPaper2, lblPaper3, lblPaper4;
	private JColorChooser cc;
	/**
	 * The constructor method to initialise the frame 
	 */
	public EditSubjects() {
		topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		getContentPane().add(topPanel);
		
		setVisible(false);
		setTitle("Edit your details");
	
		setResizable(false);
		setLocationRelativeTo(null);
		done = false;
				/**
		 * The top level panel
		 */
		
		
		createSubjectsTab();
		createThemesTab();
		tabs = new JTabbedPane();
		tabs.add("Subjects",subjectsPanel);
		tabs.add("Themes", themesPanel);
		topPanel.add(tabs,BorderLayout.CENTER);
		
		setSize(600, 400);
		setVisible(true);
		//JColorChooser cc = new JColorChooser();
        //topPanel.add(cc);
	}
	private void createThemesTab(){
		themesPanel = new JPanel(new BorderLayout());
		
		cc = new JColorChooser();
		themesPanel.add(cc,BorderLayout.CENTER);
		
		//cmbFonts = new JComboBox(getFonts());
		//themesPanel.add(cmbFonts, BorderLayout.EAST);
		
	}
	private void createSubjectsTab(){
		subjectsPanel = new JPanel(null);
		
		/**
		 * Labels to appear on form
		 */
		lblTitle	=	new JLabel("Enter your details below");
		lblUid	 	=	new JLabel("UID: ");
		lblPaper1	=	new JLabel("Paper 1: ");
		lblPaper2	=	new JLabel("Paper 2: ");
		lblPaper3	=	new JLabel("Paper 3: ");
		lblPaper4	=	new JLabel("Paper 4: ");
		
		/**
		 * Text field for users to enter uid
		 */
		txtUid	= new JTextField(); 
		
		/**
		 * Combo boxes for the users to select their subjects
		 */
		
		cmbPaper1 = new JComboBox(getSubjects());
		cmbPaper2 = new JComboBox(getSubjects());
		cmbPaper3 = new JComboBox(getSubjects());
		cmbPaper4 = new JComboBox(getSubjects());
		
		/**
		 * Submit button
		 */
		JButton submit = new JButton("Submit");
		submit.addActionListener(this);
		
		//Display the above components
		lblTitle.setBounds(5,5, 200, 30);
		subjectsPanel.add(lblTitle);
		
		lblUid.setBounds(5, 30, 30, 30);
		subjectsPanel.add(lblUid);
		txtUid.setBounds(31, 30, 60, 20);
		subjectsPanel.add(txtUid);
		
		lblPaper1.setBounds(5, 55, 50, 30);
		subjectsPanel.add(lblPaper1);
		cmbPaper1.setBounds(55, 55, 70, 20);
		subjectsPanel.add(cmbPaper1);
		
		lblPaper2.setBounds(5, 80, 50, 20);
		subjectsPanel.add(lblPaper2);
		cmbPaper2.setBounds(55, 80, 70, 20);
		subjectsPanel.add(cmbPaper2);
		
		lblPaper3.setBounds(5, 105, 50, 20);
		subjectsPanel.add(lblPaper3);
		cmbPaper3.setBounds(55, 105, 70, 20);
		subjectsPanel.add(cmbPaper3);
		
		lblPaper4.setBounds(5, 130, 50, 20);
		subjectsPanel.add(lblPaper4);
		cmbPaper4.setBounds(55, 130, 70, 20);
		subjectsPanel.add(cmbPaper4);
		
		submit.setBounds(100, 200, 100, 20);
		subjectsPanel.add(submit);
	}
	/**
	 * Method to fill in comboboxes to reduce clutter
	 * @return String[]	returns a list of subjects for the users to take
	 */
	public String[] getSubjects() {
		return new String[] {
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
	}
	
	public String[] getFonts(){
		return new String[]{
			"Black",
			"Cyan",
			"Green",
			"Orange",
			"Yellow",
			"Pink"		
		};
	}
	
	public void actionPerformed(ActionEvent e){
		PrintWriter writeFile = null;
		
		try{
			s = new String[4];
			s[0] = cmbPaper1.getSelectedItem().toString(); s[1] = cmbPaper2.getSelectedItem().toString(); 
			s[2] = cmbPaper3.getSelectedItem().toString(); s[3] = cmbPaper4.getSelectedItem().toString(); 
			
			
			
			if((!(txtUid.getText().trim().equals(""))) && unique(s) && validUPI(txtUid.getText())){  //check whether UID field is empty and if any subjects are the same
							
				writeFile = new PrintWriter(new FileWriter("subjects.txt"));
			 	writeFile.println(txtUid.getText().trim());
				writeFile.println(cmbPaper1.getSelectedItem());
				writeFile.println(cmbPaper2.getSelectedItem());
				writeFile.println(cmbPaper3.getSelectedItem());
				writeFile.println(cmbPaper4.getSelectedItem());	
			}else{
				JOptionPane.showMessageDialog(this ,"Please enter a valid UID and unique subjects","Oops!", JOptionPane.WARNING_MESSAGE);
				return;
			}
		}catch(IOException b){
			System.out.println("Error creating file");
		}
		done = true;
		writeFile.close();
		this.dispose();
		
	}
	
	private boolean unique(String[] s){  //returns true if array contains all unique values
		Set<String> b = new HashSet<String>();
		for(int i = 0; i < s.length; i++){
			b.add(s[i]);
		}
		if(b.size() == s.length){
			return true;
		}
		return false;
	}
	
	public boolean getDone(){
		return done;
	}
	
	public static boolean validUPI(String upi) {

	//Check if UPI is a valid length
	if(upi.length() > 7 || upi.length() < 5) { return false; }

	//Check if the last 3 characters are numbers
	String lastThreeChars = upi.substring(upi.length()-3, upi.length()); 
	if(!isInteger(lastThreeChars)) {
		return false;
	}
	
	//Check if everything before the last the characters is not a number
	String startOfUPI = upi.substring(0, upi.length()-4);	
	if(containsDigit(startOfUPI)) { return false; }
	
	return true;
}

	public static boolean isInteger(String input) {  
	try {  
		Integer.parseInt(input);  
		return true;  
	}  
	catch(Exception e){  
      return false;  
   }  
}

	public static boolean containsDigit(final String s){    
		for (char c: s.toCharArray()){
			if(Character.isDigit(c)){
				return true;
			}
		}
		return false;
	}
	public void windowActivated(WindowEvent e){}
	public void windowDeactivated(WindowEvent e){}	
	public void windowClosed(WindowEvent e){} 
	public void windowClosing(WindowEvent e){done = true;}
	public void windowDeiconified(WindowEvent e){}
	public void windowIconified(WindowEvent e){}
	public void windowOpened(WindowEvent e){}
}
