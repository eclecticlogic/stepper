package com.eclecticlogic.stepper.construct;

import java.util.Set;

public class WhenCase {

    private Set<String> symbols;
    private String expression;
    private Construct block;


    public WhenCase(Set<String> symbols, String expression, Construct block) {
        this.symbols = symbols;
        this.expression = expression;
        this.block = block;
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
