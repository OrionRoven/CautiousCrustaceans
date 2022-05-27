import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
public class JScheme {
  public static void main(String[] args) {
    String in = "";
    try {
      File f = new File(args[0]);
      Scanner sc = new Scanner(f);
      while (sc.hasNext()) {
        in += " " + sc.next();
      }
      String[] tokens = in.split("\\s");
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
