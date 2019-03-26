package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.StateMachine;
import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.construct.Construct;
import com.eclecticlogic.stepper.construct.ProgramConstruct;
import com.eclecticlogic.stepper.construct.SuccessConstruct;
import com.eclecticlogic.stepper.etc.WeaveContext;
import com.eclecticlogic.stepper.state.NameProvider;

import java.math.BigDecimal;

public class StepperVisitor extends StepperBaseVisitor<StateMachine> {

    @Override
    public StateMachine visitProgram(StepperParser.ProgramContext ctx) {
        ProgramConstruct program = new ProgramConstruct();

        program.setProgramName(ctx.ID().getText());
        for (StepperParser.AnnotationContext anCtx : ctx.annotation()) {
            if (anCtx.scalar().STRING() != null) {
                program.addAnnotation(anCtx.ID().getText(), anCtx.scalar().getText());
            } else if (anCtx.scalar().NUMBER() != null) {
                program.addAnnotation(anCtx.ID().getText(), new BigDecimal(anCtx.scalar().getText()));
            } else {
                program.addAnnotation(anCtx.ID().getText(), Boolean.parseBoolean(anCtx.scalar().getText()));
            }
        }

        NameProvider.usingName(program.getProgramName(), () -> {
            Construct current = program;
            for (StepperParser.StatementContext stCtx : ctx.statement()) {
                StatementVisitor visitor = new StatementVisitor();
                Construct c = visitor.visit(stCtx);
                current.setNext(c);
                current = c;
            }
            current.setNext(new SuccessConstruct(program.getProgramName()));

            program.weave(new WeaveContext());
        });

        return new StateMachine(program);
    }


}
