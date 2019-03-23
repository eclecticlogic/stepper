package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.construct.*;
import com.eclecticlogic.stepper.state.Task;
import com.google.common.collect.Lists;

import java.util.List;

import static com.eclecticlogic.stepper.etc.StringHelper.from;
import static com.eclecticlogic.stepper.etc.StringHelper.strip;


public class StatementVisitor extends StepperBaseVisitor<Construct> {


    @Override
    public Construct visitStatementTask(StepperParser.StatementTaskContext ctx) {
        String taskName = strip(from(ctx.task().taskName));
        Task task = taskName == null ? new Task() : new Task(taskName);
        return new TaskVisitor(task).visit(ctx.task());
    }


    @Override
    public Construct visitStatementAssignment(StepperParser.StatementAssignmentContext ctx) {
        AssignmentVisitor visitor = new AssignmentVisitor();
        return visitor.visit(ctx.assignment());
    }


    @Override
    public Construct visitIfStatement(StepperParser.IfStatementContext ctx) {
        IfConstruct construct = new IfConstruct();
        construct.setCondition(ctx.ifCondition.getText());

        DereferencingVisitor defVisitor = new DereferencingVisitor();
        construct.setSymbols(defVisitor.visit(ctx.ifCondition));

        StatementBlockVisitor ifBlockVisitor = new StatementBlockVisitor();
        construct.setFirstIf(ifBlockVisitor.visit(ctx.ifBlock));

        if (ctx.ELSE() != null) {
            StatementBlockVisitor elseBlockVisitor = new StatementBlockVisitor();
            construct.setFirstElse(elseBlockVisitor.visit(ctx.elseBlock));
        }

        return construct;
    }


    @Override
    public Construct visitStatementFor(StepperParser.StatementForContext ctx) {
        ForVisitor visitor = new ForVisitor();
        return visitor.visit(ctx.forStatement());
    }


    @Override
    public Construct visitWhileStatement(StepperParser.WhileStatementContext ctx) {
        WhileConstruct construct = new WhileConstruct();
        construct.setExpression(ctx.expr().getText());

        DereferencingVisitor defVisitor = new DereferencingVisitor();
        construct.setSymbols(defVisitor.visit(ctx.expr()));

        StatementBlockVisitor visitor = new StatementBlockVisitor();
        construct.setBlock(visitor.visit(ctx.statementBlock()));

        return construct;
    }


    @Override
    public Construct visitWhenStatement(StepperParser.WhenStatementContext ctx) {
        WhenConstruct construct = new WhenConstruct();
        List<WhenCase> cases = Lists.newArrayList();
        construct.setCases(cases);

        for (int i = 0; i < ctx.caseExpr.size(); i++) {
            StepperParser.ExprContext ectx = ctx.caseExpr.get(i);
            DereferencingVisitor defVisitor = new DereferencingVisitor();
            StatementBlockVisitor statementBlockVisitor = new StatementBlockVisitor();
            Construct blockConstruct = statementBlockVisitor.visit(ctx.caseBlock.get(i));
            WhenCase wcase = new WhenCase(defVisitor.visit(ectx), ectx.getText(), blockConstruct);
            cases.add(wcase);
        }

        if (ctx.elseBlock != null) {
            StatementBlockVisitor statementBlockVisitor = new StatementBlockVisitor();
            construct.setElseBlock(statementBlockVisitor.visit(ctx.elseBlock));
        }
        return construct;
    }
}
