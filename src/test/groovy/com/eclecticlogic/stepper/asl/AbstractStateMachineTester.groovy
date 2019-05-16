/*
Copyright 2015-2019 KR Abram

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.eclecticlogic.stepper.asl

import com.eclecticlogic.stepper.StateMachine
import com.eclecticlogic.stepper.antlr.StepperLexer
import com.eclecticlogic.stepper.antlr.StepperParser
import com.eclecticlogic.stepper.visitor.StepperVisitor
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.ReadContext
import org.antlr.v4.runtime.*
import org.stringtemplate.v4.ST
import org.stringtemplate.v4.STGroup
import org.stringtemplate.v4.STGroupFile
import spock.lang.Specification

abstract class AbstractStateMachineTester extends Specification {

    class TestListener extends BaseErrorListener {
        @Override
        void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            throw new RuntimeException("Line: $line, char: $charPositionInLine, $msg")
        }
    }


    StateMachine _run(String groupFile, String name) {
        STGroup group = new STGroupFile('stepper/' + groupFile)
        ST st = group.getInstanceOf(name)

        CharStream input = CharStreams.fromString(st.render())
        StepperLexer lexer = new StepperLexer(input)
        CommonTokenStream tokens = new CommonTokenStream(lexer)
        StepperParser parser = new StepperParser(tokens)
        parser.removeErrorListeners()
        parser.addErrorListener(new TestListener())

        StepperVisitor visitor = new StepperVisitor()
        return visitor.visitProgram(parser.program())
    }


    ReadContext run(String groupFile, String name) {
        StateMachine machine = _run(groupFile, name)
        String json = machine.asl
        println(json)
        return JsonPath.parse(json)
    }


    class TestOutput {
        ReadContext ctx
        String lambda
    }


    TestOutput runWithLambda(String groupFile, String name) {
        StateMachine machine = _run(groupFile, name)
        String json = machine.asl
        println(json)
        println(machine.lambda)
        return new TestOutput().with {
            ctx = JsonPath.parse(json)
            lambda = machine.lambda
            return it
        }
    }
}
