package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.antlr.StepperParser;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.List;

public class DereferencingVisitor extends StepperBaseVisitor<List<String>> {

    @Override
    public List<String> visitExpr(StepperParser.ExprContext ctx) {
        ParseTreeWalker walker = new ParseTreeWalker();
        DereferenceListener listener = new DereferenceListener();
        walker.walk(listener, ctx);
        return listener.getReferences();
    }
}
