/*
Cautious Crusaceans :: Orion Roven, Joshua Yagupsky, Jonathan Song
APCS pd7
Final Project: Scheme interpreter
time spent: .5 hrs
*/
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
public class JScheme {
  public static void main(String[] args) {
    String in = "";
    if(args.length == 0) {
      System.out.println("No file entered, nothing to interpret");
      return;
    }
    try {
      File f = new File(args[0]);
      Scanner sc = new Scanner(f);
      String next = "";
      while (sc.hasNext()) {
        next = sc.next();
        in += " ";
        if (next.charAt(0) == '(') {
          in += "( ";
          next = next.substring(1);
        }
        if (next.charAt(next.length()-1) == ')') {
          in += next.substring(0, next.length()-1) + " )";
        }
        else {
          in += next;
        }
      }
      String[] tokens = in.split("\\s+");
      for (String t : tokens) {
        System.out.println(t);
      }
    }
    catch(FileNotFoundException e) {
      System.out.println("Error: Could not find file " + e.getMessage());
      return;
    }
  }
}
