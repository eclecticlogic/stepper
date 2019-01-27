
grammar Ezra;

program
    : statement+
    ;

// control statements

eachStatement
    : 'each' '(' ID 'in' ID ('.' ID)* ('[' expr ']')? ')' statementBlock
    ;

forStatement
    : 'for' '(' ID '=' expr 'to' expr ')' statementBlock
    ;

ifStatement
    : 'if' '(' ifCondition ')' statementBlock
        (keywordElse statementBlock | keywordElse ifStatement)?
    ;

ifCondition
    : expr
    ;

keywordElse
    : 'else'
    ;

whileStatement
    : 'while' '(' expr ')' statementBlock
    ;

// basic structures

statementBlock
    : '{' statement+ '}'
    ;

statement
    : assignment
    | task
    | eachStatement
    | forStatement
    | ifStatement
    | whileStatement
    ;

assignment
    : dereference '=' task              #assignmentTask
    | dereference '=' expr ';'          #assignmentExpr
    | indexingDereference '=' expr ';'  #assignmentIndexingExpr
    ;

expr
    : STRING
    | NUMBER
    | 'true'
    | 'false'
    | 'null'
    | dereference
    | indexingDereference
    | methodCall ('.' (dereference | indexingDereference | methodCall))*
    | expr MUL expr
    | expr DIV expr
    | expr ADD expr
    | expr SUB expr
    | '(' expr ')'
    | expr AND expr
    | expr OR expr
    | NOT expr
    | expr EQUALS expr
    | expr LE expr
    | expr LT expr
    | expr GE expr
    | expr GT expr
    ;

methodCall
    : dereference '(' ')'
    | dereference '(' expr (',' expr)* ')'
    ;

indexingDereference
    : dereference '[' expr ']'
    ;

dereference
    : ID ('.' ID )*
    ;

// task
task
    : 'task' '{' pair (',' pair)* '}'
;

obj
   : '{' pair (',' pair)* '}'
   | '{' '}'
;

pair
    : ATTR ':' value
;

array
   : '[' value (',' value)* ']'
   | '[' ']'
;

value
   : ATTR           #valueAttr
   | NUMBER         #valueNum
   | obj            #valueObj
   | array          #valueArr
   | 'true'         #valueTrue
   | 'false'        #valueFalse
;


// lexer rules

NUMBER
   : '-'? INT ('.' [0-9] +)? EXP?
   ;

ATTR
    : STRING
    ;

STRING
   : '"' (ESC | SAFECODEPOINT)* '"'
   ;

ID
    : ALPHA (ALPHA | DIGIT)*
    ;

MUL: '*';
DIV: '/';
ADD: '+';
SUB: '-';
AND: '&&';
OR: '||';
NOT: '!';
EQUALS: '==';
LE: '<=';
LT: '<';
GE: '>=';
GT: '>';

fragment ESC
   : '\\' (["\\/bfnrt])
   ;

fragment SAFECODEPOINT
   : ~ ["\\\u0000-\u001F]
   ;

fragment INT
   : '0' | [1-9] DIGIT*
   ;

// no leading zeros

fragment EXP
   : [Ee] [+\-]? INT
   ;

fragment DIGIT
    : [0-9]
    ;

fragment ALPHA
    : [a-zA-Z_]
    ;

COMMENT
    : '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    : '//' ~[\r\n]* -> skip
    ;

WS
   : [ \t\n\r]+ -> skip
   ;