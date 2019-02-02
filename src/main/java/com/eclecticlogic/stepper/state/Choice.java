package com.eclecticlogic.stepper.state;

import com.google.gson.JsonObject;

public class Choice extends AttributableState {

    private JsonObject objIf, objElse;

    public Choice(String variable) {
        setType(StateType.CHOICE);
        captureAttribute("Choices");
        handleArray(() -> {
            // if
            objIf = handleObject(() -> {
                captureAttribute("Variable");
                setProperty("$." + variable);
                captureAttribute("BooleanEquals");
                setProperty(true);
            });

            // else
            objElse = handleObject(() -> {
                captureAttribute("Variable");
                setProperty("$." + variable);
                captureAttribute("BooleanEquals");
                setProperty(false);
            });
        });
    }


    public void setIfNextState(String value) {
        objIf.addProperty("Next", value);
    }


    public void setElseNextState(String value) {
        objElse.addProperty("Next", value);
    }
}
