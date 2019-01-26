package com.eclecticlogic.ezra.machine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.text.MessageFormat;

public abstract class AbstractStepState implements StepState {

    String stateName;

    AbstractStepState next;

    JsonObject json = new JsonObject();


    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    @Override
    public String getJsonRepresentation() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return MessageFormat.format("\"{0}\": {1}", stateName, gson.toJson(json));
    }


    public void setType(StateType type) {
        json.addProperty("Type", type.getName());
    }

    public void setResultPath(String value) {
        json.addProperty("ResultPath", value);
    }


}
