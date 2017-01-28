
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
    
    
    public void readFile(String filename) throws Exception
    {
        String file = filename;
        byte[] buffer = new byte[100];
        PrintWriter out = new PrintWriter("output.txt");
        FileInputStream is = new FileInputStream(filename);
        ArrayList<String> saveLines = new ArrayList<String>();

        while(is.read(buffer) != -1){ //First pass gets the track 1 data
            String s = new String(buffer);
            if(s.contains ("%B")) {
                String s1 = s;
                while(addLine(s1) == null) {
                    is.read(buffer);
                    s1 = s1.concat(new String(buffer));
                }
                saveLines.add(addLine(s1));
            }   
        }
        String[][] t1info = track1sep(saveLines);
        for(int i = 0; i < t1info[0].length; i++) {
            System.out.println(t1info[6][i]);
            
        }
        System.out.println(t1info);
//        is.reset();
//        while(is.read(buffer) != -1) { //Second pass gets track2 data
//            String s = new String(buffer);
//            if(s.contains)
//        } 
       System.out.println(saveLines);
    }
    public String addLine(String s) {        
        int startLoc = 0;
        boolean sls = false;
        int endLoc = 0;
        char[] ch = s.toCharArray();
        String out = null;
        
        for(int i = 0; i < ch.length; i++) {
            if(ch[i] == '%' && startLoc == 0){
                startLoc = i;
                sls = true;
            }
            else if(ch[i] == '?'){
                if(i > startLoc && sls && endLoc == 0){
                    endLoc = i; 
                    break;
                }
            }
            else if(ch[i] == ';') {
                if(i > startLoc && sls && endLoc == 0){
                    endLoc = i-1; 
                    break;
                }
            }
        }
        if(startLoc < endLoc){    //If track1 data is good
            out = s.substring(startLoc, endLoc+1);   //Save track 1 data
        }
        return out;  //will be null
    }
    
    public String[][] track1sep(ArrayList<String> a) {
        //a.size #entries and 9 fields per entry | SS | FC |  PAN  | FS |   First Name | Last Name  | FS |  Addi6onal Data | ES | LR |
        String[][] accInfo = new String[a.size()][13];
        for(int i = 0; i < a.size(); i++) {     //step through entries
            String l = a.get(i);
            int curInd = 0;
            if(l.charAt(curInd) == '%' && l.charAt(curInd+1) == 'B') {
                    accInfo[i][0] = "%";
                    accInfo[i][1] = "B";
            }
            curInd = 2;
            String accNum = "";
            for(int j = curInd; j < curInd + 19; j++) {       //Account # <= 19 digits
                if(l.charAt(j) == '^') {
                     accInfo[i][2] = accNum;
                     accInfo[i][3] = "^";
                     curInd = j;
                     break;
                 }else {
                    accNum += (Character.toString(l.charAt(j)));
                     accNum.concat(""+l.charAt(j));
                }
            }
            String firstName = "";
            int digLeft = 26;
            for(int j = ++curInd; j < curInd + 26; j++) {         //First name
                if(l.charAt(j) == '/'){
                    accInfo[i][4] = firstName;
                    accInfo[i][5] = "/";
                    digLeft--;
                    curInd = j++;
                    break;
                }
                else {
                    firstName += (Character.toString(l.charAt(j)));
                    digLeft--;
                }
            }
            String secondName = "";
            for(int j = ++curInd; j < curInd + digLeft; j++) {         //Last name
                if(l.charAt(j) == '^'){
                    accInfo[i][6] = secondName;
                    accInfo[i][7] = "^";
                    curInd = j+1;
                    break;
                }
                else {
                    secondName += (Character.toString(l.charAt(j)));
                }
            }
            accInfo[i][8] = ""+l.charAt(curInd)+l.charAt(++curInd);     //2 digit expire year
            accInfo[i][9] = ""+l.charAt(++curInd)+l.charAt(++curInd);       //2 didgit expire month
            accInfo[i][10] = ""+l.charAt(++curInd)+l.charAt(++curInd)+l.charAt(++curInd);   //3 digit service code
            
            while(l.charAt(++curInd) != '?' && curInd < l.length()-1) {                //discressionay information
                if(accInfo[i][11] == null) {
                    accInfo[i][11] = "";
                }
                accInfo[i][11] += Character.toString(l.charAt(curInd));
            }
            if(l.charAt(curInd) == '?') {
              accInfo[i][12] = "?";  
            }  
        }
        return accInfo;
    }
}
