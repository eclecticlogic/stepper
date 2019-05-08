package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.construct.Construct;
import com.eclecticlogic.stepper.state.observer.StateObserver;

public class StatementBlockVisitor extends AbstractVisitor<Construct> {


    public StatementBlockVisitor(StateObserver observer) {
        super(observer);
    }


    @Override
    public Construct visitStatementBlock(StepperParser.StatementBlockContext ctx) {
        Construct first = null;
        Construct current = null;
        for (StepperParser.StatementContext stCtx : ctx.statement()) {
            StatementVisitor visitor = new StatementVisitor(getStateObserver());
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
