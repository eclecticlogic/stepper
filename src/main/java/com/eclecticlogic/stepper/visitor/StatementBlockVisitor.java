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

public class StatementBlockVisitor extends AbstractVisitor<Construct> {


    @Override
    public Construct visitStatementBlock(StepperParser.StatementBlockContext ctx) {
        Construct first = null;
        Construct current = null;
        for (StepperParser.StatementContext stCtx : ctx.statement()) {
            StatementVisitor visitor = new StatementVisitor();
            Construct c = visitor.visit(stCtx);
            if (first == null) {
                first = c;
                current = c;
            } else {
                current.setNext(c);
                current = c;
            }
        }
        return first;
    }
}
