package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.construct.Construct;
import com.eclecticlogic.stepper.construct.ForIterationConstruct;
import com.eclecticlogic.stepper.construct.ForLoopConstruct;

public class ForVisitor extends StepperBaseVisitor<Construct> {

    @Override
    public Construct visitForIteration(StepperParser.ForIterationContext ctx) {
        ForIterationConstruct construct = new ForIterationConstruct();
        construct.setIterableVariable(ctx.ID().getText());
        construct.setIterableExpression(ctx.iterable.getText());

        DereferencingVisitor defVisitor = new DereferencingVisitor();
        construct.setSymbols(defVisitor.visit(ctx.iterable));

        StatementBlockVisitor visitor = new StatementBlockVisitor();
        construct.setBlock(visitor.visit(ctx.statementBlock()));

        return construct;
    }


    @Override
    public Construct visitForLoop(StepperParser.ForLoopContext ctx) {
        ForLoopConstruct construct = new ForLoopConstruct();
        construct.setIterableVariable(ctx.ID().getText());

        construct.setInitialExpression(ctx.init.getText());
        {
            DereferencingVisitor defVisitor = new DereferencingVisitor();
            construct.setInitialExpressionSymbols(defVisitor.visit(ctx.init));
        }

        construct.setEndingExpression(ctx.end.getText());
        {
            DereferencingVisitor defVisitor = new DereferencingVisitor();
            construct.setEndingExpressionSymbols(defVisitor.visit(ctx.end));
        }

        if (ctx.delta != null) {
            construct.setStepExpression(ctx.delta.getText());
            DereferencingVisitor defVisitor = new DereferencingVisitor();
            construct.setStepExpressionSymbols(defVisitor.visit(ctx.delta));
        }

        StatementBlockVisitor visitor = new StatementBlockVisitor();
        construct.setBlock(visitor.visit(ctx.statementBlock()));

        return construct;
    }
}
