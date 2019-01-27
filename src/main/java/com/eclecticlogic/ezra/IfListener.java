package com.eclecticlogic.ezra;

import com.eclecticlogic.ezra.antlr.EzraParser;
import com.eclecticlogic.ezra.machine.construct.IfConstruct;

public class IfListener extends TaskListener {


    @Override
    public void enterIfCondition(EzraParser.IfConditionContext ctx) {
        IfConstruct ifConstruct = new IfConstruct();
        push(ifConstruct);
        ifConstruct.setCondition(ctx.expr().getText());
        registerDeferenceListener(ifConstruct);
    }


    @Override
    public void enterKeywordElse(EzraParser.KeywordElseContext ctx) {
        unregisterDeferenceListener((IfConstruct)stepConstruct);
    }

}
