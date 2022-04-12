all: clean generate compile

generate:
	java -jar /antlr-4.9.3-complete.jar BaseLan.g4
	javac -cp /antlr-4.9.3-complete.jar *.java

clean:
	rm -f *.class
	rm -f a.out
	rm -f *.ll
	rm -f *.s
	rm -f PLwypisz*.java
	rm -f *.tokens
	rm -f *.bc

