all: generate compile test testArray testString clean

generate:
	java -jar antlr-4.9.3-complete.jar -o outputFiles BaseLan.g4

compile:
	javac -cp antlr-4.9.3-complete.jar:outputFiles:. *.java

test:
	java -cp antlr-4.9.3-complete.jar:outputFiles:. Main testfile.test >> testout.ll
	lli testout.ll

testArray:
	java -cp antlr-4.9.3-complete.jar:outputFiles:. Main testArray.test >> Arrayout.ll
	lli Arrayout.ll

testString:
	java -cp antlr-4.9.3-complete.jar:outputFiles:. Main testString.test >> Stringout.ll
	lli Stringout.ll

clean:
	rm -f *.class
	rm -f a.out
	rm -f *.ll
	rm -f *.s
	rm -f *.tokens
	rm -f *.bc
	rm -rf outputFiles
