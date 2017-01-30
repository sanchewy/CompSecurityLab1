
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Keinan
 */
public class Driver {
    public static void main(String[] args) {
        DumpReader r = new DumpReader();
        List<String> output = null;
        try {

            r.readFile("D:\\NetBeansProjects\\CompSecurityLab1\\lib\\memorydump.dmp");

//            r.readFile("/Users/Keinan/NetBeansProjects/CompSecurityLab1/lib/test.txt");
            r.readFile("/Users/Keinan/NetBeansProjects/CompSecurityLab1/lib/memorydump.dmp");

        } catch (Exception ex) {
            Logger.getLogger(Driver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
