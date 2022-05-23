all: clean generate compile test

generate:
	java -jar antlr-4.9.3-complete.jar -o . BaseLan.g4

compile:
	javac -cp antlr-4.9.3-complete.jar:. *.java

test:
	java -cp antlr-4.9.3-complete.jar:. Main testfile.test >> testout.ll
	lli testout.ll

clean:
	rm -f *.class
	rm -f a.out
	rm -f *.ll
	rm -f *.s
	rm -f *.tokens
	rm -f *.bc

