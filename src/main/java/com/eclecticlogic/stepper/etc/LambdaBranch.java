package com.eclecticlogic.stepper.etc;

import java.util.Collection;

public class LambdaBranch {

    private String commandName;
    private Collection<String> inputs;
    private String computation;
    private String outputExpression;

    public String getCommandName() {
        return commandName;
    }


    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }


    public Collection<String> getInputs() {
        return inputs;
    }


    public void setInputs(Collection<String> inputs) {
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
