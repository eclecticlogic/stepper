package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.etc.WeaveContext;
import com.eclecticlogic.stepper.state.State;
import com.google.common.collect.Lists;

import java.util.List;

public class WhenConstruct extends Construct {
    private final List<WhenCase> cases = Lists.newArrayList();
    private Construct elseBlock;


    public WhenCase addCase(String label) {
        WhenCase wcase = new WhenCase(label, getNextDynamicVariable());
        cases.add(wcase);
        return wcase;
    }


    public void setElseBlock(Construct elseBlock) {
        this.elseBlock = elseBlock;
    }


    @Override
    protected String getFirstStateName() {
        return cases.get(0).getTask().getName();
    }


    void setupBlocks(WeaveContext context) {
        for (WhenCase aCase : cases) {
            constructLambda(context, aCase.getTask(), aCase.getExpression(), aCase.getSymbols());

            aCase.getTask().setupLambdaHelper();
            aCase.getTask().setResultPath("$." + aCase.getChoiceVariable());
            aCase.getTask().setNextState(aCase.getChoice().getName());

            aCase.getChoice().setIfNextState(aCase.getBlock().getFirstStateName());
        }
        // now wire up all the choice failure parts.
        for (int i = 0; i < cases.size() - 1; i++) {
            cases.get(i).getChoice().setElseNextState(cases.get(i + 1).getTask().getName());
        }
        if (elseBlock != null) {
            cases.get(cases.size() - 1).getChoice().setElseNextState(elseBlock.getFirstStateName());
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
            cases.get(cases.size() - 1).getChoice().setElseNextState(name);
        }
    }


    @Override
    public List<State> getStates() {
        List<State> result = Lists.newArrayList();
        for (WhenCase aCase : cases) {
            result.add(aCase.getTask());
            result.add(aCase.getChoice());
            result.addAll(aCase.getBlock().getStates());
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
