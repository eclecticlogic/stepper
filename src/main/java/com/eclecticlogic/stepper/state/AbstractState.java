package com.eclecticlogic.stepper.state;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.text.MessageFormat;

public abstract class AbstractState implements State {

    private String stateName;

    JsonObject json = new JsonObject();


    AbstractState() {
        this(null);
    }


    AbstractState(String stateName) {
        this.stateName = stateName == null ? NameProvider.getName() : stateName;
    }


    @Override
    public String getName() {
        return stateName;
    }


    @Override
    public JsonObject toJson() {
        return json;
    }


    void setType(StateType type) {
        json.addProperty("Type", type.getName());
    }


    public void setupLambdaHelper() {
        json.addProperty("Resource", "@@@lambda_helper_arn@@@");
    }


    public void setResultPath(String value) {
        json.addProperty("ResultPath", value);
    }


    public void setNextState(String value) {
        json.addProperty("Next", value);
    }
}
