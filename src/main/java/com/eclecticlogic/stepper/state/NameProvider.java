package com.eclecticlogic.stepper.state;

import java.util.Stack;

public class NameProvider {

    private static ThreadLocal<Stack<NamingScheme>> namingSchemeStack = new ThreadLocal<>();


    public static void usingName(String name, Runnable code) {
        if (namingSchemeStack.get() == null) {
            namingSchemeStack.set(new Stack<>());
        }
        namingSchemeStack.get().push(new NamingScheme(name));
        code.run();
        namingSchemeStack.get().pop();
    }


    public static String getName() {
        return namingSchemeStack.get().peek().getNextName();
    }


    public static String getVar() {
        return namingSchemeStack.get().peek().getVar();
    }
}
