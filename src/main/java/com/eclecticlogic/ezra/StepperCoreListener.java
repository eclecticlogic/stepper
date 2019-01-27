package com.eclecticlogic.ezra;

import com.eclecticlogic.ezra.antlr.EzraParser;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

public class StepperCoreListener extends IfListener {


    @Override
    public void enterProgram(EzraParser.ProgramContext ctx) {
    }


    @Override
    public void exitProgram(EzraParser.ProgramContext ctx) {
        StringBuilder builder = new StringBuilder();
        builder.append(getStateMachineCode());
        System.out.println(builder.toString());
    }


    String getStateMachineCode() {
        STGroup group = new STGroupFile("ezra/template/stepper.stg");
        ST st = group.getInstanceOf("stepperShell");
        st.add("root", stepConstruct);
        String s = st.render();
        return s;
    }



}
