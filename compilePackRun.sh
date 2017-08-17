#!/bin/sh

# translate to bytecode
# compile *.java files in src/ to *.class files in bin/
javac -d bin src/main/java/*.java

cd bin

# pack all *.class files into a jar, telling where to find main().
jar cfe csvconverter.jar main.java.Main main/java/*.class

cd ..

# execute the jar
java -jar bin/csvconverter.jar
