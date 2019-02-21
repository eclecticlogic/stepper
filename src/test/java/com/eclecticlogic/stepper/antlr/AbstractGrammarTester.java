package com.eclecticlogic.stepper.antlr;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.util.function.Function;

public abstract class AbstractGrammarTester {

    public static class TestStepperListener extends StepperBaseListener {

    }


    protected void parse(String text, Function<StepperParser, ParserRuleContext> parserFunction) {
        CharStream input = CharStreams.fromString(text);
        StepperLexer lexer = new StepperLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        StepperParser parser = new StepperParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                throw new RuntimeException("Line: " + line + ", char: " + charPositionInLine + ", " + msg);
            }
        });

        TestStepperListener listener = new TestStepperListener();
        ParseTreeWalker.DEFAULT.walk(listener, parserFunction.apply(parser));
    }


    protected void parse(String groupFile, String name, Function<StepperParser, ParserRuleContext> parserFunction) {
        STGroup group = new STGroupFile("antlr/grammar/test/" + groupFile);
        ST st = group.getInstanceOf(name);
        parse(st.render(), parserFunction);
    }
}
