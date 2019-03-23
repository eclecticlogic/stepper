package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.construct.Construct;
import com.eclecticlogic.stepper.construct.ExpressionConstruct;
import com.eclecticlogic.stepper.construct.StateConstruct;
import com.eclecticlogic.stepper.state.Pass;
import com.eclecticlogic.stepper.state.Task;

import java.math.BigDecimal;

import static com.eclecticlogic.stepper.etc.StringHelper.*;


public class AssignmentVisitor extends StepperBaseVisitor<Construct> {

    @Override
    public Construct visitAssignmentTask(StepperParser.AssignmentTaskContext ctx) {
        String taskName = strip(from(ctx.task().taskName));
        Task task = taskName == null ? new Task() : new Task(taskName);
        task.setResultPath("$." + ctx.dereference().getText());

        RetryVisitor retryVisitor = new RetryVisitor(task);
        retryVisitor.visit(ctx.retries());

        JsonObjectVisitor jsonObjectVisitor = new JsonObjectVisitor(task);
        jsonObjectVisitor.visit(ctx.task().jsonObject());

        return new StateConstruct<>(task);
    }


    @Override
    public Construct visitAssignmentNumber(StepperParser.AssignmentNumberContext ctx) {
        Pass pass = new Pass();
        pass.captureAttribute("Result");
        pass.setProperty(new BigDecimal(ctx.NUMBER().getText()));
        pass.setResultPath("$." + ctx.dereference().getText());
        return new StateConstruct<>(pass);
    }


    @Override
    public Construct visitAssignmentString(StepperParser.AssignmentStringContext ctx) {
        Pass pass = new Pass();
        pass.captureAttribute("Result");
        pass.setProperty(strip(ctx.STRING().getText()));
        pass.setResultPath("$." + ctx.dereference().getText());
        return new StateConstruct<>(pass);
    }


    Construct visitAssignmentBoolean(String var, boolean value) {
        Pass pass = new Pass();
        pass.captureAttribute("Result");
        pass.setProperty(value);
        pass.setResultPath("$." + var);
        return new StateConstruct<>(pass);
    }


    @Override
    public Construct visitAssignmentTrue(StepperParser.AssignmentTrueContext ctx) {
        return visitAssignmentBoolean(ctx.dereference().getText(), true);
    }


    @Override
    public Construct visitAssignmentFalse(StepperParser.AssignmentFalseContext ctx) {
        return visitAssignmentBoolean(ctx.dereference().getText(), false);
    }


    @Override
    public Construct visitAssignmentJson(StepperParser.AssignmentJsonContext ctx) {
        Pass pass = new Pass();
        pass.captureAttribute("Parameters");
        pass.handleObject(() -> {
            JsonObjectVisitor visitor = new JsonObjectVisitor(pass);
            visitor.visit(ctx.jsonObject());
        });
        pass.setResultPath("$." + ctx.dereference().getText());
        return new StateConstruct<>(pass);
    }


    @Override
    public Construct visitAssignmentJsonArray(StepperParser.AssignmentJsonArrayContext ctx) {
        Pass pass = new Pass();
        pass.captureAttribute("Result");
        pass.handleArray(() -> {
            JsonObjectVisitor visitor = new JsonObjectVisitor(pass);
            visitor.visit(ctx);
        });
        pass.setResultPath("$." + ctx.dereference().getText());
        return new StateConstruct<>(pass);
    }


    @Override
    public Construct visitAssignmentExpr(StepperParser.AssignmentExprContext ctx) {
        ExpressionConstruct construct = new ExpressionConstruct();
        String variable = ctx.dereference().getText();
        String expression = enhance(ctx.complexAssign(), ctx.expr().getText(), variable);

        construct.setVariable(variable);
        construct.setExpression(expression);

        DereferencingVisitor defVisitor = new DereferencingVisitor();
        construct.setSymbols(defVisitor.visit(ctx.expr()));

        return construct;
    }

}
