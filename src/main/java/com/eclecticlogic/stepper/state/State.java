package com.eclecticlogic.stepper.state;

import com.google.gson.JsonObject;

public interface State {

    String getName();

    void setNextState(String value);

    JsonObject toJson();
}
