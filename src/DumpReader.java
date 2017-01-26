
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
            byte[] buffer = new byte[1000];
            PrintWriter out = new PrintWriter("output.txt");
            FileInputStream is = new FileInputStream(filename);
            while(is.read(buffer) != -1){
                System.out.println(new String(buffer));
                out.println(new String(buffer));
            }
        }
//            String dataFile = filename;
//
//            StringBuilder sb = new StringBuilder();
//            try (BufferedReader input = openFile(dataFile)) {
//                String line;
//                while ((line = input.readLine()) != null) {
//                    sb.append(line);
//                }
//            }
//
//            System.out.println("Data from file:\n" + sb);
//        }
//
//        private static BufferedReader openFile(String fileName)
//                throws IOException {
//            // Don't forget to add buffering to have better performance!
//            return new BufferedReader(
//                    new InputStreamReader(
//                            new FileInputStream(fileName),
//                            StandardCharsets.UTF_8));
//  
//        }
}
