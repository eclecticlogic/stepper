package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.state.Succeed;

public class SuccessConstruct extends StateConstruct {

    public SuccessConstruct() {
        super(new Succeed());
    }
}
