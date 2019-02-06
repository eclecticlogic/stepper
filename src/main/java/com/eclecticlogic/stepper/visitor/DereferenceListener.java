package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseListener;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

public class DereferenceListener extends StepperBaseListener {

    private final Set<String> references = Sets.newHashSet();


    List<String> getReferences() {
        return Lists.newArrayList(references);
    }


    @Override
    public void enterDereference(StepperParser.DereferenceContext ctx) {
        references.add(ctx.ID(0).getText());
    }
}
