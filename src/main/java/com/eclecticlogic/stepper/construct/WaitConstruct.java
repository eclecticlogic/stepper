package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.state.Wait;

public class WaitConstruct extends StateConstruct<Wait> {

    public WaitConstruct(String label) {
        super(new Wait(label));
    }


    @Override
    public Wait getState() {
        return super.getState();
    }
}
