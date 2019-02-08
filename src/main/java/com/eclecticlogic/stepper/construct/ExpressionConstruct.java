package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.etc.LambdaBranch;
import com.eclecticlogic.stepper.etc.WeaveContext;
import com.eclecticlogic.stepper.state.Task;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class ExpressionConstruct extends StateConstruct<Task> {

    private String expression;
    private String variable;
    private List<String> symbols;


    public ExpressionConstruct() {
        super(new Task());
    }


    public void setExpression(String expression) {
        this.expression = expression;
    }


    public void setVariable(String variable) {
        this.variable = variable;
    }


    public void setSymbols(List<String> symbols) {
        this.symbols = symbols;
    }


    public void setup() {
        Task task = getState();
        task.setResultPath("$." + variable);

        task.captureAttribute("Parameters");
        task.handleObject(() -> {
            task.captureAttribute("cmd__sm");
            task.setProperty(task.getName());

            symbols.forEach(it -> {
                task.captureAttribute(it + ".$");
                task.setProperty("$." + it);
            });
        });

        task.captureAttribute("Resource");
        task.setProperty("@@@lambda_helper_arn@@@");
    }


    @Override
    public void weave(WeaveContext context) {
        final LambdaBranch branch = new LambdaBranch();
        branch.setCommandName(getState().getName());
        branch.setInputs(symbols);
        branch.setOutputExpression(expression);
        context.getLambdaHelper().getBranches().add(branch);

        super.weave(context);
    }
}
