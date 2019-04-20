package com.eclecticlogic.stepper.state;

public class Fail extends AttributableState {

    public Fail() {
        this(null);
    }


    public Fail(String label) {
        super(label);
        setType(StateType.FAIL);
    }
}
