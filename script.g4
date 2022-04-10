grammar script;

// Lexer

WS: [\t\r\n ]+ -> skip;
LINE_COMMENT: '#' ~[\r\n]* -> skip;

ADD: '+';
SUB: '-';
MULT: '*';
DIV: '/';
ASSIGN: '=';
LP: '(';
RP: ')';
ARRAY: '[]';
SEMICOLON: ';';
LB: '{';
RB: '}';
COMMA: ',';
DOT: '.';
LA: '[';
RA: ']';
MOD: '%';

EQUAL: '==';

TYPE: 'int' | 'real';

READ_INT: 'readInt';
READ_REAL: 'readReal';

TOINT: '(int)';
TOREAL: '(real)';

INT: [0-9]+;
REAL: [0-9]+ '.' [0-9]+;
STRING: '"' (.*?) '"';

ID: ('a' ..'z' | 'A' ..'Z')+;

// PARSER

