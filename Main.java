import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Main {
    public static void main(String[] args) throws Exception {
        ANTLRFileStream input = new ANTLRFileStream(args[0]);

        BaseLanLexer lexer = new BaseLanLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BaseLanParser parser = new BaseLanParser(tokens);

        ParseTree tree = parser.prog(); 

//        System.out.println(tree.toStringTree(parser));

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new LLVMActions(), tree);
    }
}