package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.etc.StringHelper;
import com.eclecticlogic.stepper.etc.WeaveContext;
import com.eclecticlogic.stepper.state.State;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import static com.eclecticlogic.stepper.etc.StringHelper.strip;

public class ProgramConstruct extends Construct {

    private final JsonObject stepFunction = new JsonObject();
    private String programName;
    private WeaveContext context;


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
    public void weave(WeaveContext context) {
        this.context = context;
        getNext().weave(context);
        stepFunction.addProperty("StartAt", getFirstStateName());

        JsonObject states = new JsonObject();
        getStates().stream().forEach(state -> states.add(state.getName(), state.toJson()));
        stepFunction.add("States", states);
    }


    public void setProgramName(String programName) {
        this.programName = programName;
    }


    public String getProgramName() {
        return programName;
    }


    public void addAnnotation(String name, String value) {
        stepFunction.addProperty(name, strip(value));
    }


    public void addAnnotation(String name, Number value) {
        stepFunction.addProperty(name, value);
    }


    public void addAnnotation(String name, boolean value) {
        stepFunction.addProperty(name, value);
    }


    public WeaveContext getContext() {
        return context;
    }


    public JsonObject toJson() {
        return stepFunction;
    }
}
