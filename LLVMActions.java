import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

enum VarType{ INT, REAL, INT_ARRAY, REAL_ARRAY, STRING }

class Value{
    public String name;
    public VarType type;
    public int size = 0;
    public Value( String name, VarType type, int size){
        this.name = name;
        this.type = type;
        this.size = size;
    }
    public Value( String name, VarType type ){
        this(name, type, 0);
    }
}

public class LLVMActions extends BaseLanBaseListener {

    HashMap<String, VarType> variables = new HashMap<>();
    HashMap<String, Integer> complexVarSize = new HashMap<>();
    Stack<Value> stack = new Stack<>();

    @Override public void exitProg(BaseLanParser.ProgContext ctx) {
        System.out.println(LLVMGenerator.generate());
    }

    // Mikolaj
    @Override public void exitAssign(BaseLanParser.AssignContext ctx) {
        String ID = ctx.ID().getText();
        Value v = stack.pop();
        boolean alreadyDefined = false;
        if(variables.containsKey(ID)) {
            alreadyDefined = true;
            if(v.type != variables.get(ID))
                error(ctx.getStart().getLine(), "can't assign value of type "+v.type+" to variable "+ID+" of type "+variables.get(ID));
        }
        else{
            variables.put(ID, v.type);
        }

        if(v.type == VarType.STRING) {
            String value = v.name;
            char[] charValue = value.substring(1, value.length()-1).toCharArray();
            if(alreadyDefined) {
                error(ctx.getStart().getLine(), "can not redeclare string");
            }
            complexVarSize.put(ID, charValue.length);
            LLVMGenerator.declare_string(ID, charValue.length);
            int index = 0;
            for(char i : charValue) {
                LLVMGenerator.assign_string_char(ID, i, charValue.length, index);
                ++index;
            }
            LLVMGenerator.assign_string_char(ID, '\0', charValue.length, index);
        }

        if(v.type == VarType.INT) {
            if(!alreadyDefined)
                LLVMGenerator.declare_i32(ID);
            LLVMGenerator.assign_i32(ID, v.name);
        }

        if(v.type == VarType.REAL) {
            if(!alreadyDefined)
                LLVMGenerator.declare_double(ID);
            LLVMGenerator.assign_double(ID, v.name);
        }
    }

    @Override public void exitString(BaseLanParser.StringContext ctx) {
        String value = ctx.STRING().getText();
        stack.push(new Value(value, VarType.STRING));
    }

    @Override public void exitDeclareRealArray(BaseLanParser.DeclareRealArrayContext ctx) {
        String ID = ctx.ID().getText();
        if(variables.containsKey(ID)) {
            error(ctx.getStart().getLine(), "cant re-declare an array");
        }
        List<String> els = ctx.REAL().stream().map(TerminalNode::getText).collect(Collectors.toList());
        int length = els.size()+1;
        LLVMGenerator.declare_double_array(ID, length);

        int index = 0;
        for(String el : els) {
            LLVMGenerator.assign_arr_el_double(ID, el, String.valueOf(index), length);
            ++index;
        }
        variables.put(ID, VarType.REAL_ARRAY);
        complexVarSize.put(ID, length);
    }

    @Override public void exitDeclareIntArray(BaseLanParser.DeclareIntArrayContext ctx) {
        String ID = ctx.ID().getText();
        if(variables.containsKey(ID)) {
            error(ctx.getStart().getLine(), "cant re-declare an array");
        }
        List<String> els = ctx.INT()
                .stream()
                .map(TerminalNode::getText)
                .collect(Collectors.toList());
        int length = els.size()+1;
        LLVMGenerator.declare_int32_array(ID, length);

        int index = 0;
        for(String el : els) {
            LLVMGenerator.assign_arr_el_i32(ID, el, String.valueOf(index), length);
            ++index;
        }
        variables.put(ID, VarType.INT_ARRAY);
        complexVarSize.put(ID, length);
    }

    @Override public void exitAssignArrayEl(BaseLanParser.AssignArrayElContext ctx) {
        String ID = ctx.ID().getText();
        String index = ctx.INT().getText();

        if(!variables.containsKey(ID)) {
            error(ctx.getStart().getLine(), "array does not exist");
        }

        VarType type = variables.get(ID);
        Value val = stack.pop();
        int length = complexVarSize.get(ID);
        if(!doesValueTypeMatchArrayType(val.type, type)) {
            error(ctx.getStart().getLine(), "array of type "+type+"does not accept values of type "+val.type);
        }

        if(type == VarType.REAL_ARRAY) {
            LLVMGenerator.assign_arr_el_double(ID, val.name, index, length);
        }
        if(type == VarType.INT_ARRAY) {
            LLVMGenerator.assign_arr_el_i32(ID, val.name, index, length);
        }
    }

