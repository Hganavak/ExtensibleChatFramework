import java.io.*;
public class ChatProgram {
	private static File f;
	public static void main(String[] args) { 
	
		f = new File("subjects.txt");
		if(f.exists()){
			ChatFrame chatFrame = new ChatFrame();
			chatFrame.init();
			chatFrame.go();
		}else{
			ConfigFrame cfgFrame = new ConfigFrame();
		}
		
	}
}
