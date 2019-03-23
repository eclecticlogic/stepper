package com.eclecticlogic.stepper.state;


public class Task extends AttributableState {

    public Task() {
        this(null);
    }


    public Task(String stateName) {
        super(stateName);
        setType(StateType.TASK);
    }


}
