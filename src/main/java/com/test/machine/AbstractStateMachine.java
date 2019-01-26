package com.test.machine;

import com.test.StepState;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractStateMachine {
    final List<StepState> states = new ArrayList<>();

    public List<StepState> getStates() {
        return states;
    }
}
