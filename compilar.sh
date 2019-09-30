#!/bin/bash

find . -iname "*.java" > sources.txt
find . -type f -name "*.class" -exec rm -f {} \;
javac -cp ./src:./lib/asm-all-3.3.jar:./lib/jdom.jar -encoding ISO-8859-1 @sources.txt
rm sources.txt
