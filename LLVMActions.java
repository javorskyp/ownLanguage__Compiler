
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.HashMap;
import java.util.Stack;

enum VarType{ INT, REAL, UNKNOWN }

class Value{
    public String name;
    public VarType type;
    public Value( String name, VarType type ){
        this.name = name;
        this.type = type;
    }
}

public class LLVMActions extends BaseLanBaseListener {

    HashMap<String, VarType> variables = new HashMap<>();
    Stack<Value> stack = new Stack<>();

    // COPYPASTE
    @Override public void exitProg(BaseLanParser.ProgContext ctx) { }
	@Override public void exitEveryRule(ParserRuleContext ctx) { }

    // Mikolaj
    @Override public void exitAssign(BaseLanParser.AssignContext ctx) { }
    @Override public void exitPar(BaseLanParser.ParContext ctx) { }
    @Override public void exitReadInt(BaseLanParser.ReadIntContext ctx) { }
    @Override public void exitReadReal(BaseLanParser.ReadRealContext ctx) { }
    @Override public void exitReadId(BaseLanParser.ReadIdContext ctx) { }
    @Override public void exitToInt(BaseLanParser.ToIntContext ctx) { }
    @Override public void exitToReal(BaseLanParser.ToRealContext ctx) { }
    @Override public void exitInt(BaseLanParser.IntContext ctx) { }
    @Override public void exitReal(BaseLanParser.RealContext ctx) { }

    // Pawel
    @Override public void exitMultiply(BaseLanParser.MultiplyContext ctx) { }
	@Override public void exitDivide(BaseLanParser.DivideContext ctx) { }
    @Override public void exitSum(BaseLanParser.SumContext ctx) { }
	@Override public void exitSubtract(BaseLanParser.SubtractContext ctx) { }
    @Override public void exitSingle1(BaseLanParser.Single1Context ctx) { }
    @Override public void exitPrint(BaseLanParser.PrintContext ctx) { }
    @Override public void exitSingle0(BaseLanParser.Single0Context ctx) { }
}
