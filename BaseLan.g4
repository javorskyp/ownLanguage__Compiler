grammar BaseLan;

prog: ( stat? NEWLINE )*;

stat: ID EQ expr0           #assign
    | PRINT RBO ID RBC      #print;

expr0: expr1                #single0
     | expr1 ADD expr1      #sum
     | expr1 SUB expr1      #subtract;

expr1: expr2                #single1
     | expr2 MULT expr2     #multiply
     | expr2 DIV expr2      #divide;

expr2: INT                  #int
     | REAL                 #real
     | TOINT  expr2         #toInt
     | TOREAL expr2         #toReal
     | RBO expr0 RBC        #par
     | ID                   #idRef
     | READ_INT             #readInt
     | READ_REAL            #readReal;


READ_INT: 'readInt()';
READ_REAL: 'readReal()';
PRINT: 'print';
TOINT: '(int)';
TOREAL: '(real)';

ADD: '+';
SUB: '-';
MULT: '*';
DIV: '/';
EQ: '=';
RBO: '(';
RBC: ')'; 
NEWLINE: '\r'? '\n';
WS: (' '|'\t')+ { skip(); };
INT: [0-9]+;
REAL: [0-9]+ '.' [0-9]+;
ID: ('a' ..'z' | 'A' ..'Z')+;

COMMENT: '#' ~[\r\n]* -> skip;