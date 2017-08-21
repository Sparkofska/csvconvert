#!/bin/bash

# create bin/ directory if not exists
if [ ! -d bin ]; then
	mkdir bin
fi

# enable the ** syntax for following commands
shopt -s globstar

# translate to bytecode
# compile *.java files in src/ to *.class files in bin/
javac -d bin src/main/java/**/*.java

# pack all *.class files into a jar, telling where to find main().
cd bin
jar cfe csvtransform.jar main.java.Main main/java/**/*.class
cd ..
