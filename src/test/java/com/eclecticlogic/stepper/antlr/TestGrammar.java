package com.eclecticlogic.stepper.antlr;

import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.Test;

import java.util.function.Function;

/**
 * Tests parsing rules. Simply tests that parsing doesn't fail with exceptions.
 */
public class TestGrammar extends AbstractGrammarTester {


    void testScalarTypeUse(Function<StepperParser, ParserRuleContext> parserFunction) {
        parse("\"hello\"", parserFunction);
        parse("\"he\"llo\"", parserFunction);
        parse("\"he\"\"llo\"", parserFunction);
        parse("123", parserFunction);
        parse("123.23", parserFunction);
        parse("123.23e+10", parserFunction);
        parse("123.23e-10", parserFunction);
        parse("-123.23e-10", parserFunction);
        parse("true", parserFunction);
        parse("false", parserFunction);
    }


    @Test
    public void testScalar() {
        testScalarTypeUse(StepperParser::scalar);
    }


    @Test
    public void testValue() {
        Function<StepperParser, ParserRuleContext> fn = StepperParser::value;
        testScalarTypeUse(fn);
        parse("{}", fn);
        parse("[]", fn);
        parse("[1, 2, true, false]", fn);
        parse("[\"a\", \"b\"]", fn);
        parse("{\"attrib\": 123}", fn);
        parse("[{\"attrib\": 123}]", fn);
        parse("[{\"attrib\": 123, \"c\": [\"a\", true]}]", fn);
        parse("grammar.stg", "testValueNested", fn);
    }


    @Test
    public void testJsonObject() {
        parse("grammar.stg", "testValueNested", StepperParser::jsonObject);
    }


    @Test
    public void testTask() {
        parse("grammar.stg", "testTask", StepperParser::task);
        parse("grammar.stg", "testStatementTask1", StepperParser::statement);
        parse("grammar.stg", "testStatementTask2", StepperParser::statement);
        parse("grammar.stg", "testStatementTask3", StepperParser::statement);
        parse("grammar.stg", "testStatementTask4", StepperParser::statement);
    }


    @Test
    public void testDereference() {
        Function<StepperParser, ParserRuleContext> fn = StepperParser::dereference;
        parse("abc.def", fn);
        parse("abc", fn);
        parse("abc.d.e.f", fn);
    }


    @Test
    public void testExpr() {
        Function<StepperParser, ParserRuleContext> fn = StepperParser::expr;
        testScalarTypeUse(fn);
        parse("null", fn);
        parse("12 * 3", fn);
        parse("12 / 3", fn);
        parse("12 + 3", fn);
        parse("12 - 3", fn);
        parse("a * b", fn);
        parse("a / b", fn);
        parse("a + b", fn);
        parse("a - b", fn);
        parse("(a - b)", fn);
        parse("c + (a - b)", fn);
        parse("c && (a || !b)", fn);
        parse("c == (a + b) * c", fn);
        parse("c < (a + b) * c", fn);
        parse("c <= (a + b) * c", fn);
        parse("c > (a + b) * c", fn);
        parse("c >= (a + b) * c", fn);
        parse("c.d.e", fn);
        parse("c.d.e[3].f", fn);
        parse("c.d.e[3].f().g", fn);
        parse("c().d().e[0][1]", fn);
        parse("c(a+b).d(r && f).e[0][w-q(4)]", fn);
        parse("c(a+b, r(z).d && f)", fn);
    }


    @Test
    public void testAssignment() {
        Function<StepperParser, ParserRuleContext> fn = StepperParser::assignment;
        parse("grammar.stg", "testAssignmentTask1", fn);
        parse("grammar.stg", "testAssignmentTask2", fn);
        parse("@Label(\"xyz\") a.b.c = d - g();", fn);
        parse("a.b.c -= d - g();", fn);
        parse("@Label(\"xyz\") a.b.c *= d - g();", fn);
        parse("a.b.c /= d - g();", fn);
        parse("grammar.stg", "testAssignmentJson", fn);
        parse("@Label(\"xyz\") ab.cd = [1, 2, 3]", fn);
    }


    @Test
    public void testFor() {
        Function<StepperParser, ParserRuleContext> fn = StepperParser::forStatement;
        parse("grammar.stg", "testForLoop1", fn);
        parse("grammar.stg", "testForLoop2", fn);
        parse("grammar.stg", "testForLoop3", fn);
        parse("grammar.stg", "testForIteration", fn);
    }


    @Test
    public void testIf() {
        Function<StepperParser, ParserRuleContext> fn = StepperParser::ifStatement;
        parse("grammar.stg", "testIf1", fn);
        parse("grammar.stg", "testIf2", fn);
        parse("grammar.stg", "testIf3", fn);
    }


    @Test
    public void testWhile() {
        Function<StepperParser, ParserRuleContext> fn = StepperParser::whileStatement;
        parse("grammar.stg", "testWhile", fn);
    }


    @Test
    public void testWhen() {
        Function<StepperParser, ParserRuleContext> fn = StepperParser::whenStatement;
        parse("grammar.stg", "testWhen1", fn);
        parse("grammar.stg", "testWhen2", fn);
        parse("grammar.stg", "testWhen3", fn);
    }


    @Test
    public void testWait() {
        Function<StepperParser, ParserRuleContext> fn = StepperParser::waitStatement;
        parse("grammar.stg", "testWait1", fn);
        parse("grammar.stg", "testWait2", fn);
    }


    @Test
    public void testFail() {
        Function<StepperParser, ParserRuleContext> fn = StepperParser::failStatement;
        parse("grammar.stg", "testFail0", fn);
        parse("grammar.stg", "testFail1", fn);
        parse("grammar.stg", "testFail2", fn);
    }


    @Test
    public void testGoto() {
        Function<StepperParser, ParserRuleContext> fn = StepperParser::gotoStatement;
        parse("goto \"abc\";", fn);
        parse("goto \"abc\"", fn);
    }


    @Test
    public void testParallel() {
        Function<StepperParser, ParserRuleContext> fn = StepperParser::parallelStatement;
        parse("grammar.stg", "parallel1", fn);
        parse("grammar.stg", "parallel2", fn);
        parse("grammar.stg", "parallel3", fn);

    }
}
