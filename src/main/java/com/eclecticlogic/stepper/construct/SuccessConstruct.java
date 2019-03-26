package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.state.Succeed;

public class SuccessConstruct extends StateConstruct<Succeed> {

    public SuccessConstruct() {
        super(new Succeed());
    }


    public SuccessConstruct(String programName) {
        super(new Succeed(programName + ".Success"));
    }
}
