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
import com.eclecticlogic.stepper.construct.ExpressionConstruct;
import com.eclecticlogic.stepper.construct.StateConstruct;
import com.eclecticlogic.stepper.state.Pass;
import com.eclecticlogic.stepper.state.Task;

import java.math.BigDecimal;
import java.util.Set;

import static com.eclecticlogic.stepper.etc.Etc.*;


public class AssignmentVisitor extends AbstractVisitor<Construct> {


    @Override
    public Construct visitAssignmentTask(StepperParser.AssignmentTaskContext ctx) {
        RetryVisitor<Task> retryVisitor = new RetryVisitor<>(Task::new);
        Task task = retryVisitor.visit(ctx.retries());

        JsonObjectVisitor jsonObjectVisitor = new JsonObjectVisitor(task);
        jsonObjectVisitor.visit(ctx.task().jsonObject());

        task.setResultPath("$." + ctx.dereference().getText());
        return new StateConstruct<>(task);
    }


    @Override
    public Construct visitAssignmentNumber(StepperParser.AssignmentNumberContext ctx) {
        Pass pass = new Pass(toLabel(ctx.label()));
        pass.captureAttribute("Result");
        pass.setProperty(new BigDecimal(ctx.NUMBER().getText()));
        pass.setResultPath("$." + ctx.dereference().getText());
        return new StateConstruct<>(pass);
    }


    @Override
    public Construct visitAssignmentString(StepperParser.AssignmentStringContext ctx) {
        Pass pass = new Pass(toLabel(ctx.label()));
        pass.captureAttribute("Result");
        pass.setProperty(strip(ctx.STRING().getText()));
        pass.setResultPath("$." + ctx.dereference().getText());
        return new StateConstruct<>(pass);
    }


    Construct visitAssignmentBoolean(String var, boolean value, StepperParser.LabelContext lbl) {
        Pass pass = new Pass(toLabel(lbl));
        pass.captureAttribute("Result");
        pass.setProperty(value);
        pass.setResultPath("$." + var);
        return new StateConstruct<>(pass);
    }


    @Override
    public Construct visitAssignmentTrue(StepperParser.AssignmentTrueContext ctx) {
        return visitAssignmentBoolean(ctx.dereference().getText(), true, ctx.label());
    }


    @Override
    public Construct visitAssignmentFalse(StepperParser.AssignmentFalseContext ctx) {
        return visitAssignmentBoolean(ctx.dereference().getText(), false, ctx.label());
    }


    @Override
    public Construct visitAssignmentJson(StepperParser.AssignmentJsonContext ctx) {
        Pass pass = new Pass(toLabel(ctx.label()));
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
        Pass pass = new Pass(toLabel(ctx.label()));
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
        ExpressionConstruct construct = new ExpressionConstruct(toLabel(ctx.label()));
        String variable = ctx.dereference().getText();
        String expression = enhance(ctx.complexAssign(), ctx.expr().getText(), variable);

        construct.setVariable(variable);
        construct.setExpression(expression);

        DereferencingVisitor defVisitor = new DereferencingVisitor();
        Set<String> symbols = defVisitor.visit(ctx.expr());
        if (ctx.complexAssign().ASSIGN() == null) {
            symbols.add(variable);
        }
        construct.setSymbols(symbols);
        return construct;
    }

}
