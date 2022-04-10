grammar BaseLan;

prog: ( instruction? NEWLINE )*;

instruction: assign | print;

print: PRINT RBO ID RBC;

assign: ID EQ expr0;

expr0: expr1 | sum | subtract;

expr1: expr2 | multiply | divide;

multiply: expr2 MULT expr2;

divide: expr2 DIV expr2;

expr2: INT 
     | REAL 
     | TOINT expr2 
     | TOREAL expr2 
     | innerComponent 
     | read
     | ID;

innerComponent: RBO expr0 RBC;

sum: expr1 ADD expr1;

subtract: expr1 SUB expr1;

read: READ_INT | READ_REAL;




ADD: '+';
SUB: '-';
MULT: '*';
DIV: '/';
EQ: '=';
RBO: '(';
RBC: ')'; 
READ_INT: 'readInt()';
READ_REAL: 'readReal()';
NEWLINE: '\r'? '\n';
TOINT: '(int)';
TOREAL: '(real)';
PRINT: 'print';
WS: (' '|'\t')+ { skip(); };
INT: [0-9]+;
REAL: [0-9]+ '.' [0-9]+;

ID: ('a' ..'z' | 'A' ..'Z')+;

COMMENT: '#' ~[\r\n]* -> skip;