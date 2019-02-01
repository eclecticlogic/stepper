package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.construct.Construct;

public class StatementBlockVisitor extends StepperBaseVisitor<Construct> {

    @Override
    public Construct visitStatementBlock(StepperParser.StatementBlockContext ctx) {
        Construct first = null;
        Construct current = null;
        for (StepperParser.StatementContext stCtx : ctx.statement()) {
            StatementVisitor visitor = new StatementVisitor();
            Construct c = visitor.visit(stCtx);
            if (first == null) {
                first = c;
                current = c;
            } else {
                current.setNext(c);
                current = c;
            }
        }
        return first;
    }
}
