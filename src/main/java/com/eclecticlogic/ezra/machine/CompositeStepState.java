package com.eclecticlogic.ezra.machine;

import java.util.List;

public interface CompositeStepState extends StepState {

    List<StepState> getStates();
}
