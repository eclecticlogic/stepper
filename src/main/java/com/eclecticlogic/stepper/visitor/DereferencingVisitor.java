package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.google.common.collect.Sets;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.Set;

public class DereferencingVisitor extends StepperBaseVisitor<Set<String>> {

    @Override
    public Set<String> visitExpr(StepperParser.ExprContext ctx) {
        ParseTreeWalker walker = new ParseTreeWalker();
        DereferenceListener listener = new DereferenceListener();
        walker.walk(listener, ctx);
        return Sets.newHashSet(listener.getReferences());
    }
}
