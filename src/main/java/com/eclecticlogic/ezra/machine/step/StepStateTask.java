package com.eclecticlogic.ezra.machine.step;

import com.eclecticlogic.ezra.machine.AttributableStepState;
import com.eclecticlogic.ezra.machine.StateType;

public class StepStateTask extends AttributableStepState {

    public StepStateTask() {
        setType(StateType.TASK);
    }
}
