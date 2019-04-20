package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.etc.WeaveContext;
import com.eclecticlogic.stepper.state.Pass;

public class GotoConstruct extends StateConstruct<Pass> {

    public GotoConstruct(String nextTarget) {
        super(new Pass());
        super.setNextStateName(nextTarget);
    }


    @Override
    public Pass getState() {
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
