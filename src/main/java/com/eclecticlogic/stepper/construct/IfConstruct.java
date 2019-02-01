package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.state.Choice;
import com.eclecticlogic.stepper.state.State;
import com.eclecticlogic.stepper.state.Task;
import com.google.common.collect.Lists;

import java.util.List;

public class IfConstruct extends Construct {

    private String conditionText;
    private List<String> conditionDeferences;
    private Construct firstIf, firstElse;

    private Task conditionTask = new Task();
    private String choiceVariable = getNextDynamicVariable();
    private Choice choice = new Choice(choiceVariable);


    public void setCondition(String condition) {
        conditionText = condition;
    }


    public void setConditionDeferences(List<String> conditionDeferences) {
        this.conditionDeferences = conditionDeferences;
    }


    public void setFirstIf(Construct firstIf) {
        this.firstIf = firstIf;
    }


    public void setFirstElse(Construct firstElse) {
        this.firstElse = firstElse;
    }


    public void setup() {
        conditionTask.captureAttribute("Parameters");
        conditionTask.handleObject(() ->
                conditionDeferences.forEach(it -> {
                    conditionTask.captureAttribute(it + ".$");
                    conditionTask.setProperty("$." + it);
                }));

        conditionTask.captureAttribute("Resource");
        conditionTask.setProperty("lambda arn for condition");
        conditionTask.setResultPath("$." + choiceVariable);
    }


    @Override
    public List<State> getStates() {
        List<State> result = Lists.newArrayList(conditionTask, choice);
        result.addAll(firstIf.getStates());
        if (firstElse != null) {
            result.addAll(firstElse.getStates());
        }
        if (getNext() != null) {
            result.addAll(getNext().getStates());
        }
        return result;
    }


}
