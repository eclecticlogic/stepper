package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.etc.LambdaBranch;
import com.eclecticlogic.stepper.etc.WeaveContext;
import com.eclecticlogic.stepper.state.Choice;
import com.eclecticlogic.stepper.state.Pass;
import com.eclecticlogic.stepper.state.State;
import com.eclecticlogic.stepper.state.Task;
import com.google.common.collect.Lists;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.util.List;

import static com.eclecticlogic.stepper.etc.Constants.COMMAND_VAR;

public class ForIterationConstruct extends Construct {

    private String iterableExpression;
    private String iterableVariable;
    private List<String> symbols;
    private Construct block;

    private final Pass indexInitializer = new Pass();
    private final String index = getNextDynamicVariable();
    private final Task iteratingLambda = new Task();
    final Choice choice = new Choice(index + ".exists");
    private final Pass iterVariableSetter = new Pass();


    public void setIterableExpression(String iterableExpression) {
        this.iterableExpression = iterableExpression;
    }


    public void setIterableVariable(String iterableVariable) {
        this.iterableVariable = iterableVariable;
    }


    public void setSymbols(List<String> symbols) {
        this.symbols = symbols;
    }


    public void setBlock(Construct block) {
        this.block = block;
    }


    void setupInitializer() {
        indexInitializer.captureAttribute("Result");
        indexInitializer.handleObject(() -> {
            indexInitializer.captureAttribute("idx");
            indexInitializer.setProperty(-1);
        });

        indexInitializer.setResultPath("$." + index);
        indexInitializer.setNextState(iteratingLambda.getName());
    }


    void setupIteratingLambda(WeaveContext context) {
        final LambdaBranch branch = new LambdaBranch();
        branch.setCommandName(iteratingLambda.getName());
        branch.setInputs(symbols);
        STGroup group = new STGroupFile("stepper/template/lambda.stg");
        ST st = group.getInstanceOf("forIterationBody");
        st.add("expr", iterableExpression);
        st.add("index", index);
        branch.setComputation(st.render());
        branch.setOutputExpression("r");
        context.getLambdaHelper().getBranches().add(branch);

        symbols.add(index);
        iteratingLambda.captureAttribute("Parameters");
        iteratingLambda.handleObject(() -> {
            iteratingLambda.captureAttribute(COMMAND_VAR);
            iteratingLambda.setProperty(iteratingLambda.getName());

            symbols.forEach(it -> {
                iteratingLambda.captureAttribute(it + ".$");
                iteratingLambda.setProperty("$." + it);
            });
        });

        iteratingLambda.captureAttribute("Resource");
        iteratingLambda.setProperty("@@@lambda_helper_arn@@@");
        iteratingLambda.setResultPath("$." + index);
        iteratingLambda.setNextState(choice.getName());
    }


    void setupChoice() {
        choice.setIfNextState(iterVariableSetter.getName());
        iterVariableSetter.captureAttribute("InputPath");
        iterVariableSetter.setProperty("$." + index + ".var");
        iterVariableSetter.setResultPath("$." + iterableVariable);
        iterVariableSetter.setNextState(block.getFirstStateName());
        getLastInChain(block).setNextStateName(iteratingLambda.getName());
    }


    @Override
    protected String getFirstStateName() {
        return indexInitializer.getName();
    }


    @Override
    protected void setNextStateName(String name) {
        choice.setElseNextState(name);
    }


    @Override
    public void weave(WeaveContext context) {
        setupInitializer();
        setupIteratingLambda(context);
        setupChoice();
        block.weave(context);
        if (getNext() != null) {
            getNext().weave(context);
            setNextStateName(getNext().getFirstStateName());
        }
    }


    @Override
    public List<State> getStates() {
        List<State> result = Lists.newArrayList(indexInitializer, iteratingLambda, choice, iterVariableSetter);
        result.addAll(block.getStates());
        if (getNext() != null) {
            result.addAll(getNext().getStates());
        }
        return result;
    }
}
