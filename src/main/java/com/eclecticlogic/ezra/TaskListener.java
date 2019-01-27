package com.eclecticlogic.ezra;

import com.eclecticlogic.ezra.antlr.EzraParser;
import com.eclecticlogic.ezra.machine.construct.StateConstruct;
import com.eclecticlogic.ezra.machine.step.StepStateTask;

import java.text.NumberFormat;
import java.text.ParseException;

public class TaskListener extends BaseListener {

    private StepStateTask current;

    @Override
    public void enterTask(EzraParser.TaskContext ctx) {
        current = new StepStateTask();
        stepConstruct.getConstructs().add(new StateConstruct(current));
    }


    @Override
    public void exitAssignmentTask(EzraParser.AssignmentTaskContext ctx) {
        String dereference = ctx.dereference().getText();
        current.setResultPath("$." + dereference);
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
