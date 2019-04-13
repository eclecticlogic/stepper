package com.eclecticlogic.stepper.construct;

import java.util.Set;

public class WhenCase {

    private String label;
    private Set<String> symbols;
    private String expression;
    private Construct block;


    public WhenCase(String label, Set<String> symbols, String expression, Construct block) {
        this.label = label;
        this.symbols = symbols;
        this.expression = expression;
        this.block = block;
    }


    public String getLabel() {
        return label;
    }


    public Set<String> getSymbols() {
        return symbols;
    }


    public String getExpression() {
        return expression;
    }


    public Construct getBlock() {
        return block;
    }
}
