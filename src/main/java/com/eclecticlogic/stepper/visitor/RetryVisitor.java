package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.etc.StringHelper;
import com.eclecticlogic.stepper.state.Task;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

public class RetryVisitor extends StepperBaseVisitor<Task> {

    private final Task task;


    public RetryVisitor(Task task) {
        this.task = task;
    }


    @Override
    public Task visitRetries(StepperParser.RetriesContext ctx) {
        if (!ctx.retry().isEmpty()) {
            setupRetries(ctx.retry());
        }
        return task;
    }


    void setupRetries(List<StepperParser.RetryContext> ctx) {
        task.captureAttribute("Retry");
        task.handleArray(() -> {
            for (StepperParser.RetryContext rc : ctx) {
                task.handleObject(() -> {
                    task.captureAttribute("ErrorEquals");
                    task.handleArray(() -> rc.STRING().stream()//
                            .map(TerminalNode::getText) //
                            .map(StringHelper::strip) //
                            .forEach(task::setProperty));
                    JsonObjectVisitor visitor = new JsonObjectVisitor(task);
                    visitor.visit(rc.jsonObject());
                });
            }
        });
    }
}
