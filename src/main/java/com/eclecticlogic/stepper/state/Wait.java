package com.eclecticlogic.stepper.state;

public class Wait extends AttributableState {

    public Wait() {
        this(null);
    }


    public Wait(String label) {
        super(label);
        setType(StateType.WAIT);
    }
}
