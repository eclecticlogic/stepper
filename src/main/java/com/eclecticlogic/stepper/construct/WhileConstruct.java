package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.etc.WeaveContext;
import com.eclecticlogic.stepper.state.Choice;
import com.eclecticlogic.stepper.state.State;
import com.eclecticlogic.stepper.state.Task;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Set;

public class WhileConstruct extends Construct {
    private String expression;
    private Set<String> symbols;
    private Construct block;

    private final Task conditionLambda;
    private final String choiceVar;
    final Choice choice;


    public WhileConstruct(String label) {
        conditionLambda = new Task(label);
        choiceVar = getNextDynamicVariable();
        choice = new Choice(choiceVar);
    }


    public void setExpression(String expression) {
        this.expression = expression;
    }


    public void setSymbols(Set<String> symbols) {
        this.symbols = symbols;
    }


    public void setBlock(Construct block) {
        this.block = block;
    }


    void setupConditionLambda(WeaveContext context) {
        constructLambda(context, conditionLambda, expression, symbols);

        conditionLambda.setupLambdaHelper();
        conditionLambda.setResultPath("$." + choiceVar);
        conditionLambda.setNextState(choice.getName());
    }


    void setupChoice() {
        choice.setIfNextState(block.getFirstStateName());
        getLastInChain(block).setNextStateName(conditionLambda.getName());
    }


    @Override
    protected String getFirstStateName() {
        return conditionLambda.getName();
    }


    @Override
    protected void setNextStateName(String name) {
        choice.setElseNextState(name);
    }


    @Override
    public void weave(WeaveContext context) {
        setupConditionLambda(context);
        setupChoice();
        block.weave(context);
        if (getNext() != null) {
            getNext().weave(context);
            setNextStateName(getNext().getFirstStateName());
        }
    }


    @Override
    public List<State> getStates() {
        List<State> result = Lists.newArrayList(conditionLambda, choice);
        result.addAll(block.getStates());
        if (getNext() != null) {
            result.addAll(getNext().getStates());
        }
        return result;
    }
}
