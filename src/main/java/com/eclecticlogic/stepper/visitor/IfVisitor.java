package com.eclecticlogic.stepper.visitor;

import com.eclecticlogic.stepper.antlr.StepperBaseVisitor;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.construct.IfConstruct;

public class IfVisitor extends StepperBaseVisitor<IfConstruct> {

    @Override
    public IfConstruct visitIfStatement(StepperParser.IfStatementContext ctx) {
        IfConstruct construct = new IfConstruct();
        construct.setCondition(ctx.ifCondition.getText());

        DereferencingVisitor defVisitor = new DereferencingVisitor();
        construct.setConditionDeferences(defVisitor.visit(ctx.ifCondition));

        StatementBlockVisitor ifBlockVisitor = new StatementBlockVisitor();
        construct.setFirstIf(ifBlockVisitor.visit(ctx.ifBlock));

        if (ctx.ELSE() != null) {
            StatementBlockVisitor elseBlockVisitor = new StatementBlockVisitor();
            construct.setFirstElse(elseBlockVisitor.visit(ctx.elseBlock));
        }

        return construct;
    }


}
