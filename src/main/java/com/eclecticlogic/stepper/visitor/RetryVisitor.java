package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.etc.Etc;
import com.eclecticlogic.stepper.state.AttributableState;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.function.Function;

import static com.eclecticlogic.stepper.etc.Etc.strip;

public class RetryVisitor<T extends AttributableState> extends StepperBaseVisitor<T> {

    private T state;
    private final Function<String, T> stateCreator;


    public RetryVisitor(Function<String, T> stateCreator) {
        this.stateCreator = stateCreator;
    }


    @Override
    public T visitRetries(StepperParser.RetriesContext ctx) {
        String name = ctx.label() == null ? null : strip(ctx.label().STRING().getText());
        state = stateCreator.apply(name);

        if (!ctx.retry().isEmpty()) {
            setupRetries(ctx.retry());
        }
        return state;
    }


    void setupRetries(List<StepperParser.RetryContext> ctx) {
        state.captureAttribute("Retry");
        state.handleArray(() -> {
            for (StepperParser.RetryContext rc : ctx) {
                state.handleObject(() -> {
                    state.captureAttribute("ErrorEquals");
                    state.handleArray(() -> rc.STRING().stream()//
                            .map(TerminalNode::getText) //
                            .map(Etc::strip) //
                            .forEach(state::setProperty));
                    JsonObjectVisitor visitor = new JsonObjectVisitor(state);
                    visitor.visit(rc.jsonObject());
                });
            }
        });
    }
}
