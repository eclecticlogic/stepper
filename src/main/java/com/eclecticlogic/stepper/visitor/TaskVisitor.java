package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.construct.StateConstruct;
import com.eclecticlogic.stepper.etc.StringHelper;
import com.eclecticlogic.stepper.state.Retry;
import com.eclecticlogic.stepper.state.Task;
import com.google.common.collect.Lists;

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
        List<Retry> retries = Lists.newArrayList();
        for (StepperParser.RetryContext retryContext : ctx.retry()) {
            Retry retry = new Retry();
            for (StepperParser.RetryPairContext retryPair : retryContext.retryPair()) {
                retry.put(retryPair.ID().getText(), getValue(retryPair.scalar()));
            }
            retries.add(retry);
        }
        task.setRetries(retries);

        JsonObjectVisitor visitor = new JsonObjectVisitor(task);
        visitor.visit(ctx.jsonObject());
        return new StateConstruct<>(task);
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
