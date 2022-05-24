import java.util.*;

class LLVMGenerator{
   
   static String header_text = "";
   static String main_text = "";
   static int reg = 1;
   static int global_reg = 1;
   static int br = 0;
   static Map<Integer,Integer> else_counter_map = new HashMap<>();
   static int end_counter = 0;
   static Stack<Integer> brStack = new Stack<Integer>();
   static Stack<Integer> endStack = new Stack<Integer>();
   static Stack<Integer> whileStack = new Stack<>();
   static Boolean global = true;

   private static void incrementElseCounter(int level) {
      if(else_counter_map.get(level) == null) {
         else_counter_map.put(level, 0);
         return;
      }
      int current = else_counter_map.get(level);
      else_counter_map.put(level, ++current);
   }

   private static int getElseCounter(int level) {
      return else_counter_map.get(level);
   }

   static String generate(){
      String text;
      text = "declare i32 @printf(i8*, ...)\n";
      text += "declare i32 @scanf(i8*, ...)\n";
      text += "@strpi = constant [4 x i8] c\"%d\\0A\\00\"\n";
      text += "@strpd = constant [5 x i8] c\"%lf\\0A\\00\"\n";
      text += "@strps = private unnamed_addr constant [4 x i8] c\"%s\\0A\\00\"\n";
      text += header_text;
      text += "define i32 @main() nounwind{\n";
      text += main_text;
      text += "ret i32 0 }\n";
      return text;
   }

   static void declare_string(String id, int size){
      update_text( "%"+id+" = alloca ["+(size+1)+" x i8], align 1\n");
   }

   static void assign_string_char(String id, int val, int size, int index) {
      update_text( "%"+reg+" = getelementptr inbounds [" + (size+1) + " x i8], [" + (size+1) + " x i8]* %" + id + ", i64 0, i64 " + index + "\n");
      ++reg;
      update_text( "store i8 "+val+", i8* %"+(reg-1)+", align 1\n");
   }

   static void declare_int32_array(String id, int size) {
      update_text( "%"+id+"=alloca [" + size + " x i32], align 16\n");
   }

   static void declare_double_array(String id, int size) {
      update_text( "%" + id + " = alloca [" + size + " x double], align 16\n");
   }

   static void assign_arr_el_i32(String id, String val, String index, int size){
      update_text( "%"+reg+" = getelementptr inbounds [" + size + " x i32], [" + size + " x i32]* %" + id + ", i64 0, i64 " + index + "\n");
      ++reg;
      update_text( "store i32 "+val+", i32* %"+(reg-1)+", align 16\n");
   }

   static void assign_arr_el_double(String id, String val, String index, int size){
      update_text( "%"+reg+" = getelementptr inbounds [" + size + " x double], [" + size + " x double]* %" + id + ", i64 0, i64 " + index + "\n");
      ++reg;
      update_text( "store double "+val+", double* %"+(reg-1)+", align 16\n");
   }

   static String loadReal(String id) {
      update_text( "%"+reg+" = load double, double* %"+id+"\n");
      String newId = "%"+reg;
      reg++;
      return newId;
   }

   static String loadInt(String id) {
      update_text( "%"+reg+" = load i32, i32* %"+id+"\n");
      String newId = "%"+reg;
      reg++;
      return newId;
   }

   static String loadIntEl(String id, String index, int size) {
      update_text( "%"+reg+" = getelementptr inbounds [" + size + " x i32], [" + size + " x i32]* %" + id + ", i64 0, i64 " + index + "\n");
      String newId = String.valueOf(reg);
      reg++;
      return newId;
   }

   static String loadRealEl(String id, String index, int size) {
      update_text( "%"+reg+" = getelementptr inbounds [" + size + " x double], [" + size + " x double]* %" + id + ", i64 0, i64 " + index + "\n");
      String newId = String.valueOf(reg);
      reg++;
      return newId;
   }


   static String scanfInt(){
      update_text( "%"+reg+" = alloca i32\n");
      reg++;
      update_text( "%"+reg+" = call i32 (i8*, ...) @scanf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strpi, i32 0, i32 0), i32* %"+(reg-1)+")\n");
      reg++;
      update_text( "%"+reg+" = load i32, i32* %"+(reg-2)+"\n");
      String newId = "%"+reg;
      reg++;
      return newId;
   }

   static String scanfReal(){
      update_text( "%"+reg+" = alloca double\n");
      reg++;
      update_text( "%"+reg+" = call i32 (i8*, ...) @scanf(i8* getelementptr inbounds ([5 x i8], [5 x i8]* @strpd, i32 0, i32 0), double* %"+(reg-1)+")\n");
      reg++;
      update_text( "%"+reg+" = load double, double* %"+(reg-2)+"\n");
      String newId = "%"+reg;
      reg++;
      return newId;
   }

   static void printInt(String id){
//      update_text( "%"+reg+" = load i32, i32* %"+id+"\n");
//      reg++;
      update_text( "%"+reg+" = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strpi, i32 0, i32 0), i32 "+(id)+")\n");
      reg++;
   }

