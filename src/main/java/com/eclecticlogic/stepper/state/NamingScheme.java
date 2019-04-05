package com.eclecticlogic.stepper.state;

public class NamingScheme {

    private String prefix;
    private int index;
    private int varIndex;


    public NamingScheme(String prefix) {
        this.prefix = prefix;
    }


    public String getNextName() {
        return String.format("%s%03d", prefix, index++);
    }


    public String getVar() {
        return String.format("%svar__%03d", prefix, varIndex++);
    }
}
