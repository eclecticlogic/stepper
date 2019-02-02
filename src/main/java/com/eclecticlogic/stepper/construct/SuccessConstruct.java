package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.state.Succeed;

public class SuccessConstruct extends StateConstruct<Succeed> {

    public SuccessConstruct() {
        super(new Succeed());
    }
}
