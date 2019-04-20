package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.etc.WeaveContext;
import com.eclecticlogic.stepper.state.Fail;

public class FailConstruct extends StateConstruct<Fail> {

    public FailConstruct(String label) {
        super(new Fail(label));
    }


    @Override
    public Fail getState() {
        return super.getState();
    }


    @Override
    protected void setNextStateName(String name) {
        // noop
    }


    @Override
    public void weave(WeaveContext context) {
        if (getNext() != null) {
            getNext().weave(context);
        }
    }


}
