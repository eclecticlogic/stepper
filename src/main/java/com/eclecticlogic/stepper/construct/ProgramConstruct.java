package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.state.State;
import com.google.common.collect.Lists;

import java.util.List;

public class ProgramConstruct extends Construct {

    private String programName;
    private final List<String> machineAttributes = Lists.newArrayList();

    @Override
    public String getFirstStateName() {
        return getNext().getFirstStateName();
    }


    @Override
    protected void setNextStateName(String name) {
        // noop
    }


    @Override
    public List<State> getStates() {
        return getNext().getStates();
    }


    @Override
    public void weave() {
        getNext().weave();
    }


    public void setProgramName(String programName) {
        this.programName = programName;
    }


    public String getProgramName() {
        return programName;
    }


    public void addAnnotation(String name, String value) {
        machineAttributes.add("\"" + name + "\": " + value);
    }


    public List<String> getMachineAttributes() {
        return machineAttributes;
    }
}
