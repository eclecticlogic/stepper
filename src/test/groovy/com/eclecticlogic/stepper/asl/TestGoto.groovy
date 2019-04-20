package com.eclecticlogic.stepper.asl

import com.jayway.jsonpath.ReadContext


class TestGoto extends AbstractStateMachineTester {

    def "test goto1"() {
        given:
        TestOutput output = runWithLambda('goto.stg', 'goto1')
        ReadContext ctx = output.ctx

        when:
        Closure v = { name, param ->
            return ctx.read('$.States.' + name + '.' + param)
        }

        then:
        Object[] data = ctx.read('$.States.*')
        data.length == 8

        ctx.read('$.StartAt') == 'Goto000'

        with('Goto000') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 5
            v(it, 'ResultPath') == '$.a'
            v(it, 'Next') == 'Goto001'
        }

        with('Goto001') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'Goto001'
            v(it, "Parameters.['a.\$']") == '$.a'
            v(it, 'ResultPath') == '$.Gotovar__000'
            v(it, 'Next') == 'Goto002'
        }
        output.lambda.contains('const response = a==5;')

        with('Goto002') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.Gotovar__000'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'Goto003'

            v(it, 'Choices[1].Variable') == '$.Gotovar__000'
            v(it, 'Choices[1].BooleanEquals') == false
            v(it, 'Choices[1].Next') == 'Goto004'
        }

        with('Goto003') {
            v(it, 'Type') == 'Pass'
            v(it, 'Next') == 'a'
        }

        with('Goto004') {
            v(it, 'Type') == 'Fail'
        }

        with('a') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 7
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'Goto005'
        }

        with('Goto005') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 5
            v(it, 'ResultPath') == '$.d'
            v(it, 'Next') == 'Goto.Success'
        }

        v("['Goto.Success']", 'Type') == 'Succeed'
    }


    def "test goto2"() {
        given:
        TestOutput output = runWithLambda('goto.stg', 'goto2')
        ReadContext ctx = output.ctx

        when:
        Closure v = { name, param ->
            return ctx.read('$.States.' + name + '.' + param)
        }

        then:
        Object[] data = ctx.read('$.States.*')
        data.length == 9

        ctx.read('$.StartAt') == 'Goto000'

        with('Goto000') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 5
            v(it, 'ResultPath') == '$.a'
            v(it, 'Next') == 'Goto001'
        }

        with('Goto001') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'Goto001'
            v(it, "Parameters.['a.\$']") == '$.a'
            v(it, 'ResultPath') == '$.Gotovar__000'
            v(it, 'Next') == 'Goto002'
        }
        output.lambda.contains('const response = a==5;')

        with('Goto002') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.Gotovar__000'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'Goto003'

            v(it, 'Choices[1].Variable') == '$.Gotovar__000'
            v(it, 'Choices[1].BooleanEquals') == false
            v(it, 'Choices[1].Next') == 'Goto004'
        }

        with('Goto003') {
            v(it, 'Type') == 'Pass'
            v(it, 'Next') == 'a'
        }

        with('Goto004') {
            v(it, 'Type') == 'Pass'
            v(it, 'Next') == 'b'
        }

        with('a') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 7
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'Goto005'
        }

        with('Goto005') {
            v(it, 'Type') == 'Pass'
            v(it, 'Next') == 'Goto.Success'
        }

        with('b') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 5
            v(it, 'ResultPath') == '$.d'
            v(it, 'Next') == 'Goto.Success'
        }

        v("['Goto.Success']", 'Type') == 'Succeed'
    }

}