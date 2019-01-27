package com.eclecticlogic.ezra.machine.construct;

import com.eclecticlogic.ezra.machine.StepState;

public class StateConstruct extends StepConstruct {

    private final StepState state;


    public StateConstruct(StepState state) {
        this.state = state;
    }

    public StepState getState() {
        return state;
    }


}
