package com.eclecticlogic.ezra.machine;

import com.eclecticlogic.ezra.machine.step.StepStatePass;

import java.util.ArrayList;
import java.util.List;

public class StepMachine implements CompositeStepState {

    private final List<StepState> states = new ArrayList<>();


    public StepMachine() {
        StepStatePass pass = new StepStatePass();
        pass.setResultPath("$.input");
        getStates().add(pass);

    }


    @Override
    public List<StepState> getStates() {
        return states;
    }


    @Override
    public String getJsonRepresentation() {
        return null;
    }
}
