package com.eclecticlogic.stepper.asl

import com.eclecticlogic.stepper.StateMachine
import com.eclecticlogic.stepper.antlr.StepperLexer
import com.eclecticlogic.stepper.antlr.StepperParser
import com.eclecticlogic.stepper.visitor.StepperVisitor
import com.google.gson.JsonObject
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

/**
 * General structure tests.
 */
class TestGeneral extends Specification {

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


    def "test annotation and state name"() {
        given:
        ReadContext ctx = runProgram('programs.stg', 'annotationName')

        expect:
        with(ctx) {
            read('$.Comment') == 'this is a comment'
            read('$.TimeoutSeconds') == 3600
            read('$.Version') == '1.0'

            read('$.StartAt') == 'AnnotationTest000'
            read('$..AnnotationTest000.Type')[0] == 'Pass'
            read('$..AnnotationTest000.Result')[0] == 5
            read('$..AnnotationTest000.ResultPath')[0] == '$.c'
            read('$..AnnotationTest000.Next')[0] == 'AnnotationTest001'

            read('$..AnnotationTest001.Type')[0] == 'Succeed'
        }
    }
}
