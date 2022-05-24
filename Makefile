all: clean generate compile test

generate:
	java -jar antlr-4.9.3-complete.jar -o . BaseLan.g4

compile:
	javac -cp antlr-4.9.3-complete.jar:. *.java

test: test1 test2 test3

test1:
	java -cp antlr-4.9.3-complete.jar:. Main test1.test >> test1.ll
	lli test1.ll

test2:
	java -cp antlr-4.9.3-complete.jar:. Main test2.test >> test2.ll
	lli test2.ll

test3:
	java -cp antlr-4.9.3-complete.jar:. Main test3.test >> test3.ll
	lli test3.ll

clean:
	rm -f *.class
	rm -f a.out
	rm -f *.ll
	rm -f *.s
	rm -f *.tokens
	rm -f *.bc

