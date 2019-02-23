package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.construct.ForIterationConstruct;

public class ForVisitor extends StepperBaseVisitor<ForIterationConstruct> {

    @Override
    public ForIterationConstruct visitForIteration(StepperParser.ForIterationContext ctx) {
        ForIterationConstruct construct = new ForIterationConstruct();
        construct.setIterableVariable(ctx.ID().getText());
        construct.setIterableExpression(ctx.iterable.getText());

        DereferencingVisitor defVisitor = new DereferencingVisitor();
        construct.setSymbols(defVisitor.visit(ctx.iterable));

        StatementBlockVisitor ifBlockVisitor = new StatementBlockVisitor();
        construct.setBlock(ifBlockVisitor.visit(ctx.statementBlock()));

        return construct;
    }
}
