package dao.textDB;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class fileClass{

	public static void main() throws IOException{

		try{
		  File file = new File("C:\\Users\\home401\\Downloads\\test.txt");
		  FileWriter filewriter = new FileWriter(file);

		  filewriter.write("‚±‚ñ‚É‚¿‚Í");

		  filewriter.close();
		}catch(IOException e){
		  System.out.println(e);
		}
	}
}
