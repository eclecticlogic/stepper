package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.state.State;

import java.util.List;

public class StateConstruct extends Construct {

    private final State state;


    public StateConstruct(State state) {
        this.state = state;
    }


    @Override
    public List<State> getStates() {
        return getStates(state);
    }


}
