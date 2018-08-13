# jetpreter
Interpreter of simple script language with map/reduce and GUI.

Program example:

var n = 500<br/>
var sequence = map({0, n}, i -> (-1)^i / (2 * i + 1))<br/>
var pi = 4 * reduce(sequence, 0, x y -> x + y)<br/>
print "pi = "<br/>
out pi<br/>

How to build and run:

mvn clean package
java -jar interpreter_gui/target/interpreter_gui-1.0.jar
