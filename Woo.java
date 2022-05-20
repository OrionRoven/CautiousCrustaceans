import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
public class Woo {
  public static void main(String[] args) {
    File wordleWords = new File("C:/words.txt");
    Scanner sc = new Scanner(wordleWords);
    ArrayList<String> words = new ArrayList<String>();
    for (int i = 0; sc.hasNextLine(); i++) {
      words.add(sc.nextLine().trim().split("\\s")[0]);
    }
    System.out.println(words.toString());
  }
}
