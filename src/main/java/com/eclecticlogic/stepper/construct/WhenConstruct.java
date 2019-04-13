package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.etc.WeaveContext;
import com.eclecticlogic.stepper.state.Choice;
import com.eclecticlogic.stepper.state.State;
import com.eclecticlogic.stepper.state.Task;
import com.google.common.collect.Lists;

import java.util.List;

public class WhenConstruct extends Construct {
    private List<WhenCase> cases;
    private Construct elseBlock;

    private final List<Task> whenTasks = Lists.newArrayList();
    private final List<String> choiceVariables = Lists.newArrayList();
    private final List<Choice> choices = Lists.newArrayList();


    public void setCases(List<WhenCase> cases) {
        this.cases = cases;
    }


    public void setElseBlock(Construct elseBlock) {
        this.elseBlock = elseBlock;
    }


    @Override
    protected String getFirstStateName() {
        return whenTasks.get(0).getName();
    }


    void setupBlocks(WeaveContext context) {
        for (WhenCase aCase : cases) {
            Task task = new Task(aCase.getLabel());
            String var = getNextDynamicVariable();
            Choice choice = new Choice(var);

            constructLambda(context, task, aCase.getExpression(), aCase.getSymbols());

            task.setupLambdaHelper();
            task.setResultPath("$." + var);
            task.setNextState(choice.getName());

            choice.setIfNextState(aCase.getBlock().getFirstStateName());

            whenTasks.add(task);
            choiceVariables.add(var);
            choices.add(choice);
        }
        // now wire up all the choice failure parts.
        for (int i = 0; i < choices.size() - 1; i++) {
            choices.get(i).setElseNextState(whenTasks.get(i + 1).getName());
        }
        if (elseBlock != null) {
            choices.get(choices.size() - 1).setElseNextState(elseBlock.getFirstStateName());
        }
    }


    @Override
    public void weave(WeaveContext context) {
        setupBlocks(context);

        cases.forEach(c -> c.getBlock().weave(context));
        if (elseBlock != null) {
            elseBlock.weave(context);
        }

        if (getNext() != null) {
            getNext().weave(context);
            setNextStateName(getNext().getFirstStateName());
        }
    }


    @Override
    protected void setNextStateName(String name) {
        cases.forEach(c -> getLastInChain(c.getBlock()).setNextStateName(name));
        if (elseBlock != null) {
            getLastInChain(elseBlock).setNextStateName(name);
        } else {
            choices.get(choices.size() - 1).setElseNextState(name);
        }
    }


    @Override
    public List<State> getStates() {
        List<State> result = Lists.newArrayList();
        for (int i = 0; i < whenTasks.size(); i++) {
            result.add(whenTasks.get(i));
            result.add(choices.get(i));
            result.addAll(cases.get(i).getBlock().getStates());
        }
        if (elseBlock != null) {
            result.addAll(elseBlock.getStates());
        }
        if (getNext() != null) {
            result.addAll(getNext().getStates());
        }
        return result;
    }


}
