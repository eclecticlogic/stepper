package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.etc.Etc;
import com.eclecticlogic.stepper.state.Task;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

import static com.eclecticlogic.stepper.etc.Etc.strip;

public class RetryVisitor extends StepperBaseVisitor<Task> {

    private Task task;


    @Override
    public Task visitRetries(StepperParser.RetriesContext ctx) {
        String taskName = ctx.label() == null ? null : strip(ctx.label().STRING().getText());
        task = new Task(taskName);

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
                            .map(Etc::strip) //
                            .forEach(task::setProperty));
                    JsonObjectVisitor visitor = new JsonObjectVisitor(task);
                    visitor.visit(rc.jsonObject());
                });
            }
        });
    }
}
