grammar BaseLan;

prog: block          ;

block: ( (stat|function)? NEWLINE )*;

function: realFunction | intFunction;

//realFunction: REALD ID RBO ((INTD|REALD) ID (CM (INTD|REALD) ID)*)? RBC COLON funBody RET expr0 NEWLINE END;
realFunction: REALD ID funDefParams funBody RET expr0 NEWLINE END;

//intFunction: INTD ID RBO ((INTD|REALD) ID (CM (INTD|REALD) ID)*)? RBC COLON funBody RET expr0 NEWLINE END;
intFunction: INTD ID funDefParams funBody RET expr0 NEWLINE END;

funDefParams: RBO (param (CM param)*)? RBC COLON;

param: INTD ID                          #intParam
     | REALD ID                         #realParam;

funBody: (stat? NEWLINE)*;

stat: ID EQ expr0                       #assign
    | ID EQ CBO REAL? (CM REAL)* CBC    #declareRealArray
    | ID EQ CBO INT? (CM INT)* CBC      #declareIntArray
    | ID SBO INT SBC EQ expr0           #assignArrayEl
    | PRINT RBO expr0 RBC               #print
    | REPEAT INT COLON repeatBody END   #repeat
    | IF comp COLON ifBody (ELSIF comp COLON elsifBody)* (ELSE COLON elseBody)? ENDIF #startIf
    | expr0                             #noRetExpr;

comp: expr0 compOper expr0;

compOper: EQ EQ                         #equal
        | NOT EQ                        #notEqual
        | LESSER EQ                     #lesserEqual
        | LESSER                        #lesser
        | GREATER EQ                    #greaterEqual
        | GREATER                       #greater;

repeatBody: block                       ;

ifBody:  block                          ;

elsifBody:  block                       ;

elseBody:  block                        ;

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
     | ID                               #idRef // should add str exception
     | READ_INT                         #readInt
     | READ_REAL                        #readReal
     | ID SBO INT SBC                   #arrayElRef
     | STRING                           #string
     | ID RBO (expr0 (CM expr0)*)? RBC  #funCall;


FUN: 'fun';
ENDFUN: 'endfun';
VOID: 'void';
RET: 'return';
REPEAT: 'repeat';
END: 'end';
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
NOT: '!';
LESSER: '<';
GREATER: '>';
RBO: '(';
RBC: ')';
SBO: '[';
SBC: ']';
CBO: '{';
CBC: '}';
NEWLINE: '\r'? '\n';
CM: ',';
ELSE: 'else';
ELSIF: 'elsif';
IF: 'if';
COLON: ':';
ENDIF: 'endif';
WS: (' '|'\t')+ { skip(); };
INT: [0-9]+;
REAL: [0-9]+ '.' [0-9]+;
ID: ('a' ..'z' | 'A' ..'Z')+;
STRING: '"'[a-zA-Z0-9 \t]+'"';
COMMENT: '#' ~[\r\n]* -> skip;