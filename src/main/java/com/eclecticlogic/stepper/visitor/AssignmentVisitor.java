package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.state.State;
import com.eclecticlogic.stepper.state.Task;

public class AssignmentVisitor extends StepperBaseVisitor<State> {

    @Override
    public State visitAssignmentTask(StepperParser.AssignmentTaskContext ctx) {
        TaskVisitor taskVisitor = new TaskVisitor();
        Task task = taskVisitor.visit(ctx.task());
        task.setResultPath("$." + ctx.dereference().getText());
        return task;
    }


}
