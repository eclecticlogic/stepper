package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.construct.Construct;
import com.eclecticlogic.stepper.construct.StateConstruct;
import com.eclecticlogic.stepper.state.Task;

import static com.eclecticlogic.stepper.etc.Stringer.from;
import static com.eclecticlogic.stepper.etc.Stringer.strip;


public class StatementVisitor extends StepperBaseVisitor<Construct> {


    @Override
    public Construct visitStatementTask(StepperParser.StatementTaskContext ctx) {
        String taskName = strip(from(ctx.task().taskName));
        Task task = taskName == null ? new Task() : new Task(taskName);
        JsonObjectVisitor visitor = new JsonObjectVisitor(task);
        visitor.visit(ctx.task().jsonObject());
        return new StateConstruct<>(task);
    }


    @Override
    public Construct visitStatementAssignment(StepperParser.StatementAssignmentContext ctx) {
        AssignmentVisitor visitor = new AssignmentVisitor();
        return visitor.visit(ctx.assignment());
    }


    @Override
    public Construct visitStatementIf(StepperParser.StatementIfContext ctx) {
        IfVisitor visitor = new IfVisitor();
        return visitor.visit(ctx.ifStatement());
    }
}
