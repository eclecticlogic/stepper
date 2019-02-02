package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.state.State;

import java.util.List;

public class ProgramConstruct extends Construct {

    @Override
    public String getFirstStateName() {
        return getNext().getFirstStateName();
    }


    @Override
    protected void setNextStateName(String name) {
        // noop
    }


    @Override
    public List<State> getStates() {
        return getNext().getStates();
    }


    @Override
    public void weave() {
        getNext().weave();
    }
}
