package com.eclecticlogic.stepper.asl

import com.jayway.jsonpath.ReadContext


class TestWhen extends AbstractStateMachineTester {

    def "test simple when"() {
        given:
        TestOutput output = runWithLambda('when.stg', 'simple')
        ReadContext ctx = output.ctx

        when:
        Closure v = { name, param ->
            return ctx.read('$.States.' + name + '.' + param)
        }

        then:
        Object[] data = ctx.read('$.States.*')
        data.length == 11

        ctx.read('$.StartAt') == 'Simple000'

        with('Simple000') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 5
            v(it, 'ResultPath') == '$.a'
            v(it, 'Next') == 'Simple001'
        }

        with('Simple001') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'Simple001'
            v(it, "Parameters.['a.\$']") == '$.a'
            v(it, 'ResultPath') == '$.Simplevar__000'
            v(it, 'Next') == 'Simple002'
        }
        output.lambda.contains('const response = a<5;')

        with('Simple002') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.Simplevar__000'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'Simple003'

            v(it, 'Choices[1].Variable') == '$.Simplevar__000'
            v(it, 'Choices[1].BooleanEquals') == false
            v(it, 'Choices[1].Next') == 'abc'
        }

        with('Simple003') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 1
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'Simple.Success'
        }

        with('abc') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'abc'
            v(it, "Parameters.['a.\$']") == '$.a'
            v(it, 'ResultPath') == '$.Simplevar__001'
            v(it, 'Next') == 'Simple004'
        }
        output.lambda.contains('const response = a==5;')

        with('Simple004') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.Simplevar__001'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'Simple005'

            v(it, 'Choices[1].Variable') == '$.Simplevar__001'
            v(it, 'Choices[1].BooleanEquals') == false
            v(it, 'Choices[1].Next') == 'Simple007'
        }
        with('Simple005') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 2
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'Simple006'
        }
        with('Simple006') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 1
            v(it, 'ResultPath') == '$.c'
            v(it, 'Next') == 'Simple.Success'
        }
        with('Simple007') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 3
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'Simple008'
        }
        with('Simple008') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 2
            v(it, 'ResultPath') == '$.c'
            v(it, 'Next') == 'Simple.Success'
        }

        v("['Simple.Success']", 'Type') == 'Succeed'
    }


    def "test nested when"() {
        given:
        TestOutput output = runWithLambda('when.stg', 'nested')
        ReadContext ctx = output.ctx

        when:
        Closure v = { name, param ->
            return ctx.read('$.States.' + name + '.' + param)
        }

        then:
        Object[] data = ctx.read('$.States.*')
        data.length == 15

        ctx.read('$.StartAt') == 'Nested000'

        with('Nested000') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'Nested000'
            v(it, "Parameters.['a.\$']") == '$.a'
            v(it, 'ResultPath') == '$.Nestedvar__000'
            v(it, 'Next') == 'Nested001'
        }
        output.lambda.contains('const response = a<5;')

        with('Nested001') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.Nestedvar__000'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'Nested002'

            v(it, 'Choices[1].Variable') == '$.Nestedvar__000'
            v(it, 'Choices[1].BooleanEquals') == false
            v(it, 'Choices[1].Next') == 'Nested008'
        }

        with('Nested002') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 1
            v(it, 'ResultPath') == '$.c'
            v(it, 'Next') == 'Nested003'
        }

        with('Nested003') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'Nested003'
            v(it, "Parameters.['c.\$']") == '$.c'
            v(it, 'ResultPath') == '$.Nestedvar__001'
            v(it, 'Next') == 'Nested004'
        }
        output.lambda.contains('const response = c==2;')

        with('Nested004') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.Nestedvar__001'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'Nested005'

            v(it, 'Choices[1].Variable') == '$.Nestedvar__001'
            v(it, 'Choices[1].BooleanEquals') == false
            v(it, 'Choices[1].Next') == 'x'
        }

        with('Nested005') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 1
            v(it, 'ResultPath') == '$.e'
            v(it, 'Next') == 'Nested.Success'
        }

        with('x') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'x'
            v(it, "Parameters.['c.\$']") == '$.c'
            v(it, 'ResultPath') == '$.Nestedvar__002'
            v(it, 'Next') == 'Nested006'
        }
        output.lambda.contains('const response = c==3;')

        with('Nested006') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.Nestedvar__002'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'Nested007'

            v(it, 'Choices[1].Variable') == '$.Nestedvar__002'
            v(it, 'Choices[1].BooleanEquals') == false
            v(it, 'Choices[1].Next') == 'Nested.Success'
        }

        with('Nested007') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 2
            v(it, 'ResultPath') == '$.e'
            v(it, 'Next') == 'Nested.Success'
        }

        with('Nested008') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 3
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'a1'
        }

        with('a1') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'a1'
            v(it, "Parameters.['b.\$']") == '$.b'
            v(it, 'ResultPath') == '$.Nestedvar__003'
            v(it, 'Next') == 'Nested009'
        }
        output.lambda.contains('const response = b%3==0;')

        with('Nested009') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.Nestedvar__003'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'Nested010'

            v(it, 'Choices[1].Variable') == '$.Nestedvar__003'
            v(it, 'Choices[1].BooleanEquals') == false
            v(it, 'Choices[1].Next') == 'a2'
        }

        with('Nested010') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 1
            v(it, 'ResultPath') == '$.d'
            v(it, 'Next') == 'Nested.Success'
        }

        with('a2') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 2
            v(it, 'ResultPath') == '$.d'
            v(it, 'Next') == 'Nested.Success'
        }

        v("['Nested.Success']", 'Type') == 'Succeed'
    }


}