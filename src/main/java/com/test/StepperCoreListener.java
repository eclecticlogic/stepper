package com.test;

import com.eclecticlogic.ezra.antlr.EzraBaseListener;
import com.eclecticlogic.ezra.antlr.EzraParser;
import com.eclecticlogic.ezra.machine.AbstractStepState;
import com.eclecticlogic.ezra.machine.StepMachine;
import com.eclecticlogic.ezra.machine.step.StepStateTask;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Stack;

public class StepperCoreListener extends EzraBaseListener {

    private int stateCounter;
    StepMachine stateMachine = new StepMachine();
    private Stack<List<AbstractStepState>> awaiting = new Stack<>();

    private StepStateTask current;


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
        st.add("stateMachine", stateMachine);
        String s = st.render();
        return s;
    }


    @Override
    public void exitAssignmentTask(EzraParser.AssignmentTaskContext ctx) {
        String dereference = ctx.dereference().getText();
        current.setResultPath("$." + dereference);
    }


    @Override
    public void enterTask(EzraParser.TaskContext ctx) {
        current = new StepStateTask();
        stateMachine.getStates().add(current);
    }


    @Override
    public void enterPair(EzraParser.PairContext ctx) {
        current.captureAttribute(ctx.ATTR().getText().replaceAll("\"", ""));
    }


    @Override
    public void enterValueAttr(EzraParser.ValueAttrContext ctx) {
        current.setProperty(ctx.ATTR().getText().replaceAll("\"", ""));
    }

    @Override
    public void enterValueNum(EzraParser.ValueNumContext ctx) {
        try {
            current.setProperty(NumberFormat.getInstance().parse(ctx.NUMBER().getText()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void enterValueTrue(EzraParser.ValueTrueContext ctx) {
        current.setProperty(true);
    }

    @Override
    public void enterValueFalse(EzraParser.ValueFalseContext ctx) {
        current.setProperty(false);
    }

    @Override
    public void enterValueArr(EzraParser.ValueArrContext ctx) {
        current.startArray();
    }

    @Override
    public void exitValueArr(EzraParser.ValueArrContext ctx) {
        current.endArray();
    }

    @Override
    public void enterValueObj(EzraParser.ValueObjContext ctx) {
        current.startObject();
    }

    @Override
    public void exitValueObj(EzraParser.ValueObjContext ctx) {
        current.stopObject();
    }
}
