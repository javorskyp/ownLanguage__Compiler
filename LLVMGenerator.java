import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

class LLVMGenerator{
   
   static String header_text = "";
   static String main_text = "";
   static int reg = 1;
   static int br = 0;
   static Map<Integer,Integer> else_counter_map = new HashMap<>();
   static int end_counter = 0;
   static Stack<Integer> brStack = new Stack<Integer>();
   static Stack<Integer> endStack = new Stack<Integer>();
   static Stack<Integer> whileStack = new Stack<>();

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
      main_text += "%"+id+" = alloca ["+(size+1)+" x i8], align 1\n";
   }

   static void assign_string_char(String id, int val, int size, int index) {
      main_text += "%"+reg+" = getelementptr inbounds [" + (size+1) + " x i8], [" + (size+1) + " x i8]* %" + id + ", i64 0, i64 " + index + "\n";
      ++reg;
      main_text += "store i8 "+val+", i8* %"+(reg-1)+", align 1\n";
   }

   static void declare_int32_array(String id, int size) {
      main_text += "%"+id+"=alloca [" + size + " x i32], align 16\n";
   }

   static void declare_double_array(String id, int size) {
      main_text += "%" + id + " = alloca [" + size + " x double], align 16\n";
   }

   static void assign_arr_el_i32(String id, String val, String index, int size){
      main_text += "%"+reg+" = getelementptr inbounds [" + size + " x i32], [" + size + " x i32]* %" + id + ", i64 0, i64 " + index + "\n";
      ++reg;
      main_text += "store i32 "+val+", i32* %"+(reg-1)+", align 16\n";
   }

   static void assign_arr_el_double(String id, String val, String index, int size){
      main_text += "%"+reg+" = getelementptr inbounds [" + size + " x double], [" + size + " x double]* %" + id + ", i64 0, i64 " + index + "\n";
      ++reg;
      main_text += "store double "+val+", double* %"+(reg-1)+", align 16\n";
   }

   static String loadReal(String id) {
      main_text += "%"+reg+" = load double, double* %"+id+"\n";
      String newId = "%"+reg;
      reg++;
      return newId;
   }

   static String loadInt(String id) {
      main_text += "%"+reg+" = load i32, i32* %"+id+"\n";
      String newId = "%"+reg;
      reg++;
      return newId;
   }

   static String loadIntEl(String id, String index, int size) {
      main_text += "%"+reg+" = getelementptr inbounds [" + size + " x i32], [" + size + " x i32]* %" + id + ", i64 0, i64 " + index + "\n";
      String newId = String.valueOf(reg);
      reg++;
      return newId;
   }

   static String loadRealEl(String id, String index, int size) {
      main_text += "%"+reg+" = getelementptr inbounds [" + size + " x double], [" + size + " x double]* %" + id + ", i64 0, i64 " + index + "\n";
      String newId = String.valueOf(reg);
      reg++;
      return newId;
   }


   static String scanfInt(){
      main_text += "%"+reg+" = alloca i32\n";
      reg++;
      main_text += "%"+reg+" = call i32 (i8*, ...) @scanf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strpi, i32 0, i32 0), i32* %"+(reg-1)+")\n";
      reg++;
      main_text += "%"+reg+" = load i32, i32* %"+(reg-2)+"\n";
      String newId = "%"+reg;
      reg++;
      return newId;
   }

   static String scanfReal(){
      main_text += "%"+reg+" = alloca double\n";
      reg++;
      main_text += "%"+reg+" = call i32 (i8*, ...) @scanf(i8* getelementptr inbounds ([5 x i8], [5 x i8]* @strpd, i32 0, i32 0), double* %"+(reg-1)+")\n";
      reg++;
      main_text += "%"+reg+" = load double, double* %"+(reg-2)+"\n";
      String newId = "%"+reg;
      reg++;
      return newId;
   }

   static void printInt(String id){
//      main_text += "%"+reg+" = load i32, i32* %"+id+"\n";
//      reg++;
      main_text += "%"+reg+" = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strpi, i32 0, i32 0), i32 "+(id)+")\n";
      reg++;
   }

   static void printDouble(String id){
//      main_text += "%"+reg+" = load double, double* %"+id+"\n";
//      reg++;
      main_text += "%"+reg+" = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([5 x i8], [5 x i8]* @strpd, i32 0, i32 0), double "+(id)+")\n";
      reg++;
   }

   static void printString(String id, int size) {
      main_text += "%" + reg + " = getelementptr inbounds [" + (size + 1) + " x i8], [" + (size + 1) + " x i8]* %" + id + ", i64 0, i64 0\n";
      reg++;
      main_text += "%" + reg + " = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strps, i64 0, i64 0), i8* %" + (reg - 1) + ")\n";
      reg++;
   }

   static void declare_i32(String id){
      main_text += "%"+id+" = alloca i32\n";
   }

   static void declare_double(String id){
      main_text += "%"+id+" = alloca double\n";
   }

   static void assign_i32(String id, String value){
      main_text += "store i32 "+value+", i32* %"+id+"\n";
   }

   static void assign_double(String id, String value){
      main_text += "store double "+value+", double* %"+id+"\n";
   }

   static void sum_i32(String value1, String value2){
      main_text += "%"+reg+" = add i32 "+value1+", "+value2+"\n";
      reg++;
   }

   static void sum_double(String value1, String value2){
      main_text += "%"+reg+" = fadd double "+value1+", "+value2+"\n";
      reg++;
   }

   static void subtract_i32(String val1, String val2){
      main_text += "%"+reg+" = sub i32 "+val2+", "+val1+"\n";
      reg++;
   }

   static void subtract_double(String val1, String val2){
      main_text += "%"+reg+" = fsub double "+val2+", "+val1+"\n";
      reg++;
   }

   static void multiply_i32(String value1, String value2){
      main_text += "%"+reg+" = mul i32 "+value1+", "+value2+"\n";
      reg++;
   }

   static void multiply_double(String value1, String value2){
      main_text += "%"+reg+" = fmul double "+value1+", "+value2+"\n";
      reg++;
   }

   static void divide_i32(String val1, String val2){
      main_text += "%"+reg+" = sdiv i32 "+val2+", "+val1+"\n";
      reg++;
   }

   static void divide_double(String val1, String val2){
      main_text += "%"+reg+" = fdiv double "+val2+", "+val1+"\n";
      reg++;
   }

   static void sitofp(String id){
      main_text += "%"+reg+" = sitofp i32 "+id+" to double\n";
      reg++;
   }

   static void fptosi(String id){
      main_text += "%"+reg+" = fptosi double "+id+" to i32\n";
      reg++;
   }

   static void ieq(Value val1, Value val2){
      main_text += "%"+reg+" = icmp eq i32 "+val1.name+", "+val2.name+"\n";
      reg++;
   }

   static void ileq(Value val1, Value val2){
      main_text += "%"+reg+" = icmp sle i32 "+val1.name+", "+val2.name+"\n";
      reg++;
   }

   static void igeq(Value val1, Value val2){
      main_text += "%"+reg+" = icmp sge i32 "+val1.name+", "+val2.name+"\n";
      reg++;
   }

   static void ineq(Value val1, Value val2){
      main_text += "%"+reg+" = icmp ne i32 "+val1.name+", "+val2.name+"\n";
      reg++;
   }

   static void ile(Value val1, Value val2){
      main_text += "%"+reg+" = icmp slt i32 "+val1.name+", "+val2.name+"\n";
      reg++;
   }

   static void ige(Value val1, Value val2){
      main_text += "%"+reg+" = icmp sgt i32 "+val1.name+", "+val2.name+"\n";
      reg++;
   }

   static void req(Value val1, Value val2){
      main_text += "%"+reg+" = fcmp eq double "+val1.name+", "+val2.name+"\n";
      reg++;
   }

   static void rleq(Value val1, Value val2){
      main_text += "%"+reg+" = fcmp sle double "+val1.name+", "+val2.name+"\n";
      reg++;
   }

   static void rgeq(Value val1, Value val2){
      main_text += "%"+reg+" = fcmp sge double "+val1.name+", "+val2.name+"\n";
      reg++;
   }

   static void rneq(Value val1, Value val2){
      main_text += "%"+reg+" = fcmp ne double "+val1.name+", "+val2.name+"\n";
      reg++;
   }

   static void rle(Value val1, Value val2){
      main_text += "%"+reg+" = fcmp slt double "+val1.name+", "+val2.name+"\n";
      reg++;
   }

   static void rge(Value val1, Value val2){
      main_text += "%"+reg+" = fcmp sgt double "+val1.name+", "+val2.name+"\n";
      reg++;
   }

   static void ifStart() {
      br++;
      incrementElseCounter(br);
      end_counter++;
      brStack.push(br);
      endStack.push(end_counter);
      main_text += "br i1 %"+(reg-1)+", label %true"+br+"_"+getElseCounter(br)+", label %false"+br+"_"+getElseCounter(br)+"\n";
      main_text += "true"+br+"_"+getElseCounter(br)+":\n";
   }

   static void elsifStart() {
      int b = brStack.peek();
      incrementElseCounter(b);
      main_text += "br i1 %"+(reg-1)+", label %true"+b+"_"+getElseCounter(b)+", label %false"+b+"_"+getElseCounter(b)+"\n";
      main_text += "true"+br+"_"+getElseCounter(b)+":\n";
   }


   static void elseStart() {
      incrementElseCounter(brStack.peek());
      // main_text += "br i1 %"+(reg-1)+", label %true"+br+"_"+else_counter+", label %false"+br+"_"+else_counter+"\n";
   }

   static void ifEnd() {
      int b = brStack.peek();
      main_text += "br label %end"+endStack.peek()+"\n";
      main_text += "false"+br+"_"+getElseCounter(b)+":\n";
   }

   static void elsifEnd() {
      int b = brStack.peek();
      main_text += "br label %end"+endStack.peek()+"\n";
      main_text += "false"+b+"_"+getElseCounter(b)+":\n";
   }
//
//
//   static void elseEnd() {
//      main_text += "br label %end"+endStack.peek()+"\n";
//   }

   static void endConditional() {
      int b = brStack.pop();
      int end = endStack.pop();
      main_text += "br label %end"+end+"\n";
      main_text += "end"+end+":\n";
      incrementElseCounter(b);
      ++end_counter;
   }

   static void startRepeat(int repeatCount) {
      String counter = Integer.toString(reg);
      declare_i32(counter);
      ++reg;
      assign_i32(counter, "0");
      br++;
      main_text += "br label %cond"+br+"\n";
      main_text += "cond"+br+":\n";

      String newId = loadInt(counter);
      sum_i32(newId, "1");
      assign_i32(counter, "%"+(reg-1));

      main_text+="%"+reg+" = icmp slt i32 %"+(reg-2)+", "+repeatCount+"\n";
      reg++;

      main_text+="br i1 %"+(reg-1)+", label %true"+br+", label %false"+br+"\n";
      main_text+="true"+br+":\n";
      brStack.push(br);
   }

   static void endRepeat(){
      int b = brStack.pop();
      main_text+="br label %cond"+b+"\n";
      main_text+="false"+b+":\n";
   }
}
