package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.construct.Construct;
import com.eclecticlogic.stepper.construct.ExpressionConstruct;
import com.eclecticlogic.stepper.construct.StateConstruct;
import com.eclecticlogic.stepper.state.Pass;
import com.eclecticlogic.stepper.state.Task;

import static com.eclecticlogic.stepper.etc.Stringer.from;
import static com.eclecticlogic.stepper.etc.Stringer.strip;


public class AssignmentVisitor extends StepperBaseVisitor<Construct> {

    @Override
    public Construct visitAssignmentTask(StepperParser.AssignmentTaskContext ctx) {
        String taskName = strip(from(ctx.task().taskName));
        Task task = taskName == null ? new Task() : new Task(taskName);
        JsonObjectVisitor visitor = new JsonObjectVisitor(task);
        visitor.visit(ctx.task().jsonObject());
        task.setResultPath("$." + ctx.dereference().getText());
        return new StateConstruct<>(task);
    }


    @Override
    public Construct visitAssignmentJson(StepperParser.AssignmentJsonContext ctx) {
        Pass pass = new Pass();
        pass.captureAttribute("Result");
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
        String expression = enhance(ctx, ctx.expr().getText(), variable);

        construct.setVariable(variable);
        construct.setExpression(expression);

        DereferencingVisitor defVisitor = new DereferencingVisitor();
        construct.setSymbols(defVisitor.visit(ctx.expr()));
        construct.setup();

        return construct;
    }


    String enhance(StepperParser.AssignmentExprContext ctx, String expression, String variable) {
        StepperParser.ComplexAssignContext cactx = ctx.complexAssign();
        if (cactx.ASSIGN() != null) {
            return expression;
        } else {
            String symbol;
            if (cactx.PLUSASSIGN() != null) {
                symbol = "+";
            } else if (cactx.MINUSASSIGN() != null) {
                symbol = "-";
            } else if (cactx.MULTASSIGN() != null) {
                symbol = "*";
            } else {
                symbol = "/";
            }
            return variable + " " + symbol + " " + expression;
        }
    }
}
