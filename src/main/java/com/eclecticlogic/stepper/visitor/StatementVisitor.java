/*
Copyright 2015-2019 KR Abram

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.construct.*;
import com.eclecticlogic.stepper.etc.Etc;
import com.eclecticlogic.stepper.state.Parallel;
import com.eclecticlogic.stepper.state.Task;
import com.eclecticlogic.stepper.state.observer.StateObserver;

import java.util.List;

import static com.eclecticlogic.stepper.etc.Etc.strip;
import static com.eclecticlogic.stepper.etc.Etc.toLabel;
import static java.util.stream.Collectors.toList;


public class StatementVisitor extends AbstractVisitor<Construct> {


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


    @Override
    public Construct visitTryCatchStatement(StepperParser.TryCatchStatementContext ctx) {
        TryCatchConstruct construct = new TryCatchConstruct();
        StateObserver.register(construct, () -> {
            StatementBlockVisitor statementBlockVisitor = new StatementBlockVisitor();
            Construct blockConstruct = statementBlockVisitor.visit(ctx.statementBlock());
            construct.setTryBlock(blockConstruct);
        });
        ctx.catchClause().forEach(clause -> {
            CatchClause cc = new CatchClause();
            cc.setErrors(clause.STRING().stream().map(it -> strip(it.getText())).collect(toList()));

            Construct current = null, first = null;
            for (StepperParser.StatementContext stCtx : clause.statement()) {
                StatementVisitor visitor = new StatementVisitor();
                Construct c = visitor.visit(stCtx);
                if (current == null) {
                    current = c;
                    first = c;
                } else {
                    current.setNext(c);
                    current = c;
                }
            }
            cc.setBlock(first);

            if (clause.dereference() != null) {
                cc.setResultPath(clause.dereference().getText());
            }
            construct.add(cc);
        });
        return construct;
    }
}
