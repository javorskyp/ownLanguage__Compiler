
class LLVMGenerator{
   
   static String header_text = "";
   static String main_text = "";
   static int reg = 1;

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

   static String scanfInt(){
      main_text += "%"+reg+" = alloca i32\n";
      reg++;
      main_text += "%"+reg+" = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strpi, i32 0, i32 0), i32* %"+(reg-1)+")\n";
      reg++;
      main_text += "%"+reg+" = load i32, i32* %"+(reg-2)+"\n";
      String newId = "%"+reg;
      reg++;
      return newId;
   }

   static String scanfReal(){
      main_text += "%"+reg+" = alloca double\n";
      reg++;
      main_text += "%"+reg+" = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strpd, i32 0, i32 0), double* %"+(reg-1)+")\n";
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
      main_text += "%"+reg+" = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @strpd, i32 0, i32 0), double "+(id)+")\n";
      reg++;
   }


   static String generate(){
      String text;
      text = "declare i32 @printf(i8*, ...)\n";
      text += "declare i32 @__isoc99_scanf(i8*, ...)\n";
      text += "@strpi = constant [4 x i8] c\"%d\\0A\\00\"\n";
      text += "@strpd = constant [4 x i8] c\"%f\\0A\\00\"\n";
      text += header_text;
      text += "define i32 @main() nounwind{\n";
      text += main_text;
      text += "ret i32 0 }\n";
      return text;
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
}
