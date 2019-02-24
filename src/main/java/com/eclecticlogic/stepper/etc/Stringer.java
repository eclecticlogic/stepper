package com.eclecticlogic.stepper.etc;

import com.eclecticlogic.stepper.antlr.StepperParser;
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


    public static String enhance(StepperParser.ComplexAssignContext cactx, String expression, String variable) {
        if (cactx.ASSIGN() != null) {
            return expression;
        } else {
            String symbol;
            if (cactx.PLUSASSIGN() != null) {
                symbol = "+";
            } else if (cactx.MINUSASSIGN() != null) {
                symbol = "-";
            } else if (cactx.MULTASSIGN() != null) {
                symbol = "*";
            } else {
                symbol = "/";
            }
            return variable + " " + symbol + " " + expression;
        }
    }
}
