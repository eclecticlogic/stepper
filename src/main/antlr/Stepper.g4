
grammar Stepper;

program
    : annotation* 'state' programName=ID '{' statement+ '}'
    ;

// control statements

annotation
    : '@' ID '(' scalar ')'
    ;

forStatement
    : label? FOR '(' ID '=' init=expr TO end=expr (STEP delta=expr)? ')' statementBlock #forLoop
    | label? FOR '(' ID 'in' iterable=expr ')' statementBlock     #forIteration
    ;

ifStatement
    : IF '(' ifCondition=expr ')' ifBlock=statementBlock
        (ELSE elseBlock=statementBlock)?
    ;

whileStatement
    : WHILE '(' expr ')' statementBlock
    ;

whenStatement
    : WHEN '{'
        (CASE caseExpr+=expr ':' caseBlock+=statementBlock)+
        (ELSE elseBlock=statementBlock)?
    '}'
    ;

statementBlock
    : '{' statement+ '}'
    | statement
    ;

statement
    : assignment                        #statementAssignment
    | retries task                       #statementTask
    | forStatement                      #statementFor
    | ifStatement                       #statementIf
    | whileStatement                    #statementWhile
    | whenStatement                     #statementWhen
    ;

assignment
    : retries dereference ASSIGN task SEMICOLON?                           #assignmentTask
    | label? dereference ASSIGN NUMBER ';'                                 #assignmentNumber
    | label? dereference ASSIGN TRUE ';'                                   #assignmentTrue
    | label? dereference ASSIGN FALSE ';'                                  #assignmentFalse
    | label? dereference ASSIGN STRING ';'                                 #assignmentString
    | label? dereference complexAssign expr ';'                            #assignmentExpr
    | label? dereference ASSIGN jsonObject SEMICOLON?                      #assignmentJson
    | label? dereference ASSIGN '[' value (',' value)* ']' SEMICOLON?      #assignmentJsonArray
    ;

expr
    : scalar
    | NULL
    | expressionStatement
    | expr INCR
    | expr DECR
    | INCR expr
    | DECR expr
    | expr MUL expr
    | expr DIV expr
    | expr ADD expr
    | expr SUB expr
    | '(' expr ')'
    | expr AND expr
    | expr OR expr
    | NOT expr
    | expr EQUALITY expr
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

retries
    : retry* label? retry*;

retry
    : '@RetryOnError' '(' STRING (',' STRING)* ')' jsonObject
    ;

label
    : '@Label' '(' STRING ')'
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
   | TRUE                               #valueTrue
   | FALSE                              #valueFalse
   | '{' pair (',' pair)* '}'           #valueObj
   | '{' '}'                            #valueObjEmpty
   | '[' value (',' value)* ']'         #valueArr
   | '[' ']'                            #valueArrEmpty
;

scalar
    : STRING
    | NUMBER
    | TRUE
    | FALSE
    ;

complexAssign
    : (ASSIGN | PLUSASSIGN | MINUSASSIGN | MULTASSIGN | DIVASSIGN)
    ;

// lexer rules

NUMBER
   : '-'? INT ('.' [0-9] +)? EXP?
   ;

STRING
   : '"' (ESC | SAFECODEPOINT)* '"'
   ;

// operators
INCR: '++';
DECR: '--';
MUL: '*';
DIV: '/';
ADD: '+';
SUB: '-';
AND: '&&';
OR: '||';
NOT: '!';
EQUALITY: '==';
LE: '<=';
LT: '<';
GE: '>=';
GT: '>';
ASSIGN: '=';
PLUSASSIGN: '+=';
MINUSASSIGN: '-=';
MULTASSIGN: '*=';
DIVASSIGN: '/=';

// keywords
TRUE: 'true';
FALSE: 'false';
NULL: 'null';
IF: 'if';
ELSE: 'else';
FOR: 'for';
TO: 'to';
STEP: 'step';
WHILE: 'while';
WHEN: 'when';
CASE: 'case';

// symbols
SEMICOLON: ';';

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