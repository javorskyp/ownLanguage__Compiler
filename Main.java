import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Main {
    public static void main(String[] args) throws Exception {
        ANTLRFileStream input = new ANTLRFileStream(args[0]);

        PLwypiszLexer lexer = new PLwypiszLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PLwypiszParser parser = new PLwypiszParser(tokens);

        ParseTree tree = parser.prog(); 

//        System.out.println(tree.toStringTree(parser));

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new LLVMActions(), tree);

    }
}