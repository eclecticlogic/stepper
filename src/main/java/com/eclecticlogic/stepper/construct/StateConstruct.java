package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.state.State;

import java.util.List;

public class StateConstruct<T extends State> extends Construct {

    private final T state;


    public StateConstruct(T state) {
        this.state = state;
    }


    protected T getState() {
        return state;
    }


    @Override
    protected String getFirstStateName() {
        return state.getName();
    }


    @Override
    protected void setNextStateName(String name) {
        state.setNextState(name);
    }


    @Override
    public void weave() {
        if (getNext() != null) {
            state.setNextState(getNext().getFirstStateName());
            getNext().weave();
        }
    }


    @Override
    public List<State> getStates() {
        return getStates(state);
    }


}
