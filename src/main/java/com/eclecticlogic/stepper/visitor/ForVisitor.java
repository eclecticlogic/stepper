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
import com.eclecticlogic.stepper.construct.Construct;
import com.eclecticlogic.stepper.construct.ForIterationConstruct;
import com.eclecticlogic.stepper.construct.ForLoopConstruct;

import static com.eclecticlogic.stepper.etc.Etc.toLabel;

public class ForVisitor extends AbstractVisitor<Construct> {


    @Override
    public Construct visitForIteration(StepperParser.ForIterationContext ctx) {
        ForIterationConstruct construct = new ForIterationConstruct(toLabel(ctx.label()));
        construct.setIterableVariable(ctx.ID().getText());
        construct.setIterableExpression(ctx.iterable.getText());

        DereferencingVisitor defVisitor = new DereferencingVisitor();
        construct.setSymbols(defVisitor.visit(ctx.iterable));

        StatementBlockVisitor visitor = new StatementBlockVisitor();
        construct.setBlock(visitor.visit(ctx.statementBlock()));

        return construct;
    }


    @Override
    public Construct visitForLoop(StepperParser.ForLoopContext ctx) {
        ForLoopConstruct construct = new ForLoopConstruct(toLabel(ctx.label()));
        construct.setIterableVariable(ctx.ID().getText());

        construct.setInitialExpression(ctx.init.getText());
        {
            DereferencingVisitor defVisitor = new DereferencingVisitor();
            construct.setInitialExpressionSymbols(defVisitor.visit(ctx.init));
        }

        construct.setEndingExpression(ctx.end.getText());
        {
            DereferencingVisitor defVisitor = new DereferencingVisitor();
            construct.setEndingExpressionSymbols(defVisitor.visit(ctx.end));
        }

        if (ctx.delta != null) {
            construct.setStepExpression(ctx.delta.getText());
            DereferencingVisitor defVisitor = new DereferencingVisitor();
            construct.setStepExpressionSymbols(defVisitor.visit(ctx.delta));
        }

        StatementBlockVisitor visitor = new StatementBlockVisitor();
        construct.setBlock(visitor.visit(ctx.statementBlock()));

        return construct;
    }
}
