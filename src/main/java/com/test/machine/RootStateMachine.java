package com.test.machine;

import com.test.StepStatePass;

public class RootStateMachine extends AbstractStateMachine {

    public RootStateMachine() {
        StepStatePass pass = new StepStatePass();
        pass.setResultPath("$.input");
        getStates().add(pass);

    }


}
