/*
Cautious Crusaceans :: Orion Roven, Joshua Yagupsky, Jonathan Song
APCS pd7
Final Project: Scheme interpreter
time spent: .5 hrs
*/
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Stack;
import java.util.HashMap;
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
      sc.useDelimiter(" ");
      String next = "";
      while (sc.hasNext()) {
        in += " " + sc.next();
      }
      String[] tokens = in.split("\\s+");
      // for (String t : tokens) {
      //   System.out.println(t);
      // }
      Interpreter master = new Interpreter(tokens);
      System.out.println(master.evaluate());
    }
    catch(FileNotFoundException e) {
      System.out.println("Error: Could not find file " + e.getMessage());
      return;
    }
  }
}

//Interpreter: Takes in a stream of tokens and evaluates them on a stack
class Interpreter {
  private String[] _tokens;
  private Stack<String> _evalStack;
  private HashMap<String, Value> _vars;
  public Interpreter(String[] tokens) {
    _tokens = tokens;
    _evalStack = new Stack<>();
    _vars = new HashMap<>();
  }
  public String evaluate() {
    String op = "";
    for (int i = _tokens.length - 1; i >= 1; i--) {
      if (this._tokens[i].equals("(")) {
        op = _evalStack.pop();
        this.unpack(op);
      }
      else{
        _evalStack.push(this._tokens[i]);
      }
    }

    return _evalStack.peek();
  }
  private void unpack(String op){
    if (op.equals("pass")) {
      _evalStack.pop();
      return;
    }
    if (op.equals("*") || op.equals("-") || op.equals("+")) {
      _evalStack.push(this.mathify(op));
    }
  }
  private String mathify(String op){
    int runningTotal = Integer.parseInt(_evalStack.pop());
    String next = _evalStack.pop();
    while(! next.equals(")")){
      if (op.equals("*")) {
        runningTotal *= Integer.parseInt(next);
      }
      else if (op.equals("-")){
        runningTotal -= Integer.parseInt(next);
      }
      else {
        runningTotal += Integer.parseInt(next);
      }
      next = _evalStack.pop();
    }
    return runningTotal + "";
  }
}

class Value{
  private int _type;
  private String _value;
  public static final int INTEGER = 0;
  public static final int FLOAT = 1;
  public static final int STRING = 2;
  public Value(int type, String value){_type = type; _value = value;}
}
