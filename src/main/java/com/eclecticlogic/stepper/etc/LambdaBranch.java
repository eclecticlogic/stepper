package com.eclecticlogic.stepper.etc;

import java.util.List;
import java.util.Map;

public class LambdaBranch {

    private String commandName;
    private List<String> inputs;
    private String computation;
    private String outputExpression;

    public String getCommandName() {
        return commandName;
    }


    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }


    public List<String> getInputs() {
        return inputs;
    }


    public void setInputs(List<String> inputs) {
        this.inputs = inputs;
    }


    public String getComputation() {
        return computation;
    }


    public void setComputation(String computation) {
        this.computation = computation;
    }


    public String getOutputExpression() {
        return outputExpression;
    }


    public void setOutputExpression(String outputExpression) {
        this.outputExpression = outputExpression;
    }
}
