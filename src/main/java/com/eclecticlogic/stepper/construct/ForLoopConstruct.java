package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.etc.WeaveContext;
import com.eclecticlogic.stepper.state.Choice;
import com.eclecticlogic.stepper.state.State;
import com.eclecticlogic.stepper.state.Task;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

public class ForLoopConstruct extends Construct {

    private String iterableVariable;

    private String initialExpression;
    private Set<String> initialExpressionSymbols;

    private String endingExpression;
    private Set<String> endingExpressionSymbols;

    private String stepExpression;
    private Set<String> stepExpressionSymbols;

    private Construct block;

    private final Task initializingLambda;
    private final Task endingLambda;
    private final String choiceVar;
    private final Choice choice;
    private final Task incrementingLambda;


    public ForLoopConstruct(String label) {
        initializingLambda = new Task(label);
        endingLambda = new Task();
        choiceVar = getNextDynamicVariable();
        choice = new Choice(choiceVar);
        incrementingLambda = new Task();
    }


    public void setIterableVariable(String iterableVariable) {
        this.iterableVariable = iterableVariable;
    }


    public void setInitialExpression(String initialExpression) {
        this.initialExpression = initialExpression;
    }


    public void setInitialExpressionSymbols(Set<String> initialExpressionSymbols) {
        this.initialExpressionSymbols = initialExpressionSymbols;
    }


    public void setEndingExpression(String endingExpression) {
        this.endingExpression = endingExpression;
    }


    public void setEndingExpressionSymbols(Set<String> endingExpressionSymbols) {
        this.endingExpressionSymbols = endingExpressionSymbols;
    }


    public void setStepExpression(String stepExpression) {
        this.stepExpression = stepExpression;
    }


    public void setStepExpressionSymbols(Set<String> stepExpressionSymbols) {
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
            stepExpressionSymbols = Sets.newHashSet();
        }
        stepExpressionSymbols.add(iterableVariable);

        constructLambda(context, incrementingLambda, iterableVariable + " + (" + stepExpression + ")",
                stepExpressionSymbols);
        incrementingLambda.setupLambdaHelper();
        incrementingLambda.setResultPath("$." + iterableVariable);
        incrementingLambda.setNextState(endingLambda.getName());
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

        block.weave(context);

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
