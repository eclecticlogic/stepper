
grammar Stepper;

program
    : annotation* PROGRAM programName=ID '{' statement+ '}'
    ;

// control statements

annotation
    : '@' ID '(' scalar ')'
    ;

forStatement
    : label? FOR '(' ID '=' init=expr TO end=expr (STEP delta=expr)? ')' statementBlock #forLoop
    | label? FOR '(' ID IN iterable=expr ')' statementBlock     #forIteration
    ;

ifStatement
    : label? IF '(' ifCondition=expr ')' ifBlock=statementBlock
        (ELSE elseBlock=statementBlock)?
    ;

whileStatement
    : label? WHILE '(' expr ')' statementBlock
    ;

whenStatement
    : WHEN '{'
        caseEntry+
        (ELSE elseBlock=statementBlock)?
    '}'
    ;

caseEntry
    : label? CASE expr ':' statementBlock
    ;

waitStatement
    : label? WAIT jsonObject
    ;

failStatement
    : label? FAIL jsonObject? ';'?
    ;

gotoStatement
    : GOTO STRING ';'?
    ;

parallelStatement
    : retries (dereference ASSIGN)? PARALLEL '(' STRING (',' STRING)* ')'
    ;

tryCatchStatement
    : TRY statementBlock catchClause+
    ;

catchClause
    : CATCH '(' STRING (',' STRING)* ')' '{' (dereference CLOSURE)* statement+ '}'
    ;

statementBlock
    : '{' statement+ '}'
    | statement
    ;

statement
    : assignment                        #statementAssignment
    | retries task                      #statementTask
    | forStatement                      #statementFor
    | ifStatement                       #statementIf
    | whileStatement                    #statementWhile
    | whenStatement                     #statementWhen
    | waitStatement                     #statementWait
    | failStatement                     #statementFail
    | gotoStatement                     #statementGoto
    | parallelStatement                 #statementParallel
    | tryCatchStatement                 #statementTryCatch
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
    | expr MUL expr
    | expr DIV expr
    | expr ADD expr
    | expr SUB expr
    | expr MOD expr
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
    : TASK jsonObject
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
MUL: '*';
DIV: '/';
ADD: '+';
SUB: '-';
MOD: '%';
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
PROGRAM: 'stepper';
TASK: 'task';
PARALLEL: 'parallel';
WAIT: 'wait';
FAIL: 'fail';
GOTO: 'goto';

TRUE: 'true';
FALSE: 'false';
NULL: 'null';
IF: 'if';
ELSE: 'else';
FOR: 'for';
IN: 'in';
TO: 'to';
STEP: 'step';
WHILE: 'while';
WHEN: 'when';
CASE: 'case';
TRY: 'try';
CATCH: 'catch';

// symbols
SEMICOLON: ';';
CLOSURE: '->';

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