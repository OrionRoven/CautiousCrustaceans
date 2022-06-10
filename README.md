Cautious Crusaceans :: Orion Roven, Joshua Yagupsky, Jonathan Song
APCS pd7
Final Project: Scheme interpreter
time spent: .5 hrs

## Concise Project Description:
Scheme interpreter

## How To Launch:
$ javac JScheme.java

$ java JScheme YourFile.scm
OR
$ java JScheme "put your code in here"

## Keywords
* pass: Does nothing, used for if statements: (pass)
* break: Breaks out of current loop: (break)
* let: Assigns a value to a variable: (let variable value)
* println: Prints out the value or variable: (println expression)
* if: Evaluates one of two blocks of code based on the value of a boolean expression: (if (true block) (false block) boolean)
* loop: Repeats a code block forever, unless the code block uses the break keyword: (loop (block))

In addition, there are mathematical operations (+,-,*,/) along with boolean operations (<,>,=,and,or,not). To see an example program, look at euler.scm, which calculates Euler's constant, e.