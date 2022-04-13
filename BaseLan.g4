grammar BaseLan;

prog: ( stat? NEWLINE )*;

stat: ID EQ expr0                       #assign
    | ID EQ STRING                      #assignString
    | ID EQ REALD CBO INT CBC           #declareRealArray
    | ID EQ INTD CBO INT CBC            #declareIntArray
    | ID SBO INT SBC EQ expr0           #assignIntArrayEl
    | ID SBO INT SBC EQ expr0           #assignRealArrayEl
    | PRINT RBO expr0 RBC               #print
    | PRINT RBO STRING RBC              #printString;

expr0: expr1                            #single0
     | expr1 ADD expr1                  #sum
     | expr1 SUB expr1                  #subtract;

expr1: expr2                            #single1
     | expr2 MULT expr1                 #multiply
     | expr2 DIV expr1                  #divide;

expr2: INT                              #int
     | REAL                             #real
     | TOINT  expr2                     #toInt
     | TOREAL expr2                     #toReal
     | RBO expr0 RBC                    #par
     | ID                               #idRef
     | READ_INT                         #readInt
     | READ_REAL                        #readReal
     | ID SBO INT SBC                   #arrayElRef;


READ_INT: 'readInt()';
READ_REAL: 'readReal()';
PRINT: 'print';
TOINT: '(int)';
TOREAL: '(real)';
INTD: 'int';
REALD: 'real';
ADD: '+';
SUB: '-';
MULT: '*';
DIV: '/';
EQ: '=';
RBO: '(';
RBC: ')';
SBO: '[';
SBC: ']';
CBO: '{';
CBC: '}';
NEWLINE: '\r'? '\n';
WS: (' '|'\t')+ { skip(); };
INT: [0-9]+;
REAL: [0-9]+ '.' [0-9]+;
ID: ('a' ..'z' | 'A' ..'Z')+;
STRING: '"'[a-zA-Z ]+'"';
COMMENT: '#' ~[\r\n]* -> skip;