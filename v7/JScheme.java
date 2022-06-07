/*
Cautious Crustaceans :: Orion Roven, Joshua Yagupsky, Jonathan Song
APCS pd7
Final Project: Scheme interpreter
time spent: .5 hrs
*/
import java.util.Scanner;
import java.util.Arrays;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.util.Stack;
import java.util.HashMap;
import java.util.Collections;
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
      sc.useDelimiter("\\s+");
      String next = "";
      while (sc.hasNext()) {
        next = sc.next();
        if(next.length()>1 && next.charAt(0) == '('){
          next = " ( " + next.substring(1);
        }
        if(next.length()>1 && next.charAt(next.length()-1) == ')'){
          next = next.substring(0,next.length() - 1)+ " ) ";
        }
        in += " " + next + " ";
      }
      List<String> tokens = Arrays.asList(in.split("\\s+"));
      for (String t : tokens) {
      	System.out.println(t);
      }
      Interpreter.flippydoo(tokens);
      Collections.reverse(tokens);
      for (String t : tokens) {
      	System.out.println(t);
      }
      //Interpreter master = new Interpreter(tokens);
      //System.out.println(master.evaluate());
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
    if (op.equals("and") || op.equals("or") || op.equals("not")) {
			_evalStack.push(this.boolify(op));
		}
  }
  private void addVar(){
    String name = _evalStack.pop();
    String value = _evalStack.pop();
    int type = 0;
    if (!isValuey(value)){
      type = _vars.get(value).getType();
      value = _vars.get(value).getValue();
    }
    else if (isInty(value)) {
      type = Value.INTEGER;
    }
    else if (isBooly(value)){type = Value.BOOLEAN;}
    else {
      type = Value.FLOAT;
    }
    _vars.put(name, new Value(type, value));
    _evalStack.pop();
   }
  private String boolify (String op) {
    String first = _evalStack.pop();
    if (!isBooly(first)){ first = _vars.get(first).getValue();}
    if (op.equals("not")){
      _evalStack.pop(); // Clear open paren
      if (first.equals("true")){
        return "false";
      }
      else {
        return "true";
      }
    }
    if (op.equals("or")){
      String next = first;
      boolean runningTotal = false;
      while (!next.equals(")")){
        if (!isBooly(next)){ next = _vars.get(next).getValue();}
        runningTotal = runningTotal || Boolean.parseBoolean(next);
        next = _evalStack.pop();
      }
      return runningTotal + "";
    }
    if (op.equals("and")){
      String next = first;
      boolean runningTotal = true;
      while (!next.equals(")")){
        if (!isBooly(next)){ next = _vars.get(next).getValue();}
        runningTotal = runningTotal && Boolean.parseBoolean(next);
        next = _evalStack.pop();
      }
      return runningTotal + "";
    }
    return "false";
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
  public static boolean isValuey(String s){return isFloaty(s) || isInty(s) || isBooly(s);}
  public static boolean isBooly(String s){
    if (s.equals("true") || s.equals("false")){return true;} return false;
  }
  public static void flippydoo(List<String> input){
    int start = 0;
    int count = 0;
    for (int i = 0; i < input.size(); i++){
      if(input.get(i).equals("(")){
        if (count == 0) {
          start = i;
        }
        count ++;
      }
      if(input.get(i).equals(")")){
        count --;
        if (count == 0) {
          flippydoo(input.subList(start + 1, i));
          Collections.reverse(input.subList(start + 1, i));
        }
      }
    }
    Collections.reverse(input);
  }

}

class Value{
  private int _type;
  private String _value;
  public static final int INTEGER = 0;
  public static final int FLOAT = 1;
  public static final int BOOLEAN = 2;
  public Value(int type, String value){_type = type; _value = value;}
  public int getType(){return this._type;}
  public String getValue(){return this._value;}
}
