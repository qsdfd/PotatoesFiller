import java.io.*;

public class Filer {
	private static final String checkFile = "C:\\Users\\kaisu\\OSBot\\Data\\check.txt";
	private static final String worldFile = "C:\\Users\\kaisu\\OSBot\\Data\\world.txt";
	private static final String accFile = "C:\\Users\\kaisu\\OSBot\\Data\\acc.txt";
	private static final String supplierFile = "C:\\Users\\kaisu\\OSBot\\Data\\supplier.txt";
	
	/*private static final String checkFile = "/root/OSBot/Data/check.txt";
	private static final String worldFile = "/root/OSBot/Data/world.txt";
	private static final String accFile = "/root/OSBot/Data/acc.txt";
	private static final String mulerFile = "/root/OSBot/Data/supplier.txt";*/
	
	public static boolean getCheck(){
		try {
			FileReader fr = new FileReader(new File(checkFile));
			BufferedReader br = new BufferedReader(fr);
			
			String checkStr = br.readLine().trim();
			
			br.close();
			fr.close();
			
			if(checkStr.equals("1"))
				return true;
			else
				return false;
			
		} catch (IOException e) {
			return false;
		}
	}
	
	public static boolean setCheck(boolean check){
		try{
			FileWriter fw = new FileWriter(new File(checkFile));
			PrintWriter pw = new PrintWriter(fw);
			
			if(check)
				pw.write("1");
			else
				pw.write("0");
			
			pw.close();
			fw.close();
			
		}catch(Exception e){
			return false;
		}
		
		return true;
	}
	
	
	public static String getWorld(){
		try {
			FileReader fr = new FileReader(new File(worldFile));
			BufferedReader br = new BufferedReader(fr);
			
			String worldStr = br.readLine().trim();
			
			br.close();
			fr.close();
			
			return worldStr;
			
		} catch (IOException e) {
			return "failed to get world";
		}
	}
	
	public static boolean setWorld(int world){
		try{
			FileWriter fw = new FileWriter(new File(worldFile));
			PrintWriter pw = new PrintWriter(fw);
			
			String worldStr = "" + world;
			
			pw.write(worldStr);
			
			pw.close();
			fw.close();
			
		}catch(Exception e){
			return false;
		}
		
		return true;
	}
	
	public static String getAcc(){
		try {
			FileReader fr = new FileReader(new File(accFile));
			BufferedReader br = new BufferedReader(fr);
			
			String accStr = br.readLine().trim();
			
			br.close();
			fr.close();
			
			return accStr;
			
		} catch (IOException e) {
			return "failed to get world";
		}
	}
	
	public static boolean setAcc(String acc){
		try{
			FileWriter fw = new FileWriter(new File(accFile));
			PrintWriter pw = new PrintWriter(fw);
			
			String accStr = "" + acc;
			
			pw.write(accStr);
			
			pw.close();
			fw.close();
			
		}catch(Exception e){
			return false;
		}
		
		return true;
	}
	
	public static boolean cleanFiles(){
		try{
			FileWriter fw = new FileWriter(new File(checkFile));
			PrintWriter pw = new PrintWriter(fw);
			pw.write("0");
			
			fw = new FileWriter(new File(accFile));
			pw = new PrintWriter(fw);
			pw.write("");
			
			fw = new FileWriter(new File(worldFile));
			pw = new PrintWriter(fw);
			pw.write("");
			
			pw.close();
			fw.close();
			
		}catch(Exception e){
			return false;
		}
		
		return true;
	}
	
	public static String getSupplier(){
		try {
			FileReader fr = new FileReader(new File(supplierFile));
			BufferedReader br = new BufferedReader(fr);
			
			String supplierStr = br.readLine().trim();
			
			br.close();
			fr.close();
			
			return supplierStr;
			
			
		} catch (IOException e) {
			return "failed to get supplier";
		}
	}
	
}
