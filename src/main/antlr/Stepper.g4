
grammar Stepper;

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
    : IF '(' ifCondition=expr ')' ifBlock=statementBlock
        (ELSE elseBlock=statementBlock)?
    ;

whileStatement
    : 'while' '(' expr ')' statementBlock
    ;

statementBlock
    : '{' statement+ '}'
    | statement
    ;

statement
    : assignment                        #statementAssignment
    | task                              #statementTask
    | eachStatement                     #statementEach
    | forStatement                      #statementFor
    | ifStatement                       #statementIf
    | whileStatement                    #statementWhile
    ;

assignment
    : dereference '=' task              #assignmentTask
    | dereference '=' expr ';'          #assignmentExpr
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

pair
    : ATTR ':' value
;

value
   : ATTR                               #valueAttr
   | NUMBER                             #valueNum
   | '{' pair (',' pair)* '}'           #valueObj
   | '{' '}'                            #valueObjEmpty
   | '[' value (',' value)* ']'         #valueArr
   | '[' ']'                            #valueArrEmpty
   | 'true'                             #valueTrue
   | 'false'                            #valueFalse
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

// operators
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

// keywords
IF: 'if';
ELSE: 'else';

ID
    : ALPHA (ALPHA | DIGIT)*
    ;

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