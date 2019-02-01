package com.eclecticlogic.stepper.state;

public interface State {

    String getName();

    void setNextState(String value);

    String getJsonRepresentation();
}
