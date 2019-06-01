package com.eclecticlogic.stepper.antlr;

import com.eclecticlogic.stepper.Stepper;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.util.function.Function;

public abstract class AbstractGrammarTester {

    public static class TestStepperListener extends StepperBaseListener {

    }


    protected void parse(String groupFile, String name, Function<StepperParser, ParserRuleContext> parserFunction) {
        STGroup group = new STGroupFile("antlr/grammar/" + groupFile);
        ST st = group.getInstanceOf(name);
        parse(st.render(), parserFunction);
    }


    protected void parse(String text, Function<StepperParser, ParserRuleContext> parserFunction) {
        StepperParser parser = Stepper.createParser(CharStreams.fromString(text));
        TestStepperListener listener = new TestStepperListener();
        ParseTreeWalker.DEFAULT.walk(listener, parserFunction.apply(parser));
    }


}
