public class ContainsUPITest {


	public static void main(String[] args) {

		String msg = "a muffin is pretty";
		String msg2 = "a poo in a wee";
		String msg3 = "hello im skav012 how are you";
		String msg4 = "skav012 is pretty";
		String msg5 = "OHIskav012 how are you";
		
		if(containsUPI(msg)) { System.out.println(msg + " contains skav012"); }		
		if(containsUPI(msg2)) { System.out.println(msg2 + " contains skav012"); }
		if(containsUPI(msg3)) { System.out.println(msg3 + " contains skav012"); }
		if(containsUPI(msg4)) { System.out.println(msg4 + " contains skav012"); }
		if(containsUPI(msg5)) { System.out.println(msg5 + " contains skav012"); }	
	}	

	private static boolean containsUPI(String msg){
		if(msg.indexOf("skav012") != -1){
			return false;
		} else{
			return true;
		}
	}	

}