package com.eclecticlogic.stepper.construct;

import com.eclecticlogic.stepper.state.Choice;
import com.eclecticlogic.stepper.state.Task;

import java.util.Set;

public class WhenCase {

    private Set<String> symbols;
    private String expression;
    private Construct block;

    private final Task task;
    private final String choiceVariable;
    private final Choice choice;


    public WhenCase(String label, String variable) {
        task = new Task(label);
        choiceVariable = variable;
        choice = new Choice(choiceVariable);
    }


    public Task getTask() {
        return task;
    }


    public String getChoiceVariable() {
        return choiceVariable;
    }


    public Choice getChoice() {
        return choice;
    }


    public Set<String> getSymbols() {
        return symbols;
    }


    public void setSymbols(Set<String> symbols) {
        this.symbols = symbols;
    }


    public String getExpression() {
        return expression;
    }


    public void setExpression(String expression) {
        this.expression = expression;
    }


    public Construct getBlock() {
        return block;
    }


    public void setBlock(Construct block) {
        this.block = block;
    }
}
