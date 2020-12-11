# jetpreter
Interpreter of custom script language with map/reduce, lambdas and GUI.

**Program example:**
```
var n = 500
var sequence = map({0, n}, i -> (-1)^i / (2 * i + 1))
var pi = 4 * reduce(sequence, 0, x y -> x + y)
print "pi = "
out pi
```
**How to build and run:**
```
mvn clean package
java -jar interpreter_gui/target/interpreter_gui-1.0.jar
```

**Screenshots**

![Windows](https://github.com/borunovv/jetpreter/blob/master/screenshots/win01.jpg "Version 1.0 on Windows")

![Web](https://github.com/borunovv/jetpreter/blob/master/screenshots/web01.jpg "Version 1.0 on Web")
