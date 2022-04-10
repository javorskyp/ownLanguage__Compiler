
class LLVMGenerator{
   
   static String header_text = "";
   static String main_text = "";
   static int reg = 1;

   static void print(String text){
      int str_len = text.length();
      String str_type = "["+(str_len+2)+" x i8]";  
      header_text += "@str"+reg+" = constant"+str_type+" c\""+text+"\\0A\\00\"\n";
      main_text += "call i32 (i8*, ...) @printf(i8* getelementptr inbounds ( "+str_type+", "+str_type+"* @str"+reg+", i32 0, i32 0))\n";
      reg++;
   }

   static String generate(){
      String text;
      text = "declare i32 @printf(i8*, ...)\n";
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

   static void sitofp(String id){
      main_text += "%"+reg+" = sitofp i32 "+id+" to double\n";
      reg++;
   }

   static void fptosi(String id){
      main_text += "%"+reg+" = fptosi double "+id+" to i32\n";
      reg++;
   }
}
