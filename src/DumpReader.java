
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Keinan
 */
public class DumpReader {
    
    ArrayList<String> saveLines = new ArrayList<String>();
    
    public void readFile(String filename) throws Exception
        {
            String file = filename;
            byte[] buffer = new byte[100];
            PrintWriter out = new PrintWriter("output.txt");
            FileInputStream is = new FileInputStream(filename);
            while(is.read(buffer) != -1){
                String s = new String(buffer);
                if(s.contains ("%B")) {
                    String s1 = s;
                    while(!addLine(s1)) {
                        is.read(buffer);
                        s1 = s1.concat(new String(buffer));
                    }
                }   
                out.println(new String(buffer));
            }
            System.out.println(saveLines);
        }
    public boolean addLine(String s) {        
        int startLoc = 0;
        boolean sls = false;
        int endLoc = 0;
        char[] ch = s.toCharArray();
        for(int i = 0; i < ch.length; i++) {
            if(ch[i] == '%' && startLoc == 0){
                startLoc = i;
                sls = true;
            }
            else if(ch[i] == '?' || ch[i] == ';'){
                if(i > startLoc && sls && endLoc == 0){
                    endLoc = i; 
                    break;
                }
            }
        }
        if(startLoc < endLoc){
            System.out.println("Before: "+s);
            System.out.println("After: "+s.substring(startLoc, endLoc+1));
            saveLines.add(s.substring(startLoc, endLoc));
            return true;
        }
        return false;
    }
}
