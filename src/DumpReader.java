
import java.io.FileInputStream;
import java.io.PrintWriter;

import java.util.ArrayList;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.lang.*;
import static javax.script.ScriptEngine.FILENAME;


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
//<<<<<<< Updated upstream

    public void readFile(String filename) throws Exception {
        String file = filename;
        byte[] buffer = new byte[100];
        PrintWriter out = new PrintWriter("output.txt");
        FileInputStream is = new FileInputStream(filename);
        ArrayList<String> saveLines = new ArrayList<String>();
        ArrayList<String> saveLines2 = new ArrayList<String>();
        ArrayList<String> saveTrack2 = new ArrayList<String>();

        while (is.read(buffer) != -1) { //First pass gets the track 1 data
            String s = new String(buffer);
            if (s.contains("%B")) {
                String s1 = s;
                while (track1search(s1) == null) {
                    is.read(buffer);
                    s1 = s1.concat(new String(buffer));
                }
                saveLines.add(track1search(s1));
            }
        }
        is.close();
        String[][] t1info = track1sep(saveLines);
        
        FileInputStream is2 = new FileInputStream(filename);
        byte[] buffer2 = new byte[100];
        while (is2.read(buffer2) != -1 && is2.read(buffer2) != 0) { //Second pass gets track2 data
            is2.read(buffer2);
            String s = new String(buffer2);
            for (int i = 0; i < t1info.length; i++) {
                String ent = ";" + t1info[i][2];      //ent = ;account number
                if (s.contains(ent)) {
                    String s1 = s;
                    while (track2search(s) == null) {
                        is2.read(buffer2);
                        s1 = s1.concat(new String(buffer2));
                    }
                    saveLines2.add(track2search(s1));
                }
            }
        }
        is2.close();
        String[][] t2info = track2sep(saveLines2);

        for (int x = 0; x < t1info.length; x++) {   //goes through each possible card
            for (int y = 0; y < t1info.length; y++) {
                if (t1info[x][y] == null) //if any of the fields are null
                {
                    t1info[x][0] = null;    //set the first field the null
                }
            }
        }

        for (int x = 0; x < t2info.length; x++) {
            for (int y = 0; y < t2info.length; y++) {
                if (t2info[x][y] == null) //if any of the fields are null
                {
                    t2info[x][0] = null; //set the first field the null
                }
            }
        }

        for (int k = 0; k < t1info.length; k++) {
            if (t1info[k][0] == null || t2info[k][0] == null) { //if the first field in null
                //skip
            }
            else{   //prints out credit card information
                System.out.println("<Information from credit card number " + k + ">:");
                System.out.println("Cardholder's Name: " + t1info[k][4] + " " + t1info[k][6]);
                System.out.println("Card Number: " + t1info[k][2]);
                System.out.println("Expiration Date: " + t1info[k][9] + "/" + t1info[k][8]);
                System.out.println("Encrypted PIN: " + t2info[k][6]);
                System.out.println("CVV Number: " + t2info[k][7] + "\n");
            }
        }
    }

    public String track1search(String s) {
        int startLoc = 0;
        boolean sls = false;
        int endLoc = 0;
        char[] ch = s.toCharArray();
        String out = null;

        for (int i = 0; i < ch.length; i++) {
            if (ch[i] == '%' && startLoc == 0) {
                startLoc = i;
                sls = true;
            } else if (ch[i] == '?') {
                if (i > startLoc && sls && endLoc == 0) {
                    endLoc = i;
                    break;
                }
            } else if (ch[i] == ';') {
                if (i > startLoc && sls && endLoc == 0) {
                    endLoc = i - 1;
                    break;
                }
            }
        }
        if (startLoc < endLoc) {    //If track1 data is good
            out = s.substring(startLoc, endLoc + 1);   //Save track 1 data
        }
        return out;  //will be null
    }

    public String track2search(String s) {
        int startLoc = 0;
        boolean sls = false;
        int endLoc = 0;
        char[] ch = s.toCharArray();
        String out = null;

        for (int i = 0; i < ch.length; i++) {
            if (ch[i] == ';' && startLoc == 0) {
                startLoc = i;
                sls = true;
            } else if (ch[i] == '?') {
                if (i > startLoc && sls && endLoc == 0) {
                    endLoc = i;
                    break;
                }
            }
        }
        if (startLoc < endLoc) {    //If track1 data is good
            out = s.substring(startLoc, endLoc + 1);   //Save track 1 data
        }
        return out;  //will be null
    }

    public String[][] track1sep(ArrayList<String> a) {
        //a.size #entries and 9 fields per entry | SS | FC |  PAN  | FS |   First Name | Last Name  | FS |  Addi6onal Data | ES | LR |
        String[][] accInfo = new String[a.size()][13];
        for (int i = 0; i < a.size(); i++) {     //step through entries
            String l = a.get(i);
            int curInd = 0;
            if (l.charAt(curInd) == '%' && l.charAt(curInd + 1) == 'B') {
                accInfo[i][0] = "%";
                accInfo[i][1] = "B";
            }
            curInd = 2;
            String accNum = "";
            for (int j = curInd; j < curInd + 19; j++) {       //Account # <= 19 digits
                if (l.charAt(j) == '^') {
                    accInfo[i][2] = accNum;
                    accInfo[i][3] = "^";
                    curInd = j;
                    break;
                } else {
                    accNum += (Character.toString(l.charAt(j)));
//                     accNum.concat(""+l.charAt(j));
                }
            }
            String firstName = "";
            int digLeft = 26;
            for (int j = ++curInd; j < curInd + 26; j++) {         //First name
                if (l.charAt(j) == '/') {
                    accInfo[i][4] = firstName;
                    accInfo[i][5] = "/";
                    digLeft--;
                    curInd = j++;
                    break;
                } else {
                    firstName += (Character.toString(l.charAt(j)));
                    digLeft--;
                }
            }
            String secondName = "";
            for (int j = ++curInd; j < curInd + digLeft; j++) {         //Last name
                if (l.charAt(j) == '^') {
                    accInfo[i][6] = secondName;
                    accInfo[i][7] = "^";
                    curInd = j + 1;
                    break;
                } else {
                    secondName += (Character.toString(l.charAt(j)));
                }
            }
            accInfo[i][8] = "" + l.charAt(curInd) + l.charAt(++curInd);     //2 digit expire year
            accInfo[i][9] = "" + l.charAt(++curInd) + l.charAt(++curInd);       //2 didgit expire month
            accInfo[i][10] = "" + l.charAt(++curInd) + l.charAt(++curInd) + l.charAt(++curInd);   //3 digit service code

            while (l.charAt(++curInd) != '?' && curInd < l.length() - 1) {                //discressionay information
                if (accInfo[i][11] == null) {
                    accInfo[i][11] = "";
                }
                accInfo[i][11] += Character.toString(l.charAt(curInd));
            }
            if (l.charAt(curInd) == '?') {
                accInfo[i][12] = "?";
            }
        }
        return accInfo;
    }

    public String[][] track2sep(ArrayList<String> a) {      // | SS |  PAN  | FS |  Addi6onal Data | ES | LR
        String[][] accInfo = new String[a.size()][10];
        for (int i = 0; i < a.size(); i++) {     //step through entries
            try {
                String l = a.get(i);
                int curInd = 0;
                if (l.charAt(curInd) == ';') {       //Start character
                    accInfo[i][0] = ";";
                    curInd++;
                }
                String accNum = "";
                for (int j = curInd; j < curInd + 19; j++) {       //Account # <= 19 digits
                    if (l.charAt(j) == '=') {
                        accInfo[i][1] = accNum;
                        accInfo[i][2] = "=";
                        curInd = j;
                        break;
                    } else {
                        accNum += (Character.toString(l.charAt(j)));
                    }
                }
                accInfo[i][3] = "" + l.charAt(++curInd) + l.charAt(++curInd);     //2 digit expire year
                accInfo[i][4] = "" + l.charAt(++curInd) + l.charAt(++curInd);       //2 didgit expire month
                accInfo[i][5] = "" + l.charAt(++curInd) + l.charAt(++curInd) + l.charAt(++curInd);   //3 digit service code
                accInfo[i][6] = "" + l.charAt(++curInd) + l.charAt(++curInd) + l.charAt(++curInd) + l.charAt(++curInd);   //4 digit encrypted pin
                accInfo[i][7] = "" + l.charAt(++curInd) + l.charAt(++curInd) + l.charAt(++curInd);    // 3 digit CVV

                while (l.charAt(++curInd) != '?' && curInd < l.length() - 1) {                //discressionay information
                    if (accInfo[i][8] == null) {
                        accInfo[i][8] = "";
                    }
                    accInfo[i][8] += Character.toString(l.charAt(curInd));
                }
                if (l.charAt(curInd) == '?') {
                    accInfo[i][9] = "?";
                }

            } catch (IndexOutOfBoundsException e) {
                //do nothing. null field will be set and entry will not be valid
            }
        }
        return accInfo;
    }
//    public void readFile(String filename) throws Exception
//        {            
//            
//            String file = filename;
//            byte[] buffer = new byte[100];
//            PrintWriter out = new PrintWriter("output.txt");
//            FileInputStream is = new FileInputStream(filename);
//            while(is.read(buffer) != -1) {
//                String s = new String(buffer);
//                if(s.contains("%B")){
//                    System.out.println("Found one");
//                }
//            }
////            while(is.read(buffer) != -1){
////                String s = new String(buffer);
////                if(s.contains("%B")){
////                    byte[] buff2 = new byte[50];
////                    buff2 = 
////                }
////                String binary = new BigInteger(s.getBytes()).toString(2);
//////                System.out.println(s);
////                out.println(s);
////            }
//        }

}
