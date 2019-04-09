package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.etc.WeaveContext;
import com.eclecticlogic.stepper.state.Choice;
import com.eclecticlogic.stepper.state.State;
import com.eclecticlogic.stepper.state.Task;
import com.google.common.collect.Lists;

import java.util.List;

public class IfConstruct extends Construct {

    private String conditionText;
    private List<String> symbols;
    private Construct firstIf, firstElse;

    private final Task conditionTask;
    private final String choiceVariable;
    private final Choice choice;


    public IfConstruct(String label) {
        conditionTask = new Task(label);
        choiceVariable = getNextDynamicVariable();
        choice = new Choice(choiceVariable);
    }


    public void setCondition(String condition) {
        conditionText = condition;
    }


    public void setSymbols(List<String> conditionDeferences) {
        this.symbols = conditionDeferences;
    }


    public void setFirstIf(Construct firstIf) {
        this.firstIf = firstIf;
    }


    public void setFirstElse(Construct firstElse) {
        this.firstElse = firstElse;
    }


    void setupCondition(WeaveContext context) {
        constructLambda(context, conditionTask, conditionText, symbols);

        conditionTask.setupLambdaHelper();
        conditionTask.setResultPath("$." + choiceVariable);
        conditionTask.setNextState(choice.getName());

        choice.setIfNextState(firstIf.getFirstStateName());
        String elseName = "none";
        if (firstElse != null) {
            elseName = firstElse.getFirstStateName();
        } else if (getNext() != null) {
            elseName = getNext().getFirstStateName();
        }
        choice.setElseNextState(elseName);
    }


    @Override
    protected String getFirstStateName() {
        return conditionTask.getName();
    }


    @Override
    protected void setNextStateName(String name) {
        getLastInChain(firstIf).setNextStateName(name);
        if (firstElse != null) {
            getLastInChain(firstElse).setNextStateName(name);
        } else {
            choice.setElseNextState(name);
        }
    }


    @Override
    public void weave(WeaveContext context) {
        setupCondition(context);
        firstIf.weave(context);
        if (firstElse != null) {
            firstElse.weave(context);
        }
        if (getNext() != null) {
            getNext().weave(context);
            setNextStateName(getNext().getFirstStateName());
        }
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
