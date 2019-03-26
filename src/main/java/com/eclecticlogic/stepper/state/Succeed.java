package com.eclecticlogic.stepper.state;

public class Succeed extends AbstractState {

    public Succeed() {
        this(null);
    }


    public Succeed(String label) {
        super(label);
        setType(StateType.SUCCEED);
    }
}
