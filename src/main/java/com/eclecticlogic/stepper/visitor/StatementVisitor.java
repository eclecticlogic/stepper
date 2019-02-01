package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.construct.StateConstruct;
import com.eclecticlogic.stepper.construct.Construct;

public class StatementVisitor extends StepperBaseVisitor<Construct> {


    @Override
    public Construct visitStatementTask(StepperParser.StatementTaskContext ctx) {
        TaskVisitor visitor = new TaskVisitor();
        return new StateConstruct(visitor.visit(ctx.task()));
    }


    @Override
    public Construct visitStatementAssignment(StepperParser.StatementAssignmentContext ctx) {
        AssignmentVisitor visitor = new AssignmentVisitor();
        return new StateConstruct(visitor.visit(ctx.assignment()));
    }


    @Override
    public Construct visitStatementIf(StepperParser.StatementIfContext ctx) {
        IfVisitor visitor = new IfVisitor();
        return visitor.visit(ctx.ifStatement());
    }
}
