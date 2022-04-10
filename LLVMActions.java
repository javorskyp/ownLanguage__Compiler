
import java.util.HashMap;

public class LLVMActions extends BaseLanBaseListener {

    HashMap<String, String> memory = new HashMap<String, String>();
    String value;

    // COPYPASTE
    @Override public void exitProg(BaseLanParser.ProgContext ctx) { }
	@Override public void exitEveryRule(ParserRuleContext ctx) { }

    // Mikolaj
    @Override public void exitInstruction(BaseLanParser.InstructionContext ctx) { }
	@Override public void exitAssign(BaseLanParser.AssignContext ctx) { }
	@Override public void exitExpr0(BaseLanParser.Expr0Context ctx) { }
	@Override public void exitExpr1(BaseLanParser.Expr1Context ctx) { }
    @Override public void exitExpr2(BaseLanParser.Expr2Context ctx) { }
	@Override public void exitInnerComponent(BaseLanParser.InnerComponentContext ctx) { }
    @Override public void exitCastToReal(BaseLanParser.CastToRealContext ctx) { }
    @Override public void exitCastToInt(BaseLanParser.CastToIntContext ctx) { }

    // Pawel
    @Override public void exitMultiply(BaseLanParser.MultiplyContext ctx) { }
	@Override public void exitDivide(BaseLanParser.DivideContext ctx) { }
	@Override public void exitSum(BaseLanParser.SumContext ctx) { }
	@Override public void exitSubtract(BaseLanParser.SubtractContext ctx) { }
	@Override public void exitRead(BaseLanParser.ReadContext ctx) { }
    @Override public void exitPrint(BaseLanParser.PrintContext ctx) { }
}
