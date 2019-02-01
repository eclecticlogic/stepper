package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.state.Task;

import java.text.NumberFormat;
import java.text.ParseException;

public class TaskVisitor extends StepperBaseVisitor<Task> {

    private Task task;

    @Override
    public Task visitTask(StepperParser.TaskContext ctx) {
        task = new Task();
        ctx.pair().forEach(this::visit);
        return task;
    }


    @Override
    public Task visitPair(StepperParser.PairContext ctx) {
        task.captureAttribute(ctx.ATTR().getText().replaceAll("\"", ""));
        visit(ctx.value());
        return null;
    }

    @Override
    public Task visitValueAttr(StepperParser.ValueAttrContext ctx) {
        task.setProperty(ctx.ATTR().getText().replaceAll("\"", ""));
        return null;
    }


    @Override
    public Task visitValueNum(StepperParser.ValueNumContext ctx) {
        try {
            task.setProperty(NumberFormat.getInstance().parse(ctx.NUMBER().getText()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    @Override
    public Task visitValueTrue(StepperParser.ValueTrueContext ctx) {
        task.setProperty(true);
        return null;
    }

    @Override
    public Task visitValueFalse(StepperParser.ValueFalseContext ctx) {
        task.setProperty(false);
        return null;
    }

    @Override
    public Task visitValueObj(StepperParser.ValueObjContext ctx) {
        task.handleObject(() -> ctx.pair().forEach(this::visit));
        return null;
    }


    @Override
    public Task visitValueObjEmpty(StepperParser.ValueObjEmptyContext ctx) {
        task.handleObject(() -> {});
        return null;
    }


    @Override
    public Task visitValueArr(StepperParser.ValueArrContext ctx) {
        task.handleArray(() -> ctx.value().forEach(this::visit));
        return null;
    }


    @Override
    public Task visitValueArrEmpty(StepperParser.ValueArrEmptyContext ctx) {
        task.handleArray(() -> {
        });
        return null;
    }
}
