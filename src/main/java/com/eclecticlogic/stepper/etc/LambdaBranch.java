package com.eclecticlogic.stepper.etc;

import java.util.List;
import java.util.Map;

public class LambdaBranch {

    private String commandName;
    private List<String> inputs;
    private Map<String, String> outputs;
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


    public Map<String, String> getOutputs() {
        return outputs;
    }


    public void setOutputs(Map<String, String> outputs) {
        this.outputs = outputs;
    }


    public String getOutputExpression() {
        return outputExpression;
    }


    public void setOutputExpression(String outputExpression) {
        this.outputExpression = outputExpression;
    }
}
