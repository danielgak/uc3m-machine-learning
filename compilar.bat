dir /s /b *.java > sources.txt
del *.class /s
javac -cp ./src;./lib/asm-all-3.3.jar;./lib/jdom.jar -encoding ISO-8859-1 @sources.txt


del sources.txt