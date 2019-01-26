package com.eclecticlogic.ezra.machine.step;

import com.eclecticlogic.ezra.machine.AttributableStepState;
import com.eclecticlogic.ezra.machine.StateType;

public class StepStatePass extends AttributableStepState {

    public StepStatePass() {
        setType(StateType.PASS);
    }
}
