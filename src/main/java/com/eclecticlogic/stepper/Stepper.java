package com.eclecticlogic.stepper;

import com.eclecticlogic.stepper.antlr.StepperLexer;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.visitor.StepperVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class Stepper {

    public static void main(String[] args) throws Exception {
        CharStream input = CharStreams.fromFileName(args[0]);
        StepperLexer lexer = new StepperLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        StepperParser parser = new StepperParser(tokens);

        StepperVisitor visitor = new StepperVisitor();
        StateMachine machine = visitor.visitProgram(parser.program());

        System.out.println(machine.getAsl());
        System.out.println(machine.getLambda());
    }
}
