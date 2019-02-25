package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.state.AttributableState;
import com.eclecticlogic.stepper.state.Task;

import java.text.NumberFormat;
import java.text.ParseException;

import static com.eclecticlogic.stepper.etc.StringHelper.from;
import static com.eclecticlogic.stepper.etc.StringHelper.strip;


public class JsonObjectVisitor extends StepperBaseVisitor<AttributableState> {

    private AttributableState state;


    public JsonObjectVisitor(AttributableState state) {
        this.state = state;
    }


    @Override
    public AttributableState visitJsonObject(StepperParser.JsonObjectContext ctx) {
        ctx.pair().forEach(this::visit);
        return state;
    }


    @Override
    public AttributableState visitPair(StepperParser.PairContext ctx) {
        state.captureAttribute(strip(from(ctx.STRING())));
        visit(ctx.value());
        return null;
    }


    @Override
    public Task visitValueString(StepperParser.ValueStringContext ctx) {
        state.setProperty(strip(from(ctx.STRING())));
        return null;
    }


    @Override
    public Task visitValueNum(StepperParser.ValueNumContext ctx) {
        try {
            state.setProperty(NumberFormat.getInstance().parse(ctx.NUMBER().getText()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    @Override
    public Task visitValueTrue(StepperParser.ValueTrueContext ctx) {
        state.setProperty(true);
        return null;
    }


    @Override
    public Task visitValueFalse(StepperParser.ValueFalseContext ctx) {
        state.setProperty(false);
        return null;
    }


    @Override
    public Task visitValueObj(StepperParser.ValueObjContext ctx) {
        state.handleObject(() -> ctx.pair().forEach(this::visit));
        return null;
    }


    @Override
    public Task visitValueObjEmpty(StepperParser.ValueObjEmptyContext ctx) {
        state.handleObject(() -> {
        });
        return null;
    }


    @Override
    public Task visitValueArr(StepperParser.ValueArrContext ctx) {
        state.handleArray(() -> ctx.value().forEach(this::visit));
        return null;
    }


    @Override
    public Task visitValueArrEmpty(StepperParser.ValueArrEmptyContext ctx) {
        state.handleArray(() -> {
        });
        return null;
    }
}
