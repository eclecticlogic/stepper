package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.etc.LambdaBranch;
import com.eclecticlogic.stepper.etc.WeaveContext;
import com.eclecticlogic.stepper.state.NameProvider;
import com.eclecticlogic.stepper.state.NamingScheme;
import com.eclecticlogic.stepper.state.State;
import com.eclecticlogic.stepper.state.Task;
import com.google.common.collect.Lists;

import java.util.List;

import static com.eclecticlogic.stepper.etc.Constants.COMMAND_VAR;

public abstract class Construct {

    private static int dynamicVariableCounter;
    private Construct next;


    /**
     * @return Should return all the states in the given construct and states in the construct that follows this.
     */
    public abstract List<State> getStates();


    Construct getNext() {
        return next;
    }

    public void setNext(Construct value) {
        next = value;
    }


    String getNextDynamicVariable() {
        return NameProvider.getVar();
    }


    List<State> getStates(State... first) {
        List<State> result = Lists.newArrayList(first);
        if (next != null) {
            result.addAll(next.getStates());
        }
        return result;
    }


    protected abstract String getFirstStateName();


    protected abstract void setNextStateName(String name);


    /**
     * @param context Implementors are expected to wire up the ASL json nodes and then call weave on any
     *                constructs they directly manage and call weave on the next construct (getNext() != null).
     */
    public abstract void weave(WeaveContext context);


    /**
     * @param input first construct in a chain
     * @return The last construct in a chain of constructs (useful in finding last construct in a block of constructs).
     */
    Construct getLastInChain(Construct input) {
        Construct next = input;
        while (next.getNext() != null) {
            next = next.getNext();
        }
        return next;
    }


    LambdaBranch constructLambda(WeaveContext context, Task lambda, String expression, List<String> symbols) {
        final LambdaBranch branch = new LambdaBranch();
        branch.setCommandName(lambda.getName());
        branch.setInputs(symbols);
        branch.setOutputExpression(expression);
        context.getLambdaHelper().getBranches().add(branch);

        lambda.captureAttribute("Parameters");
        lambda.handleObject(() -> {
            lambda.captureAttribute(COMMAND_VAR);
            lambda.setProperty(lambda.getName());

            symbols.forEach(it -> {
                lambda.captureAttribute(it + ".$");
                lambda.setProperty("$." + it);
            });
        });
        return branch;
    }
}
