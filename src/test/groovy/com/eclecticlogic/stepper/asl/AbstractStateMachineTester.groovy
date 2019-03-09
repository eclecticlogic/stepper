package com.eclecticlogic.stepper.asl

import com.eclecticlogic.stepper.StateMachine
import com.eclecticlogic.stepper.antlr.StepperLexer
import com.eclecticlogic.stepper.antlr.StepperParser
import com.eclecticlogic.stepper.visitor.StepperVisitor
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.ReadContext
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.stringtemplate.v4.ST
import org.stringtemplate.v4.STGroup
import org.stringtemplate.v4.STGroupFile
import spock.lang.Specification

abstract class AbstractStateMachineTester extends Specification {

    class TestListener extends BaseErrorListener {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            throw new RuntimeException("Line: $line, char: $charPositionInLine, $msg")
        }
    }


    ReadContext runProgram(String groupFile, String name) {
        STGroup group = new STGroupFile('stepper/' + groupFile)
        ST st = group.getInstanceOf(name)

        CharStream input = CharStreams.fromString(st.render())
        StepperLexer lexer = new StepperLexer(input)
        CommonTokenStream tokens = new CommonTokenStream(lexer)
        StepperParser parser = new StepperParser(tokens)
        parser.removeErrorListeners()
        parser.addErrorListener(new TestListener())

        StepperVisitor visitor = new StepperVisitor()
        StateMachine machine = visitor.visitProgram(parser.program())
        String json = machine.asl
        println(json)
        return JsonPath.parse(json)
    }

}
