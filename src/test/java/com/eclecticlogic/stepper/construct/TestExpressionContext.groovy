package com.eclecticlogic.stepper.construct

import com.eclecticlogic.stepper.etc.LambdaBranch
import com.eclecticlogic.stepper.etc.WeaveContext
import com.eclecticlogic.stepper.state.Task
import spock.lang.Specification

class TestExpressionContext extends Specification {

    def "setup creates proper task"() {
        given:
        ExpressionConstruct ec = new ExpressionConstruct()
        Task t = ec.state
        WeaveContext ctx = new WeaveContext()

        when:
        ec.expression = 'a * b + c'
        ec.variable = 'answer'
        ec.symbols = ['a', 'b', 'c']
        ec.weave(ctx)

        then:
        t.jsonRepresentation.replaceAll('[ \t\n]', '') == """
        "${t.name}": {
           "Type": "Task",
           "ResultPath": "\$.answer",
           "Parameters": {
             "cmd__sm": "${t.name}",
             "a.\$": "\$.a",
             "b.\$": "\$.b",
             "c.\$": "\$.c"
           },
           "Resource": "@@@lambda_helper_arn@@@"
        }
        """.replaceAll('[ \t\n]', '')
    }


    def "lambda setup"() {
        given:
        ExpressionConstruct ec = new ExpressionConstruct()
        WeaveContext ctx = new WeaveContext()

        when:
        ec.expression = 'a * b + c'
        ec.variable = 'answer'
        ec.symbols = ['a', 'b', 'c']
        ec.weave(ctx)

        then:
        LambdaBranch br = ctx.lambdaHelper.branches[0]
        with(br) {
            it.commandName == ec.state.name
            it.inputs == ['a', 'b', 'c']
            it.outputExpression == 'a * b + c'
        }


    }
}
