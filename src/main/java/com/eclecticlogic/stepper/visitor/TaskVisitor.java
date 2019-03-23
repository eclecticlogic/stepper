package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.construct.StateConstruct;
import com.eclecticlogic.stepper.etc.StringHelper;
import com.eclecticlogic.stepper.state.Task;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

import static com.eclecticlogic.stepper.etc.StringHelper.strip;

public class TaskVisitor extends StepperBaseVisitor<StateConstruct> {

    private Task task;


    public TaskVisitor(Task task) {
        this.task = task;
    }


    @Override
    public StateConstruct visitTask(StepperParser.TaskContext ctx) {
        if (!ctx.retry().isEmpty()) {
            setupRetries(ctx.retry());
        }

        JsonObjectVisitor visitor = new JsonObjectVisitor(task);
        visitor.visit(ctx.jsonObject());
        return new StateConstruct<>(task);
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


    Object getValue(StepperParser.ScalarContext ctx) {
        Object value;
        if (ctx.NUMBER() != null) {
            try {
                value = NumberFormat.getInstance().parse(ctx.getText());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else if (ctx.TRUE() != null) {
            value = true;
        } else if (ctx.FALSE() != null) {
            value = false;
        } else {
            value = strip(ctx.getText());
        }
        return value;
    }
}
