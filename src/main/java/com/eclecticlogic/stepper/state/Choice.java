package com.eclecticlogic.stepper.state;

public class Choice extends AttributableState {

    private String variable;

    public Choice() {
        setType(StateType.CHOICE);
    }

    public Choice(String variable) {
        this.variable = variable;
    }


    public void setup(String ifState, String elseState) {
        captureAttribute("Choices");
        handleArray(() -> {
            // if
            handleObject(() -> {
                captureAttribute("Variable");
                setProperty("$." + variable);
                captureAttribute("BooleanEquals");
                setProperty(true);
                captureAttribute("Next");
                setProperty(ifState);
            });

            // else
            handleObject(() -> {
                captureAttribute("Variable");
                setProperty("$." + variable);
                captureAttribute("BooleanEquals");
                setProperty(false);
                captureAttribute("Next");
                setProperty(elseState);
            });
        });

    }
}
