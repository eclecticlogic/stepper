package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.etc.WeaveContext;
import com.eclecticlogic.stepper.state.Choice;
import com.eclecticlogic.stepper.state.State;
import com.eclecticlogic.stepper.state.Task;
import com.google.common.collect.Lists;

import java.util.List;

public class ForLoopConstruct extends Construct {

    private String iterableVariable;

    private String initialExpression;
    private List<String> initialExpressionSymbols;

    private String endingExpression;
    private List<String> endingExpressionSymbols;

    private String stepExpression;
    private List<String> stepExpressionSymbols;

    private Construct block;

    private final Task initializingLambda = new Task();
    private final Task endingLambda = new Task();
    private final String choiceVar = getNextDynamicVariable();
    private final Choice choice = new Choice(choiceVar);
    private final Task incrementingLambda = new Task();


    public void setIterableVariable(String iterableVariable) {
        this.iterableVariable = iterableVariable;
    }


    public void setInitialExpression(String initialExpression) {
        this.initialExpression = initialExpression;
    }


    public void setInitialExpressionSymbols(List<String> initialExpressionSymbols) {
        this.initialExpressionSymbols = initialExpressionSymbols;
    }


    public void setEndingExpression(String endingExpression) {
        this.endingExpression = endingExpression;
    }


    public void setEndingExpressionSymbols(List<String> endingExpressionSymbols) {
        this.endingExpressionSymbols = endingExpressionSymbols;
    }


    public void setStepExpression(String stepExpression) {
        this.stepExpression = stepExpression;
    }


    public void setStepExpressionSymbols(List<String> stepExpressionSymbols) {
        this.stepExpressionSymbols = stepExpressionSymbols;
    }


    public void setBlock(Construct block) {
        this.block = block;
    }


    void setupInitializer(WeaveContext context) {
        constructLambda(context, initializingLambda, initialExpression, initialExpressionSymbols);

        initializingLambda.setupLambdaHelper();
        initializingLambda.setResultPath("$." + iterableVariable);
        initializingLambda.setNextState(endingLambda.getName());
    }


    void setupEnding(WeaveContext context) {
        endingExpressionSymbols.add(iterableVariable);
        constructLambda(context, endingLambda, iterableVariable + " <= " + endingExpression, endingExpressionSymbols);

        endingLambda.setupLambdaHelper();
        endingLambda.setResultPath("$." + choiceVar);
        endingLambda.setNextState(choice.getName());
    }


    void setupChoice() {
        choice.setIfNextState(block.getFirstStateName());
        getLastInChain(block).setNextStateName(incrementingLambda.getName());
    }


    void setupIncrementer(WeaveContext context) {
        if (stepExpression == null) {
            // Use default
            stepExpression = "1";
            stepExpressionSymbols = Lists.newArrayList();
        }
        stepExpressionSymbols.add(iterableVariable);

        constructLambda(context, incrementingLambda, iterableVariable + " + (" + stepExpression + ")",
                stepExpressionSymbols);
        incrementingLambda.setupLambdaHelper();
        incrementingLambda.setResultPath("$." + iterableVariable);
        incrementingLambda.setNextState(choice.getName());
    }


    @Override
    protected String getFirstStateName() {
        return initializingLambda.getName();
    }


    @Override
    protected void setNextStateName(String name) {
        choice.setElseNextState(name);
    }


    @Override
    public void weave(WeaveContext context) {
        setupInitializer(context);
        setupEnding(context);
        setupChoice();
        setupIncrementer(context);

        if (getNext() != null) {
            getNext().weave(context);
            setNextStateName(getNext().getFirstStateName());
        }
    }


    @Override
    public List<State> getStates() {
        List<State> result = Lists.newArrayList(initializingLambda, endingLambda, choice);
        result.addAll(block.getStates());
        result.add(incrementingLambda);

        if (getNext() != null) {
            result.addAll(getNext().getStates());
        }
        return result;
    }
}