   static void printDouble(String id){
//      update_text( "%"+reg+" = load double, double* %"+id+"\n");
//      reg++;
      update_text( "%"+reg+" = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([5 x i8], [5 x i8]* @strpd, i32 0, i32 0), double "+(id)+")\n");
      reg++;
   }

   static void printString(String id, int size) {
      update_text( "%" + reg + " = getelementptr inbounds [" + (size + 1) + " x i8], [" + (size + 1) + " x i8]* %" + id + ", i64 0, i64 0\n");
      reg++;
      update_text( "%" + reg + " = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strps, i64 0, i64 0), i8* %" + (reg - 1) + ")\n");
      reg++;
   }

   static void declare_i32(String id){
      update_text( "%"+id+" = alloca i32\n");
   }

   static void declare_double(String id){
      update_text( "%"+id+" = alloca double\n");
   }

   static void assign_i32(String id, String value){
      update_text( "store i32 "+value+", i32* %"+id+"\n");
   }

   static void assign_double(String id, String value){
      update_text( "store double "+value+", double* %"+id+"\n");
   }

   static void sum_i32(String value1, String value2){
      update_text( "%"+reg+" = add i32 "+value1+", "+value2+"\n");
      reg++;
   }

   static void sum_double(String value1, String value2){
      update_text( "%"+reg+" = fadd double "+value1+", "+value2+"\n");
      reg++;
   }

   static void subtract_i32(String val1, String val2){
      update_text( "%"+reg+" = sub i32 "+val2+", "+val1+"\n");
      reg++;
   }

   static void subtract_double(String val1, String val2){
      update_text( "%"+reg+" = fsub double "+val2+", "+val1+"\n");
      reg++;
   }

   static void multiply_i32(String value1, String value2){
      update_text( "%"+reg+" = mul i32 "+value1+", "+value2+"\n");
      reg++;
   }

   static void multiply_double(String value1, String value2){
      update_text( "%"+reg+" = fmul double "+value1+", "+value2+"\n");
      reg++;
   }

   static void divide_i32(String val1, String val2){
      update_text( "%"+reg+" = sdiv i32 "+val2+", "+val1+"\n");
      reg++;
   }

   static void divide_double(String val1, String val2){
      update_text( "%"+reg+" = fdiv double "+val2+", "+val1+"\n");
      reg++;
   }

   static void sitofp(String id){
      update_text( "%"+reg+" = sitofp i32 "+id+" to double\n");
      reg++;
   }

   static void fptosi(String id){
      update_text( "%"+reg+" = fptosi double "+id+" to i32\n");
      reg++;
   }

   static void ieq(Value val1, Value val2){
      update_text( "%"+reg+" = icmp eq i32 "+val1.name+", "+val2.name+"\n");
      reg++;
   }

   static void ileq(Value val1, Value val2){
      update_text( "%"+reg+" = icmp sle i32 "+val1.name+", "+val2.name+"\n");
      reg++;
   }

   static void igeq(Value val1, Value val2){
      update_text( "%"+reg+" = icmp sge i32 "+val1.name+", "+val2.name+"\n");
      reg++;
   }

   static void ineq(Value val1, Value val2){
      update_text( "%"+reg+" = icmp ne i32 "+val1.name+", "+val2.name+"\n");
      reg++;
   }

   static void ile(Value val1, Value val2){
      update_text( "%"+reg+" = icmp slt i32 "+val1.name+", "+val2.name+"\n");
      reg++;
   }

   static void ige(Value val1, Value val2){
      update_text( "%"+reg+" = icmp sgt i32 "+val1.name+", "+val2.name+"\n");
      reg++;
   }

   static void req(Value val1, Value val2){
      update_text( "%"+reg+" = fcmp oeq double "+val1.name+", "+val2.name+"\n");
      reg++;
   }

   static void rleq(Value val1, Value val2){
      update_text( "%"+reg+" = fcmp ole double "+val1.name+", "+val2.name+"\n");
      reg++;
   }

   static void rgeq(Value val1, Value val2){
      update_text( "%"+reg+" = fcmp oge double "+val1.name+", "+val2.name+"\n");
      reg++;
   }

   static void rneq(Value val1, Value val2){
      update_text( "%"+reg+" = fcmp one double "+val1.name+", "+val2.name+"\n");
      reg++;
   }

   static void rle(Value val1, Value val2){
      update_text( "%"+reg+" = fcmp olt double "+val1.name+", "+val2.name+"\n");
      reg++;
   }

   static void rge(Value val1, Value val2){
      update_text( "%"+reg+" = fcmp ogt double "+val1.name+", "+val2.name+"\n");
      reg++;
   }

   static void ifStart() {
      br++;
      incrementElseCounter(br);
      end_counter++;
      brStack.push(br);
      endStack.push(end_counter);
      update_text( "br i1 %"+(reg-1)+", label %true"+br+"_"+getElseCounter(br)+", label %false"+br+"_"+getElseCounter(br)+"\n");
      update_text( "true"+br+"_"+getElseCounter(br)+":\n");
   }

