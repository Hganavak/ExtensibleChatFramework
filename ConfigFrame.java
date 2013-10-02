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

/**
 * This class is the window that will be shown to the user
 * if the subjects.txt file has not yet been created.
 * 
 * <p>The user will select their subjects and their UPI into
 * a simple form, the content of which will then be written 
 * to the aforementioned text file.
 * 
 * @author Sam Kavanagh
 * @author Hayden Harrison
 *
 */
public class ConfigFrame extends JFrame implements ActionListener{
	private JComboBox cmbPaper1, cmbPaper2, cmbPaper3, cmbPaper4;
	private JTextField txtUid;
	private String[] s;
	/**
	 * The constructor method to initialise the frame 
	 */
	public ConfigFrame() {
		setTitle("Chat Program - Enter your details");
		setSize(230, 270);
		setVisible(false);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	
		/**
		 * The top level panel
		 */
		JPanel topPanel = new JPanel();
		topPanel.setLayout(null);
		getContentPane().add(topPanel);
		
		/**
		 * Labels to appear on form
		 */
		JLabel lblTitle, lblUid, lblPaper1, lblPaper2, lblPaper3, lblPaper4;
		lblTitle	=	new JLabel("Enter your details below: ");
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
		lblTitle.setBounds(5,1, 200, 30);
		topPanel.add(lblTitle);
		
		lblUid.setBounds(5, 30, 30, 30);
		topPanel.add(lblUid);
		txtUid.setBounds(31, 30, 135, 20);
		topPanel.add(txtUid);
		
		lblPaper1.setBounds(5, 55, 70, 30);
		topPanel.add(lblPaper1);
		cmbPaper1.setBounds(65, 55, 100, 20);
		topPanel.add(cmbPaper1);
		
		lblPaper2.setBounds(5, 80, 70, 20);
		topPanel.add(lblPaper2);
		cmbPaper2.setBounds(65, 80, 100, 20);
		topPanel.add(cmbPaper2);
		
		lblPaper3.setBounds(5, 105, 70, 20);
		topPanel.add(lblPaper3);
		cmbPaper3.setBounds(65, 105, 100, 20);
		topPanel.add(cmbPaper3);
		
		lblPaper4.setBounds(5, 130, 70, 20);
		topPanel.add(lblPaper4);
		cmbPaper4.setBounds(65, 130, 100, 20);
		topPanel.add(cmbPaper4);
		
		submit.setBounds(45, 175, 100, 20);
		topPanel.add(submit);
		setVisible(true);
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
	
	public void actionPerformed(ActionEvent e){
		PrintWriter writeFile = null;
		
		try{
			s = new String[4];
			s[0] = cmbPaper1.getSelectedItem().toString(); s[1] = cmbPaper2.getSelectedItem().toString(); 
			s[2] = cmbPaper3.getSelectedItem().toString(); s[3] = cmbPaper4.getSelectedItem().toString(); 
			
			
			
			if((!(txtUid.getText().trim().equals(""))) && unique(s) && validUPI(txtUid.getText())){  //check whether UID field is valid and if any subjects are the same
				
				
				
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
		}//finally{
			//*******THIS CODE IS BEING REACHED FOR SOME REASON, EVEN IF YOU ENTERED NO UPI***********
			//*******REMOVING FINALLY STATEMENT FIXES ALL THE EXCEPTIONS, TOO TIRED TO FIGURE OUT WHY******* 
			
			writeFile.close();
			ChatFrame chatFrame = new ChatFrame();
			chatFrame.init();
			chatFrame.go();
			this.dispose();
		//}
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
		try  
	{  
		Integer.parseInt(input);  
		return true;  
	}  
	catch(Exception e)  
   {  
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
		
}
