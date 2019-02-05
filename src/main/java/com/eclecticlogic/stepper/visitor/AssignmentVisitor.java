package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.state.Pass;
import com.eclecticlogic.stepper.state.State;
import com.eclecticlogic.stepper.state.Task;

public class AssignmentVisitor extends StepperBaseVisitor<State> {

    @Override
    public State visitAssignmentTask(StepperParser.AssignmentTaskContext ctx) {
        Task task = new Task();
        JsonObjectVisitor visitor = new JsonObjectVisitor(task);
        visitor.visit(ctx.task().jsonObject());
        task.setResultPath("$." + ctx.dereference().getText());
        return task;
    }


    @Override
    public State visitAssignmentJson(StepperParser.AssignmentJsonContext ctx) {
        Pass pass = new Pass();
        pass.captureAttribute("Result");
        pass.handleObject(() -> {
            JsonObjectVisitor visitor = new JsonObjectVisitor(pass);
            visitor.visit(ctx.jsonObject());
        });
        pass.setResultPath("$." + ctx.dereference().getText());
        return pass;
    }
}