    @Override public void exitReadInt(BaseLanParser.ReadIntContext ctx) {
        String ID = LLVMGenerator.scanfInt();
        stack.push(new Value(ID, VarType.INT));
    }

    @Override public void exitReadReal(BaseLanParser.ReadRealContext ctx) {
        String ID = LLVMGenerator.scanfReal();
        stack.push(new Value(ID, VarType.REAL));
    }

    @Override public void exitIdRef(BaseLanParser.IdRefContext ctx) {
        String ID = ctx.ID().getText();

        if(!variables.containsKey(ID)) {
            error(ctx.getStart().getLine(), "variable "+ID+" does not exist");
        }

        VarType type = variables.get(ID);

        if( type == VarType.REAL ) {
            ID = LLVMGenerator.loadReal(ID);
            stack.push(new Value(ID, type));
        }

        if(type == VarType.INT) {
            ID = LLVMGenerator.loadInt(ID);
            stack.push(new Value(ID, type));
        }

        if(type == VarType.STRING) {
            stack.push(new Value(ID, type));
        }
    }

    @Override public void exitArrayElRef(BaseLanParser.ArrayElRefContext ctx) {
        String ID = ctx.ID().getText();

        if(!variables.containsKey(ID)) {
            error(ctx.getStart().getLine(), "array "+ID+" does not exist");
        }

        VarType type = variables.get(ID);
        String index = ctx.INT().getText();
        int length = complexVarSize.get(ID);

        if( type == VarType.REAL_ARRAY ) {
            ID = LLVMGenerator.loadRealEl(ID, index, length);
            ID = LLVMGenerator.loadReal(ID);
            stack.push(new Value(ID, VarType.REAL));
        }

        if(type == VarType.INT_ARRAY) {
            ID = LLVMGenerator.loadIntEl(ID, index, length);
            ID = LLVMGenerator.loadInt(ID);
            stack.push(new Value(ID, VarType.INT));
        }
    }

    @Override public void exitToInt(BaseLanParser.ToIntContext ctx) {
        Value val = stack.pop();
        LLVMGenerator.fptosi(val.name);
        stack.push(new Value("%"+(LLVMGenerator.reg-1), VarType.INT));
    }

    @Override public void exitToReal(BaseLanParser.ToRealContext ctx) {
        Value val = stack.pop();
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
        Value val1 = stack.pop();
        Value val2 = stack.pop();
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
        Value val1 = stack.pop();
        Value val2 = stack.pop();
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
        Value val1 = stack.pop();
        Value val2 = stack.pop();
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
            error(ctx.getStart().getLine(), "incorrect sum type, val1 type: "+val1.type+" val2 type: "+val2.type);
        }
     }
	@Override public void exitSubtract(BaseLanParser.SubtractContext ctx) {
        Value val1 = stack.pop();
        Value val2 = stack.pop();
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

    @Override public void exitPrint(BaseLanParser.PrintContext ctx) {
        Value val = stack.pop();
        if(val.type == VarType.INT) {
            LLVMGenerator.printInt(val.name);
        }
        if(val.type == VarType.REAL) {
            LLVMGenerator.printDouble(val.name);
        }
        if(val.type == VarType.STRING) {
            int size = 0;
            String ID = val.name;
            if(!variables.containsKey(val.name)){
                String value = val.name;
                char[] charValue = value.substring(1, value.length()-1).toCharArray();
                ID = String.valueOf(LLVMGenerator.reg);
                LLVMGenerator.reg++;
                LLVMGenerator.declare_string(ID, charValue.length);
                int index = 0;
                for(char i : charValue) {
                    LLVMGenerator.assign_string_char(ID, i, charValue.length, index);
                    ++index;
                }
                LLVMGenerator.assign_string_char(ID, '\0', charValue.length, index);
                size = charValue.length;
            }
            else{
                size = complexVarSize.get(val.name);
            }
            LLVMGenerator.printString(ID, size);
        }
    }
    @Override public void exitSingle0(BaseLanParser.Single0Context ctx) { }

    void error(int line, String msg){
        System.err.println("Error, line "+line+", "+msg);
        System.exit(1);
    }

    boolean doesValueTypeMatchArrayType(VarType valType, VarType arrType) {
        return (valType == VarType.INT && arrType == VarType.INT_ARRAY)
                || (valType == VarType.REAL && arrType == VarType.REAL_ARRAY);
    }
}
