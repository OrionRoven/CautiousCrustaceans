/*
Cautious Crustaceans :: Orion Roven, Joshua Yagupsky, Jonathan Song
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
    if (op.equals("let")){
      this.addVar();
    }
    if (op.equals("*") || op.equals("-") || op.equals("+") || op.equals("/")) {
      _evalStack.push(this.mathify(op));
    }
  }
  private void addVar(){
    String name = _evalStack.pop();
    String value = _evalStack.pop();
    int type = 0;
    if (isInty(value)) {
      type = Value.INTEGER;
    }
    else {
      type = Value.FLOAT;
    }
    _vars.put(name, new Value(type, value));
    _evalStack.pop();
  }
  private String mathify(String op){
    String first = _evalStack.pop();
    //If first isn't a value, try to get one from the HashMap
    boolean doubleMode = false;
    if(!isValuey(first)){
      Value temp = _vars.get(first);
      first = temp.getValue();
      doubleMode = doubleMode || temp.getType() == Value.FLOAT;
    }
    double runningTotal = Double.parseDouble(first);
    String next = _evalStack.pop();
    if(!isValuey(next)){
      Value temp = _vars.get(next);
      next = temp.getValue();
      doubleMode = doubleMode || temp.getType() == Value.FLOAT;
    }
    doubleMode = !isInty(first);
    while(! next.equals(")")){
      if(!isValuey(next)){
        Value temp = _vars.get(next);
        next = temp.getValue();
        doubleMode = doubleMode || temp.getType() == Value.FLOAT;
      }
      if (doubleMode) {
        if (op.equals("*")) {
          runningTotal *= Double.parseDouble(next);
        }
        else if (op.equals("-")){
          runningTotal -= Double.parseDouble(next);
        }
        else if (op.equals("+")) {
          runningTotal += Double.parseDouble(next);
        }
        else {
          runningTotal /= Double.parseDouble(next);
        }
      }
      else if (!isInty(next)) {
        doubleMode = true;
        continue;
      }
      else {
        int u = Integer.parseInt(next);
        if (op.equals("*")) {
            runningTotal = ((int) runningTotal) * u;
          }
        else if (op.equals("-")){
            runningTotal = ((int) runningTotal) - u;
        }
        else if (op.equals("+")) {
            runningTotal = ((int) runningTotal) + u;
        }
        else {
            runningTotal = ((int) runningTotal) / u;
        }
      }
      // if (op.equals("*")) {
      //   runningTotal *= Double.parseDouble(next);
      // }
      // else if (op.equals("-")){
      //   runningTotal -= Double.parseDouble(next);
      // }
      // else if (op.equals("+")) {
      //   runningTotal += Double.parseDouble(next);
      // }
      // else if (doubleMode) {
      //   runningTotal /= Double.parseDouble(next);
      // }
      // else {
      //   try {
      //     int u = Integer.parseInt(next);
      //     runningTotal = ((int) runningTotal) / u;
      //   }
      // }
      next = _evalStack.pop();
    }
    if (doubleMode) {
      return runningTotal + "";
    }
    else {
      return ((int) runningTotal) + "";
    }
  }
  public static boolean isInty(String s){
    if (s == null) {
        return false;
    }
    try {
        int u = Integer.parseInt(s);
    } catch (NumberFormatException nfe) {
        return false;
    }
    return true;
  }
  public static boolean isFloaty(String s){
    if (s == null) {
        return false;
    }
    try {
        double u = Double.parseDouble(s);
    } catch (NumberFormatException nfe) {
        return false;
    }
    return true;
  }
  public static boolean isValuey(String s){return isFloaty(s) || isInty(s);}
}

class Value{
  private int _type;
  private String _value;
  public static final int INTEGER = 0;
  public static final int FLOAT = 1;
  public Value(int type, String value){_type = type; _value = value;}
  public int getType(){return this._type;}
  public String getValue(){return this._value;}
}
