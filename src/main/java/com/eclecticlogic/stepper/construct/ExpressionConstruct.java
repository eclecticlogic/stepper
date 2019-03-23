package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.etc.WeaveContext;
import com.eclecticlogic.stepper.state.Task;

import java.util.List;

public class ExpressionConstruct extends StateConstruct<Task> {

    private String expression;
    private String variable;
    private List<String> symbols;


    public ExpressionConstruct(String label) {
        super(new Task(label));
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


    @Override
    public void weave(WeaveContext context) {
        constructLambda(context, getState(), expression, symbols);
        Task task = getState();
        task.setResultPath("$." + variable);

        task.setupLambdaHelper();
        super.weave(context);
    }
}
