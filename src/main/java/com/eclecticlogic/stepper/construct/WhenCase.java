package com.eclecticlogic.stepper.construct;

import java.util.List;

public class WhenCase {

    private List<String> symbols;
    private String expression;
    private Construct block;


    public WhenCase(List<String> symbols, String expression, Construct block) {
        this.symbols = symbols;
        this.expression = expression;
        this.block = block;
    }


    public List<String> getSymbols() {
        return symbols;
    }


    public String getExpression() {
        return expression;
    }


    public Construct getBlock() {
        return block;
    }
}
