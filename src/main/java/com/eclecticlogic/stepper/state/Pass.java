package com.eclecticlogic.stepper.state;

public class Pass extends AttributableState {

    public Pass() {
        this(null);
    }


    public Pass(String stateName) {
        super(stateName);
        setType(StateType.PASS);
    }
}
