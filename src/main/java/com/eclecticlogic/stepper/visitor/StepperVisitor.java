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

import com.eclecticlogic.stepper.StateMachine;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.construct.Construct;
import com.eclecticlogic.stepper.construct.ProgramConstruct;
import com.eclecticlogic.stepper.construct.SuccessConstruct;
import com.eclecticlogic.stepper.etc.WeaveContext;
import com.eclecticlogic.stepper.state.NameProvider;
import com.eclecticlogic.stepper.state.observer.StateObserver;

import java.math.BigDecimal;

public class StepperVisitor extends AbstractVisitor<StateMachine> {

    private final WeaveContext weaveContext;
    private boolean suppressAnnotations;


    public StepperVisitor() {
        this(new WeaveContext());
    }


    public StepperVisitor(WeaveContext weaveContext) {
        this.weaveContext = weaveContext;
    }


    public void setSuppressAnnotations(boolean suppressAnnotations) {
        this.suppressAnnotations = suppressAnnotations;
    }


    @Override
    public StateMachine visitProgram(StepperParser.ProgramContext ctx) {
        ProgramConstruct program = new ProgramConstruct();

        program.setProgramName(ctx.ID().getText());
        if (!suppressAnnotations) {
            for (StepperParser.AnnotationContext anCtx : ctx.annotation()) {
                if (anCtx.scalar().STRING() != null) {
                    program.addAnnotation(anCtx.ID().getText(), anCtx.scalar().getText());
                } else if (anCtx.scalar().NUMBER() != null) {
                    program.addAnnotation(anCtx.ID().getText(), new BigDecimal(anCtx.scalar().getText()));
                } else {
                    program.addAnnotation(anCtx.ID().getText(), Boolean.parseBoolean(anCtx.scalar().getText()));
                }
            }
        }

        StateObserver.over(() -> {
            NameProvider.usingName(program.getProgramName(), () -> {
                Construct current = program;
                for (StepperParser.StatementContext stCtx : ctx.statement()) {
                    StatementVisitor visitor = new StatementVisitor();
                    Construct c = visitor.visit(stCtx);
                    current.setNext(c);
                    current = c;
                }
                current.setNext(new SuccessConstruct(program.getProgramName()));

                program.weave(weaveContext);
            });
        });

        return new StateMachine(program);
    }


}