   static void elsifStart() {
      int b = brStack.peek();
      incrementElseCounter(b);
      update_text( "br i1 %"+(reg-1)+", label %true"+b+"_"+getElseCounter(b)+", label %false"+b+"_"+getElseCounter(b)+"\n");
      update_text( "true"+br+"_"+getElseCounter(b)+":\n");
   }


   static void elseStart() {
      incrementElseCounter(brStack.peek());
      // update_text( "br i1 %"+(reg-1)+", label %true"+br+"_"+else_counter+", label %false"+br+"_"+else_counter+"\n");
   }

   static void ifEnd() {
      int b = brStack.peek();
      update_text( "br label %end"+endStack.peek()+"\n");
      update_text( "false"+br+"_"+getElseCounter(b)+":\n");
   }

   static void elsifEnd() {
      int b = brStack.peek();
      update_text( "br label %end"+endStack.peek()+"\n");
      update_text( "false"+b+"_"+getElseCounter(b)+":\n");
   }
//
//
//   static void elseEnd() {
//      update_text( "br label %end"+endStack.peek()+"\n");
//   }

   static void endConditional() {
      int b = brStack.pop();
      int end = endStack.pop();
      update_text( "br label %end"+end+"\n");
      update_text( "end"+end+":\n");
      incrementElseCounter(b);
      ++end_counter;
   }

   static void startRepeat(int repeatCountId) {
      String counter = Integer.toString(reg);
      declare_i32(counter);
      ++reg;
      assign_i32(counter, "0");
      br++;
      update_text( "br label %cond"+br+"\n");
      update_text( "cond"+br+":\n");

      String newId = loadInt(counter);
      sum_i32(newId, "1");
      assign_i32(counter, "%"+(reg-1));

      update_text("%"+reg+" = icmp slt i32 %"+(reg-2)+", "+repeatCountId+"\n");
      reg++;

      update_text("br i1 %"+(reg-1)+", label %true"+br+", label %false"+br+"\n");
      update_text("true"+br+":\n");
      brStack.push(br);
   }

   static void endRepeat(){
      int b = brStack.pop();
      update_text("br label %cond"+b+"\n");
      update_text("false"+b+":\n");
   }

   static void declare_real_function(String ID) {
      update_text("define dso_local double @"+ID+"(");
   }

   static void exit_real_function(String ID) {
      update_text("ret double "+ID+"\n");
      update_text("}\n");
   }

   static void declare_int_function(String ID) {
      update_text("define dso_local i32 @"+ID+"(");
   }

   static void exit_int_function(String ID) {
      update_text("ret i32 "+ID+"\n");
      update_text("}\n");
   }

   private static void update_text(String text){
      if(global){
         main_text+=text;
      }
      else{
         header_text+=text;
      }
   }

   static List<String> prepareLocalVarsList(List<String> paramNames, List<VarType> paramTypes) {
      List<String> localNames = new ArrayList<>();
      for(int i = 0; i < paramNames.size(); ++i) {
         String valName = paramNames.get(i);
         String valId;
         valId = Integer.toString(reg);
         ++reg;
         if(paramTypes.get(i).equals(VarType.INT)){
            declare_i32(valId);
            assign_i32(valId, valName);
         }
         else{
            declare_double(valId);
            assign_double(valId, valName);
         }
         localNames.add(valId);
      }
      return localNames;
   }

   static String appendCallParamList(List<String> localNames, List<VarType> paramTypes){
      for(int i = 0; i < localNames.size(); ++i) {
         String valName = localNames.get(i);
         String valType = paramTypes.get(i).equals(VarType.REAL) ? "double*" : "i32*";
         update_text(valType+" %"+valName);
         if(i < localNames.size()-1){
            update_text(", ");
         }
      }
      update_text(")\n");
      String id = "%"+reg;
      reg++;
      return id;
   }

   static String call_real_function(String funId, List<String> paramNames, List<VarType> paramTypes) {
      //declare assign
      List<String> localNames = prepareLocalVarsList(paramNames, paramTypes);
      update_text("%"+reg+" = call double @"+funId+"(");
      return appendCallParamList(localNames, paramTypes);
   }

   static String call_int_function(String funId, List<String> paramNames, List<VarType> paramTypes) {
      List<String> localNames = prepareLocalVarsList(paramNames, paramTypes);
      update_text("%"+reg+" = call i32 @"+funId+"(");
      return appendCallParamList(localNames, paramTypes);
   }

   static void declare_fun_param_list(Queue<Value> functionParams) {
      while(!functionParams.isEmpty()){
         Value value = functionParams.remove();
         String type = value.type.equals(VarType.INT)? "i32*" : "double*";
         update_text(type+" %"+value.name);
         if(!functionParams.isEmpty()){
            update_text(", ");
         }
      }
      update_text(") {\n");
   }
}
