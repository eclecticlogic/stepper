package com.eclecticlogic.stepper.state;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.text.MessageFormat;

public abstract class AbstractState implements State {

    private static int stateNameCounter;

    private String stateName;

    JsonObject json = new JsonObject();

    AbstractState() {
        stateName = String.format("state%03d", stateNameCounter++);
    }


    @Override
    public String getName() {
        return stateName;
    }


    @Override
    public String getJsonRepresentation() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return MessageFormat.format("\"{0}\": {1}", stateName, gson.toJson(json));
    }


    void setType(StateType type) {
        json.addProperty("Type", type.getName());
    }

    public void setResultPath(String value) {
        json.addProperty("ResultPath", value);
    }

    public void setNextState(String value) {
        json.addProperty("Next", value);
    }

}
