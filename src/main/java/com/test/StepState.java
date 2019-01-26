package com.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.MessageFormat;

public class StepState {

    String stateName;

    StepState next;

    JsonData jsonData = new JsonData();


    public String getStateName() {
        return stateName;
    }

    public String getJsonRepresentation() {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonData.toString()).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return MessageFormat.format("\"{0}\": {1}", stateName, gson.toJson(json));
    }


    public void setType(StateType type) {
        jsonData.captureQuotedAttribute("Type");
        jsonData.setQuotedAttributeValue(type.getName());
    }


    public void setResultPath(String value) {
        jsonData.captureQuotedAttribute("ResultPath");
        jsonData.setQuotedAttributeValue(value);
    }
}
