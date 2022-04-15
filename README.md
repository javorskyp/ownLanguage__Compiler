# ownLanguage__Compiler
Creating a simple programming language and implementing its compiler

install extension from https://code.visualstudio.com/docs/languages/java to vscode if you use this editor
download ANTLR (Complete ANTLR 4.9.3 Java binaries jar) from www.antlr.org/download.html and put it into project folder
download LLVM https://github.com/llvm/llvm-project/releases/tag/llvmorg-14.0.0 and install (Windows) +200mb
	during installation choose "Add LLVM to the system Path for current user"

An example of a very simple activity of ANTLR + LLVM

1) Generating analyzers from a .g4 file
   ANTLR or ANTLR - Works Code Generation
$ java -jar ../antlr-4.4.2-complete.jar "grammarFile.name"

2) Compiling our compiler
$ javac -cp ../antlr-4.4.2-complete.jar :. *.Java

3) PL compiler -> ll
$ java -cp ../antlr-4.4.2-complete.jar :. Home example.PL> example.ll

4) We have two options to generate the executable code

   a) Byte code for LLVM
      $ llvm-as example.ll
      $ lli and example.bc

   b) Machine code
      $ llc example.ll
      $ clang example.s

   c) Interpretation
      $ lli and example.ll

Of course, all stages.

WSL Makefile
1. install    : sudo apt install make
2. generate   : make generate
3. compile    : make compile
4. test       : make test
5. test array : make testArray
6. test string: make testString
7. clean      : make clean

Be sure to have the appropriate antlr file path and the name according to the Makefile