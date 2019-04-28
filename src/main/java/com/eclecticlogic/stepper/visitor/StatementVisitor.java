package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.construct.*;
import com.eclecticlogic.stepper.state.Parallel;
import com.eclecticlogic.stepper.state.Task;

import java.util.List;
import java.util.stream.Collectors;

import static com.eclecticlogic.stepper.etc.Etc.strip;
import static com.eclecticlogic.stepper.etc.Etc.toLabel;
import static java.util.stream.Collectors.toList;


public class StatementVisitor extends StepperBaseVisitor<Construct> {


    @Override
    public Construct visitStatementTask(StepperParser.StatementTaskContext ctx) {
        RetryVisitor<Task> retryVisitor = new RetryVisitor<>(Task::new);
        Task task = retryVisitor.visit(ctx.retries());

        JsonObjectVisitor jsonObjectVisitor = new JsonObjectVisitor(task);
        jsonObjectVisitor.visit(ctx.task().jsonObject());

        return new StateConstruct<>(task);
    }


    @Override
    public Construct visitStatementParallel(StepperParser.StatementParallelContext stCtx) {
        StepperParser.ParallelStatementContext ctx = stCtx.parallelStatement();
        RetryVisitor<Parallel> retryVisitor = new RetryVisitor<>(Parallel::new);
        Parallel parallel = retryVisitor.visit(ctx.retries());

        ParallelConstruct construct = new ParallelConstruct(parallel);
        List<String> references = ctx.STRING().stream().map(t -> strip(t.getText())).collect(toList());
        construct.setReferences(references);

        if (ctx.dereference() != null) {
            construct.setResultPath("$." + ctx.dereference().getText());
        }
        return construct;
    }


    @Override
    public Construct visitStatementAssignment(StepperParser.StatementAssignmentContext ctx) {
        AssignmentVisitor visitor = new AssignmentVisitor();
        return visitor.visit(ctx.assignment());
    }


    @Override
    public Construct visitIfStatement(StepperParser.IfStatementContext ctx) {
        IfConstruct construct = new IfConstruct(toLabel(ctx.label()));
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
        WhileConstruct construct = new WhileConstruct(toLabel(ctx.label()));
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
        for (StepperParser.CaseEntryContext caseEntry : ctx.caseEntry()) {
            WhenCase wcase = construct.addCase(toLabel(caseEntry.label()));

            DereferencingVisitor defVisitor = new DereferencingVisitor();
            StatementBlockVisitor statementBlockVisitor = new StatementBlockVisitor();
            Construct blockConstruct = statementBlockVisitor.visit(caseEntry.statementBlock());

            wcase.setSymbols(defVisitor.visit(caseEntry.expr()));
            wcase.setExpression(caseEntry.expr().getText());
            wcase.setBlock(blockConstruct);
        }

        if (ctx.elseBlock != null) {
            StatementBlockVisitor statementBlockVisitor = new StatementBlockVisitor();
            construct.setElseBlock(statementBlockVisitor.visit(ctx.elseBlock));
        }
        return construct;
    }


    @Override
    public Construct visitStatementWait(StepperParser.StatementWaitContext ctx) {
        WaitConstruct construct = new WaitConstruct(toLabel(ctx.waitStatement().label()));

        JsonObjectVisitor jsonObjectVisitor = new JsonObjectVisitor(construct.getState());
        jsonObjectVisitor.visit(ctx.waitStatement().jsonObject());

        return construct;
    }


    @Override
    public Construct visitStatementFail(StepperParser.StatementFailContext ctx) {
        FailConstruct construct = new FailConstruct(toLabel(ctx.failStatement().label()));

        if (ctx.failStatement().jsonObject() != null) {
            JsonObjectVisitor jsonObjectVisitor = new JsonObjectVisitor(construct.getState());
            jsonObjectVisitor.visit(ctx.failStatement().jsonObject());
        }

        return construct;
    }


    @Override
    public Construct visitStatementGoto(StepperParser.StatementGotoContext ctx) {
        return new GotoConstruct(strip(ctx.gotoStatement().STRING().getText()));
    }
}
