package com.eclecticlogic.stepper.state;

public class NamingScheme {

    private String prefix;
    private int index;


    public NamingScheme(String prefix) {
        this.prefix = prefix;
    }


    public String getNextName() {
        return String.format("%s%03d", prefix, index++);
    }
}
