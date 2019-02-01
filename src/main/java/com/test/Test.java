package com.test;

import com.eclecticlogic.stepper.antlr.StepperLexer;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.construct.ProgramConstruct;
import com.eclecticlogic.stepper.visitor.StepperVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

public class Test {

    public static void main(String[] args) throws Exception {
        CharStream input = CharStreams.fromFileName("c:/users/kabram/Downloads/test.txt");
        StepperLexer lexer = new StepperLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        StepperParser parser = new StepperParser(tokens);

        StepperVisitor visitor = new StepperVisitor();
        ProgramConstruct program = visitor.visitProgram(parser.program());

        STGroup group = new STGroupFile("ezra/template/stepper.stg");
        ST st = group.getInstanceOf("stepperShell");
        st.add("program", program);
        String s = st.render();
        System.out.println(s);
    }
}
