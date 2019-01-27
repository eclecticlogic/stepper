package com.eclecticlogic.ezra.machine.construct;

import com.eclecticlogic.ezra.machine.step.StepStatePass;

public class RootConstruct extends CompositeStepConstruct {

    public RootConstruct() {
        StepStatePass pass = new StepStatePass();
        pass.setResultPath("$.input");
        StateConstruct state = new StateConstruct(pass);
        getConstructs().add(state);
    }

}
