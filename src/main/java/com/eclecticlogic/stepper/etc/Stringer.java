package com.eclecticlogic.stepper.etc;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

public class Stringer {

    public static String from(Token token) {
        return token == null ? null : token.getText();
    }


    public static String from(TerminalNode token) {
        return token == null ? null : token.getText();
    }


    public static String strip(String input) {
        return input == null ? null : input.substring(1).substring(0, input.length() - 2);
    }
}
