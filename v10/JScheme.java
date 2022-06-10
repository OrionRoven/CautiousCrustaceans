/*
Cautious Crustaceans :: Orion Roven, Joshua Yagupsky, Jonathan Song
APCS pd7
Final Project: Scheme interpreter
time spent: see devlog
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
    List<String> tokens;
    if(args.length == 0) {
      System.out.println("No file or code entered, nothing to interpret");
      return;
    }
    try {
      File f = new File(args[0]);
      Scanner sc = new Scanner(f);
      tokens = tokenize(sc);
    }
    catch(FileNotFoundException e) {
      Scanner inp = new Scanner(args[0]);
      tokens = tokenize(inp);
    }
    Interpreter.preProcess(tokens);
    //System.out.println(tokens.toString());
    Interpreter master = new Interpreter(tokens);
    master.evaluate();
  }
  public static List<String> tokenize(Scanner sc){
    sc.useDelimiter("");
    String next = "";
    String token = "";
    List<String> tokens = new ArrayList<String>();
    while (sc.hasNext()) {
      next = sc.next();
      if(next.matches("\\s+")){
        if(token.length() > 0){
          tokens.add(token.trim());
          token = "";
        }
        continue;
      }
      if(next.equals("(")||next.equals(")")){
        if(token.length() > 0){
          tokens.add(token.trim());
          token = "";
        }
        tokens.add(next.trim());
        continue;
      }
      token += next;
    }
    return tokens;
  }
}

//Interpreter: Takes in a stream of tokens and evaluates them on a stack
class Interpreter {
  private List<String> _tokens;
  private Stack<String> _evalStack;
  private HashMap<String, Value> _vars;
  public Interpreter(List<String> tokens) {
    _tokens = tokens;
    _evalStack = new Stack<>();
    _vars = new HashMap<>();
  }
  private Interpreter(List<String> tokens, HashMap<String, Value> vars){
    _tokens = tokens;
    _evalStack = new Stack<>();
    _vars = vars;
  }
  public static void preProcess(List<String> tokens){
    //System.out.println(tokens.toString());
    flippydo(tokens);
    //System.out.println(tokens.toString());
    ifParse(tokens);
    //System.out.println(tokens.toString());
    loopParse(tokens);
    //System.out.println(tokens.toString());
  }
  //True means evaluated without problem, false means "break" was used.
  public boolean evaluate() {
    String op = "";
    boolean cond = true;
    int count = 0;
    int midpoint = 0;
    int pos = 0;
    Interpreter helper;
    List<String> subtokens;
    for (int i = _tokens.size() - 1; i >= 0; i--) {
      if (this._tokens.get(i).equals("loop")){
        count =1;
        pos= i-2;
        while(count != 0){
          if(_tokens.get(pos).equals(")")){
            count++;
          }
          if(_tokens.get(pos).equals("(")){
            count--;
          }
          pos--;
        }
        subtokens = _tokens.subList(pos +2, i -1);
        //System.out.println(subtokens.toString());
        helper = new Interpreter(subtokens, _vars);
        while(cond){
          cond = helper.evaluate();
        }
        i = pos -1;
        continue;
      }
      if (this._tokens.get(i).equals("if")){
        cond = Boolean.parseBoolean(_evalStack.pop());
        //System.out.println(cond);
        if(! cond){
          //False condition
          //System.out.println("False!");
          count=1;
          pos = i-2;
          while(count != 0){
            if(_tokens.get(pos).equals(")")){
              count++;
            }
            if(_tokens.get(pos).equals("(")){
              count--;
            }
            pos--;
          }
          subtokens = _tokens.subList(pos +2, i -1);
          //System.out.println(subtokens.toString());
          helper = new Interpreter(subtokens, _vars);
          if(!helper.evaluate()) {return false;}
          count = 1;
          pos--; 
          while(count != 0){
            if(_tokens.get(pos).equals(")")){
              count++;
            }
            if(_tokens.get(pos).equals("(")){
              count--;
            }
            pos--;
          }
          i = pos -1;
          continue;
        }
        else{
          //True condition
          count=1;
          pos = i-2;
          while(count != 0){
            if(_tokens.get(pos).equals(")")){
              count++;
            }
            if(_tokens.get(pos).equals("(")){
              count--;
            }
            pos--;
          }
          midpoint = pos;
          count = 1;
          pos --;
          while(count != 0){
            if(_tokens.get(pos).equals(")")){
              count++;
            }
            if(_tokens.get(pos).equals("(")){
              count--;
            }
            pos--;
          }
          subtokens = _tokens.subList(pos +2, midpoint);
          //System.out.println(subtokens.toString());
          helper = new Interpreter(subtokens, _vars);
          if(!helper.evaluate()) {return false;}
          i = pos -1;
          continue;
        }
      }
      if (i < 0){return false;}
      if (this._tokens.get(i).equals("(")) {
        op = _evalStack.pop();
        if(!op.equals("break") && op != null){
          //System.out.println(op);
          this.unpack(op);
        }
        else{
          return false;
        }
        //System.out.println("Unpacking!");
      }
      else{
        _evalStack.push(this._tokens.get(i));
      }
    }
    return true;
  }
  private String varDecode(String s){
    if(!isValuey(s)){
      return _vars.get(s).getValue();
    }
    return s;
  }
  private void unpack(String op){
    if (op.equals("pass")) {
      _evalStack.pop();
      return;
    }
    if (op.equals("let")){
      this.addVar();
    }
    if (op.equals("println")){
      System.out.println(varDecode(_evalStack.pop()));
      _evalStack.pop();
    }
    if (op.equals("=") || op.equals("<") || op.equals(">")){
      _evalStack.push(this.compareify(op));
    }
    if (op.equals("*") || op.equals("-") || op.equals("+") || op.equals("/")) {
      //System.out.println("Mathifying!");
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
  private String compareify (String op) {
    String first = varDecode(_evalStack.pop());
    String second = varDecode(_evalStack.pop());
    if(op.equals("=")){
      return String.valueOf(Double.parseDouble(first) == Double.parseDouble(second));
    }
    else if(op.equals("<")){
      return String.valueOf(Double.parseDouble(first) <  Double.parseDouble(second));
    }
    else {
      return String.valueOf(Double.parseDouble(first) >  Double.parseDouble(second));
    }
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
  public static void flippydo(List<String> input){
    //Get the subrange that needs flippydoing in the first place
    int first = 0;
    int last = input.size()-1;
    while(first < input.size() && !input.get(first).equals("(")){
      first ++;
    }
    while(last >= 0 && !input.get(last).equals(")")){
      last--;
    }
    if(last <= first){return;}
//    System.out.println(""+ first+last);
    List<String> region = input.subList(first, last + 1);
    int start = 0;
    int count = 0;
    Collections.reverse(region);
    for(int i =0; i< region.size(); i++){
      if(region.get(i).equals(")")){
        if(count == 0){
          start = i;
        }
        count++;
      }
      if(region.get(i).equals("(")){
        count--;
        if(count==0){
          Collections.reverse(region.subList(start, i+1));
          //flippydo(region.subList(start +1, i));
        }
      }
    }
  }
  public static void loopParse(List<String> input){
    int count =0;
    int pos = 0;
    List<String> subtokens;
    for(int i = 0; i < input.size(); i++){
      if(input.get(i).equals("loop")){
        count ++;
        pos = i+2;
        while(count != 0){
          if(input.get(pos).equals("(")){
            count++;
          }
          if(input.get(pos).equals(")")){
            count--;
          }
          pos++;
        }
        subtokens = input.subList(i+2, pos -1);
        flippydo(subtokens);
        Collections.rotate(input.subList(i, pos),-1);
        i = pos;
      }
    }
  }
  public static void ifParse(List<String> input){
    int count = 0;
    int pos = 0;
    int midpoint = 0;
    for(int i = 0; i < input.size(); i++){
      if(input.get(i).equals("if")){
        count++;
        pos = i+2;
        while(count != 0){
          if(input.get(pos).equals("(")){
            count++;
          }
          if(input.get(pos).equals(")")){
            count--;
          }
          pos++;
        }
        midpoint = pos;
        count ++;
        pos ++;
        preProcess(input.subList(i+2, midpoint-1));
        while(count != 0){
          if(input.get(pos).equals("(")){
            count++;
          }
          if(input.get(pos).equals(")")){
            count--;
          }
          pos++;
        }
        flippydo(input.subList(midpoint +1, pos -1));
        Collections.rotate(input.subList(i, pos), -1);
        i = pos;
        count = 0;
        midpoint = 0;
      }
    }
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
