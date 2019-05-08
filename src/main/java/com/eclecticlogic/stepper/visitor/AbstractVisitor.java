package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.state.observer.StateObserver;

public abstract class AbstractVisitor<T> extends StepperBaseVisitor<T> {

    private final StateObserver stateObserver;


    public AbstractVisitor(StateObserver observer) {
        this.stateObserver = observer;
    }


    public StateObserver getStateObserver() {
        return stateObserver;
    }
}
