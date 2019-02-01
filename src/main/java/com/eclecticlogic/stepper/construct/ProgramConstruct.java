package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.state.Pass;
import com.eclecticlogic.stepper.state.State;

import java.util.List;

public class ProgramConstruct extends Construct {

    private Pass input;

    public ProgramConstruct() {
        input = new Pass();
        input.setResultPath("$.input");
    }

    public String getStartState() {
        return input.getName();
    }


    @Override
    public List<State> getStates() {
        return getStates(input);
    }
}
