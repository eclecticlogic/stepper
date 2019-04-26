package com.eclecticlogic.stepper.state;

public class Parallel extends AttributableState {

    public Parallel() {
        this(null);
    }


    public Parallel(String stateName) {
        super(stateName);
        setType(StateType.PARALLEL);
    }
}
