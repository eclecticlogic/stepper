package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.state.Task;

import java.util.List;

public class ExpressionConstruct extends StateConstruct<Task> {

    private String expression; // TODO
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
        task.handleObject(() ->
                symbols.forEach(it -> {
                    task.captureAttribute(it + ".$");
                    task.setProperty("$." + it);
                }));

        task.captureAttribute("Resource");
        task.setProperty("lambda arn for condition");
    }
}
