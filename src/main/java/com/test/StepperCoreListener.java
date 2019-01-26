package com.test;

import com.eclecticlogic.ezra.antlr.EzraBaseListener;
import com.eclecticlogic.ezra.antlr.EzraParser;
import com.test.machine.RootStateMachine;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.util.List;
import java.util.Stack;

public class StepperCoreListener extends EzraBaseListener {

    private int stateCounter;
    RootStateMachine stateMachine = new RootStateMachine();
    private Stack<List<StepState>> awaiting = new Stack<>();

    private StepState current;


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
        current = new StepState();
        current.setType(StateType.TASK);
        stateMachine.getStates().add(current);
    }


    @Override
    public void enterPair(EzraParser.PairContext ctx) {
        current.jsonData.captureAttribute(ctx.ATTR().getText());
    }


    @Override
    public void enterValueAttr(EzraParser.ValueAttrContext ctx) {
        current.jsonData.setAttributeValue(ctx.ATTR().getText());
    }

    @Override
    public void enterValueNum(EzraParser.ValueNumContext ctx) {
        current.jsonData.setAttributeValue(ctx.NUMBER().getText());
    }
    @Override
    public void enterValueTrue(EzraParser.ValueTrueContext ctx) {
        current.jsonData.setAttributeValue(true);
    }

    @Override
    public void enterValueFalse(EzraParser.ValueFalseContext ctx) {
        current.jsonData.setAttributeValue(false);
    }

    @Override
    public void enterValueNull(EzraParser.ValueNullContext ctx) {
        current.jsonData.setAttributeValue(null);
    }

    @Override
    public void enterValueArr(EzraParser.ValueArrContext ctx) {
        current.jsonData.startArray();
    }

    @Override
    public void exitValueArr(EzraParser.ValueArrContext ctx) {
        current.jsonData.endArray();
    }

    @Override
    public void enterValueObj(EzraParser.ValueObjContext ctx) {
        current.jsonData.startObject();
    }

    @Override
    public void exitValueObj(EzraParser.ValueObjContext ctx) {
        current.jsonData.stopObject();
    }
}
