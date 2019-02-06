
grammar Stepper;

program
    : annotation* 'state' programName=ID '{' statement+ '}'
    ;

// control statements

annotation
    : '@' ID '(' scalar ')'
    ;

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
    | dereference '=' jsonObject        #assignmentJson
    | dereference '=' '[' value (',' value)* ']'    #assignmentJsonArray
    ;

expr
    : scalar
    | NULL
    | expressionStatement
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

expressionStatement
    : dereference indexingDereference? ('.' expressionStatement)?
    | methodCall indexingDereference? ('.' expressionStatement)?
    ;

methodCall
    : ID '(' ')'
    | ID '(' expr (',' expr)* ')'
    ;

indexingDereference
    : '[' expr ']'
    ;

dereference
    : ID ('.' ID )*
    ;

task
    : 'task' jsonObject
    ;

jsonObject
    : '{' pair (',' pair)* '}'
    ;

pair
    : STRING ':' value
;

value
   : STRING                             #valueString
   | NUMBER                             #valueNum
   | '{' pair (',' pair)* '}'           #valueObj
   | '{' '}'                            #valueObjEmpty
   | '[' value (',' value)* ']'         #valueArr
   | '[' ']'                            #valueArrEmpty
   | TRUE                               #valueTrue
   | FALSE                              #valueFalse
;

scalar
    : STRING
    | NUMBER
    | TRUE
    | FALSE
    ;

// lexer rules

NUMBER
   : '-'? INT ('.' [0-9] +)? EXP?
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
TRUE: 'true';
FALSE: 'false';
NULL: 'null';
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