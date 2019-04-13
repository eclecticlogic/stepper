package com.eclecticlogic.stepper.asl

import com.jayway.jsonpath.ReadContext


class TestWhile extends AbstractStateMachineTester {

    def "test simple while"() {
        given:
        TestOutput output = runWithLambda('while.stg', 'simple')
        ReadContext ctx = output.ctx

        when:
        Closure v = { name, param ->
            return ctx.read('$.States.' + name + '.' + param)
        }

        then:
        Object[] data = ctx.read('$.States.*')
        data.length == 7

        ctx.read('$.StartAt') == 'Simple000'

        with('Simple000') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 5
            v(it, 'ResultPath') == '$.a'
            v(it, 'Next') == 'abc'
        }

        with('abc') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'abc'
            v(it, "Parameters.['a.\$']") == '$.a'
            v(it, 'ResultPath') == '$.Simplevar__000'
            v(it, 'Next') == 'Simple001'
        }
        output.lambda.contains('const response = a<15;')

        with('Simple001') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.Simplevar__000'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'Simple002'

            v(it, 'Choices[1].Variable') == '$.Simplevar__000'
            v(it, 'Choices[1].BooleanEquals') == false
            v(it, 'Choices[1].Next') == 'Simple004'
        }

        with('Simple002') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'Simple002'
            v(it, "Parameters.['a.\$']") == '$.a'
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'Simple003'
        }
        output.lambda.contains('const response = a+1;')

        with('Simple003') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'Simple003'
            v(it, "Parameters.['a.\$']") == '$.a'
            v(it, 'ResultPath') == '$.a'
            v(it, 'Next') == 'abc'
        }
        output.lambda.contains('const response = a+1;')

        with('Simple004') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'Simple004'
            v(it, "Parameters.['a.\$']") == '$.a'
            v(it, 'ResultPath') == '$.c'
            v(it, 'Next') == 'Simple.Success'
        }
        output.lambda.contains('const response = a;')

        v("['Simple.Success']", 'Type') == 'Succeed'
    }


    def "test nested while"() {
        given:
        TestOutput output = runWithLambda('while.stg', 'nested')
        ReadContext ctx = output.ctx

        when:
        Closure v = { name, param ->
            return ctx.read('$.States.' + name + '.' + param)
        }

        then:
        Object[] data = ctx.read('$.States.*')
        data.length == 9

        ctx.read('$.StartAt') == 'Nested000'

        with('Nested000') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 5
            v(it, 'ResultPath') == '$.a'
            v(it, 'Next') == 'abc'
        }

        with('abc') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'abc'
            v(it, "Parameters.['a.\$']") == '$.a'
            v(it, 'ResultPath') == '$.Nestedvar__000'
            v(it, 'Next') == 'Nested001'
        }
        output.lambda.contains('const response = a<15;')

        with('Nested001') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.Nestedvar__000'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'Nested002'

            v(it, 'Choices[1].Variable') == '$.Nestedvar__000'
            v(it, 'Choices[1].BooleanEquals') == false
            v(it, 'Choices[1].Next') == 'Nested.Success'
        }

        with('Nested002') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'Nested002'
            v(it, "Parameters.['a.\$']") == '$.a'
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'def'
        }
        output.lambda.contains('const response = a+1;')

        with('def') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'def'
            v(it, "Parameters.['b.\$']") == '$.b'
            v(it, 'ResultPath') == '$.Nestedvar__001'
            v(it, 'Next') == 'Nested003'
        }
        output.lambda.contains('const response = b>3;')

        with('Nested003') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.Nestedvar__001'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'Nested004'

            v(it, 'Choices[1].Variable') == '$.Nestedvar__001'
            v(it, 'Choices[1].BooleanEquals') == false
            v(it, 'Choices[1].Next') == 'abc'
        }

        with('Nested004') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'Nested004'
            v(it, "Parameters.['a.\$']") == '$.a'
            v(it, "Parameters.['b.\$']") == '$.b'
            v(it, 'ResultPath') == '$.c'
            v(it, 'Next') == 'Nested005'
        }
        output.lambda.contains('const response = b+a;')

        with('Nested005') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'Nested005'
            v(it, "Parameters.['b.\$']") == '$.b'
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'def'
        }
        output.lambda.contains('const response = b - (1);')

        v("['Nested.Success']", 'Type') == 'Succeed'
    }


}