all: generate compile test clean

generate:
	java -jar antlr-4.9.3-complete.jar -o outputFiles BaseLan.g4

compile:
	javac -cp antlr-4.9.3-complete.jar:outputFiles:. *.java

test:
	java -cp antlr-4.9.3-complete.jar:outputFiles:. Main testfile.test >> testout.ll
	lli testout.ll

clean:
	rm -f *.class
	rm -f a.out
	rm -f *.ll
	rm -f *.s
	rm -f *.tokens
	rm -f *.bc
	rm -rf outputFiles
