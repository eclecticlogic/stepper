package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.etc.LambdaBranch;
import com.eclecticlogic.stepper.etc.WeaveContext;
import com.eclecticlogic.stepper.state.Choice;
import com.eclecticlogic.stepper.state.State;
import com.eclecticlogic.stepper.state.Task;
import com.google.common.collect.Lists;

import java.util.List;

import static com.eclecticlogic.stepper.etc.Constants.COMMAND_VAR;

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
    final Choice choice = new Choice(choiceVar);
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
        final LambdaBranch branch = new LambdaBranch();
        branch.setCommandName(initializingLambda.getName());
        branch.setInputs(initialExpressionSymbols);
        branch.setOutputExpression(initialExpression);
        context.getLambdaHelper().getBranches().add(branch);

        initializingLambda.captureAttribute("Parameters");
        initializingLambda.handleObject(() -> {
            initializingLambda.captureAttribute(COMMAND_VAR);
            initializingLambda.setProperty(initializingLambda.getName());

            initialExpressionSymbols.forEach(it -> {
                initializingLambda.captureAttribute(it + ".$");
                initializingLambda.setProperty("$." + it);
            });
        });

        initializingLambda.captureAttribute("Resource");
        initializingLambda.setProperty("@@@lambda_helper_arn@@@");
        initializingLambda.setResultPath("$." + iterableVariable);
        initializingLambda.setNextState(endingLambda.getName());
    }


    void setupEnding(WeaveContext context) {
        endingExpressionSymbols.add(iterableVariable);

        final LambdaBranch branch = new LambdaBranch();
        branch.setCommandName(endingLambda.getName());
        branch.setInputs(endingExpressionSymbols);
        branch.setOutputExpression(iterableVariable + " <= " + endingExpression);
        context.getLambdaHelper().getBranches().add(branch);

        endingLambda.captureAttribute("Parameters");
        endingLambda.handleObject(() -> {
            endingLambda.captureAttribute(COMMAND_VAR);
            endingLambda.setProperty(endingLambda.getName());

            endingExpressionSymbols.forEach(it -> {
                endingLambda.captureAttribute(it + ".$");
                endingLambda.setProperty("$." + it);
            });
        });

        endingLambda.captureAttribute("Resource");
        endingLambda.setProperty("@@@lambda_helper_arn@@@");
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

        final LambdaBranch branch = new LambdaBranch();
        branch.setCommandName(incrementingLambda.getName());
        branch.setInputs(stepExpressionSymbols);
        branch.setOutputExpression(iterableVariable + " + (" + stepExpression + ")");
        context.getLambdaHelper().getBranches().add(branch);

        incrementingLambda.captureAttribute("Parameters");
        incrementingLambda.handleObject(() -> {
            incrementingLambda.captureAttribute(COMMAND_VAR);
            incrementingLambda.setProperty(incrementingLambda.getName());

            stepExpressionSymbols.forEach(it -> {
                incrementingLambda.captureAttribute(it + ".$");
                incrementingLambda.setProperty("$." + it);
            });
        });

        incrementingLambda.captureAttribute("Resource");
        incrementingLambda.setProperty("@@@lambda_helper_arn@@@");
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
