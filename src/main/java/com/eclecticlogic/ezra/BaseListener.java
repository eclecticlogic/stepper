package com.eclecticlogic.ezra;

import com.eclecticlogic.ezra.antlr.EzraBaseListener;
import com.eclecticlogic.ezra.antlr.EzraParser;
import com.eclecticlogic.ezra.machine.construct.CompositeStepConstruct;
import com.eclecticlogic.ezra.machine.construct.RootConstruct;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class BaseListener extends EzraBaseListener {

    private final Stack<CompositeStepConstruct> constructStack = new Stack<>();

    protected CompositeStepConstruct stepConstruct = new RootConstruct();

    private final Set<DereferenceListener> dereferenceListeners = new HashSet<>();

    protected void push(CompositeStepConstruct construct) {
        stepConstruct.getConstructs().add(construct);
        constructStack.push(stepConstruct);
        stepConstruct = construct;
    }


    protected void registerDeferenceListener(DereferenceListener listener) {
        dereferenceListeners.add(listener);
    }


    protected void unregisterDeferenceListener(DereferenceListener listener) {
        dereferenceListeners.remove(listener);
    }


    @Override
    public void enterDereference(EzraParser.DereferenceContext ctx) {
        dereferenceListeners.forEach(it -> it.onDeference(ctx.ID(0).getText()));
    }
}
