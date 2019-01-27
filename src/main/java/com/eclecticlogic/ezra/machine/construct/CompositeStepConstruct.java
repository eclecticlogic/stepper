package com.eclecticlogic.ezra.machine.construct;

import java.util.ArrayList;
import java.util.List;

public class CompositeStepConstruct extends StepConstruct {

    private final List<StepConstruct> constructs = new ArrayList<>();


    public List<StepConstruct> getConstructs() {
        return constructs;
    }
}
