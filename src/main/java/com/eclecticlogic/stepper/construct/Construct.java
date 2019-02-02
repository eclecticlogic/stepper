package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.state.State;
import com.google.common.collect.Lists;

import java.util.List;

public abstract class Construct {

    private static int dynamicVariableCounter;
    private Construct next;

    public abstract List<State> getStates();


    Construct getNext() {
        return next;
    }

    public void setNext(Construct value) {
        next = value;
    }


    String getNextDynamicVariable() {
        return String.format("var__%03d", dynamicVariableCounter++);
    }


    List<State> getStates(State... first) {
        List<State> result = Lists.newArrayList(first);
        if (next != null) {
            result.addAll(next.getStates());
        }
        return result;
    }


    protected abstract String getFirstStateName();


    protected abstract void setNextStateName(String name);


    public abstract void weave();


    Construct getLastInChain(Construct input) {
        Construct next = input;
        while (next.getNext() != null) {
            next = next.getNext();
        }
        return next;
    }

}
