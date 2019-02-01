package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseListener;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.google.common.collect.Lists;

import java.util.List;

public class DereferenceListener extends StepperBaseListener {

    private final List<String> references = Lists.newArrayList();


    List<String> getReferences() {
        return references;
    }


    @Override
    public void enterDereference(StepperParser.DereferenceContext ctx) {
        references.add(ctx.ID(0).getText());
    }
}
