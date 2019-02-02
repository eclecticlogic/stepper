package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.state.Pass;

public class ProgramConstruct extends StateConstruct<Pass> {


    public ProgramConstruct() {
        super(new Pass());
        getState().setResultPath("$.input");
    }

}
