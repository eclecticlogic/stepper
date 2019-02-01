package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.construct.Construct;
import com.eclecticlogic.stepper.construct.ProgramConstruct;
import com.eclecticlogic.stepper.construct.SuccessConstruct;

public class StepperVisitor extends StepperBaseVisitor<ProgramConstruct> {

    @Override
    public ProgramConstruct visitProgram(StepperParser.ProgramContext ctx) {
        ProgramConstruct program = new ProgramConstruct();
        Construct current = program;
        for (StepperParser.StatementContext stCtx : ctx.statement()) {
            StatementVisitor visitor = new StatementVisitor();
            Construct c = visitor.visit(stCtx);
            current.setNext(c);
            current = c;
        }
        current.setNext(new SuccessConstruct());
        return program;
    }


}
