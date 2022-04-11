import java.util.HashMap;
import java.util.Stack;

enum VarType{ INT, REAL }

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

    @Override public void exitProg(BaseLanParser.ProgContext ctx) {
        System.out.println(LLVMGenerator.generate());
    }

    // Mikolaj
    @Override public void exitAssign(BaseLanParser.AssignContext ctx) {
        var ID = ctx.ID().getText();
        var v = stack.pop();
        variables.put(ID, v.type);
        if(v.type == VarType.INT) {
            LLVMGenerator.declare_i32(ID);
            LLVMGenerator.assign_i32(ID, v.name);
        }
        if(v.type == VarType.REAL) {
            LLVMGenerator.declare_double(ID);
            LLVMGenerator.assign_double(ID, v.name);
        }
    }

    @Override public void exitReadInt(BaseLanParser.ReadIntContext ctx) {
        var ID = ctx.ID().getText();
        var type = variables.get(ID);
        if(type == null) {
            variables.put(ID, VarType.INT);
            LLVMGenerator.declare_i32(ID);
        }
        else if(type != VarType.INT) {
            error(ctx.getStart().getLine(), "variable of type: "+type+", expected REAL");
        }
        LLVMGenerator.scanfInt(ID);
    }

    @Override public void exitReadReal(BaseLanParser.ReadRealContext ctx) {
        var ID = ctx.ID().getText();
        var type = variables.get(ID);
        if(type == null) {
            variables.put(ID, VarType.REAL);
            LLVMGenerator.declare_double(ID);
        }
        else if(type != VarType.REAL) {
            error(ctx.getStart().getLine(), "variable of type: "+type+", expected REAL");
        }
        LLVMGenerator.scanfReal(ID);
    }

    @Override public void exitIdRef(BaseLanParser.IdRefContext ctx) {
        var ID = ctx.ID().getText();
        var type = variables.get(ID);
        if( type != null ) {
            stack.push(new Value(ID, type));
        }
        else {
            error(ctx.getStart().getLine(), "variable uninitialized");
        }
    }

    @Override public void exitToInt(BaseLanParser.ToIntContext ctx) {
        var val = stack.pop();
        LLVMGenerator.fptosi(val.name);
        stack.push(new Value("%"+(LLVMGenerator.reg-1), VarType.INT));
    }

    @Override public void exitToReal(BaseLanParser.ToRealContext ctx) {
        var val = stack.pop();
        LLVMGenerator.sitofp(val.name);
        stack.push(new Value("%"+(LLVMGenerator.reg-1), VarType.REAL));
    }

    @Override public void exitInt(BaseLanParser.IntContext ctx) {
        stack.push(new Value(ctx.INT().getText(), VarType.INT));
    }

    @Override public void exitReal(BaseLanParser.RealContext ctx) {
        stack.push(new Value(ctx.REAL().getText(), VarType.REAL));
    }

    // Pawel
    @Override public void exitMultiply(BaseLanParser.MultiplyContext ctx) { 
        var val1 = stack.pop();
        var val2 = stack.pop();
        if( val1.type == val2.type ) {
            if(val1.type == VarType.INT){
                LLVMGenerator.multiply_i32(val1.name, val2.name);
                stack.push( new Value("%"+(LLVMGenerator.reg-1), VarType.INT) );
            }
            if(val1.type == VarType.REAL){
                LLVMGenerator.multiply_double(val1.name, val2.name);
                stack.push( new Value("%"+(LLVMGenerator.reg-1), VarType.REAL) );
            }
        } else {
            error(ctx.getStart().getLine(), "incorrect multiply type");
        }
    }
	@Override public void exitDivide(BaseLanParser.DivideContext ctx) {
        var val1 = stack.pop();
        var val2 = stack.pop();
        if( val1.type == val2.type ) {
            if(val1.type == VarType.INT){
                LLVMGenerator.divide_i32(val1.name, val2.name);
                stack.push( new Value("%"+(LLVMGenerator.reg-1), VarType.INT) );
            }
            if(val1.type == VarType.REAL){
                LLVMGenerator.divide_double(val1.name, val2.name);
                stack.push( new Value("%"+(LLVMGenerator.reg-1), VarType.REAL) );
            }
        } else {
            error(ctx.getStart().getLine(), "incorrect divide type");
        }
    }
    @Override public void exitSum(BaseLanParser.SumContext ctx) {
        var val1 = stack.pop();
        var val2 = stack.pop();
        if( val1.type == val2.type ) {
            if( val1.type == VarType.INT ){
                LLVMGenerator.sum_i32(val1.name, val2.name);
                stack.push( new Value("%"+(LLVMGenerator.reg-1), VarType.INT) );
            }
            if( val1.type == VarType.REAL ){
                LLVMGenerator.sum_double(val1.name, val2.name);
                stack.push( new Value("%"+(LLVMGenerator.reg-1), VarType.REAL) );
            }
        } else {
            error(ctx.getStart().getLine(), "incorrect sume type");
        }
     }
	@Override public void exitSubtract(BaseLanParser.SubtractContext ctx) { 
        var val1 = stack.pop();
        var val2 = stack.pop();
        if( val1.type == val2.type ) {
            if(val1.type == VarType.INT){
                LLVMGenerator.subtract_i32(val1.name, val2.name);
                stack.push( new Value("%"+(LLVMGenerator.reg-1), VarType.INT) );
            }
            if(val1.type == VarType.REAL){
                LLVMGenerator.subtract_double(val1.name, val2.name);
                stack.push( new Value("%"+(LLVMGenerator.reg-1), VarType.REAL) );
            }
        } else {
            error(ctx.getStart().getLine(), "incorrect subtract type");
        }
    }
    @Override public void exitSingle1(BaseLanParser.Single1Context ctx) { }
    @Override public void exitPrint(BaseLanParser.PrintContext ctx) { }
    @Override public void exitSingle0(BaseLanParser.Single0Context ctx) { }

    void error(int line, String msg){
        System.err.println("Error, line "+line+", "+msg);
        System.exit(1);
    }
}
