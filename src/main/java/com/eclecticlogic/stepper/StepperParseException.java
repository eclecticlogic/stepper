package com.eclecticlogic.stepper;

import org.antlr.v4.runtime.RecognitionException;

public class StepperParseException extends RuntimeException {

    public StepperParseException(Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        super("Line: " + line + ", char: " + charPositionInLine + ", " + msg, e);

    }
}
