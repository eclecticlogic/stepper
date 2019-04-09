package com.eclecticlogic.stepper.asl

import com.jayway.jsonpath.ReadContext


class TestIf extends AbstractStateMachineTester {

    def "test simple if"() {
        given:
        TestOutput output = runWithLambda('if.stg', 'simpleIf')
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
        output.lambda.contains('const response = a<5;')

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
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 6
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'Simple003'
        }

        with('Simple003') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 10
            v(it, 'ResultPath') == '$.d'
            v(it, 'Next') == 'Simple004'
        }
        with('Simple004') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 7
            v(it, 'ResultPath') == '$.c'
            v(it, 'Next') == 'Simple.Success'
        }

        v("['Simple.Success']", 'Type') == 'Succeed'
    }


    def "test if else"() {
        given:
        TestOutput output = runWithLambda('if.stg', 'ifelse')
        ReadContext ctx = output.ctx

        when:
        Closure v = { name, param ->
            return ctx.read('$.States.' + name + '.' + param)
        }

        then:
        Object[] data = ctx.read('$.States.*')
        data.length == 9

        ctx.read('$.StartAt') == 'IfElse000'

        with('IfElse000') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 5
            v(it, 'ResultPath') == '$.a'
            v(it, 'Next') == 'abc'
        }

        with('abc') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'abc'
            v(it, "Parameters.['a.\$']") == '$.a'
            v(it, 'ResultPath') == '$.IfElsevar__000'
            v(it, 'Next') == 'IfElse001'
        }
        output.lambda.contains('const response = a<5;')

        with('IfElse001') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.IfElsevar__000'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'IfElse002'

            v(it, 'Choices[1].Variable') == '$.IfElsevar__000'
            v(it, 'Choices[1].BooleanEquals') == false
            v(it, 'Choices[1].Next') == 'IfElse004'
        }

        with('IfElse002') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 6
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'IfElse003'
        }

        with('IfElse003') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 0
            v(it, 'ResultPath') == '$.d'
            v(it, 'Next') == 'IfElse006'
        }

        with('IfElse004') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 4
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'IfElse005'
        }

        with('IfElse005') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 1
            v(it, 'ResultPath') == '$.d'
            v(it, 'Next') == 'IfElse006'
        }

        with('IfElse006') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 7
            v(it, 'ResultPath') == '$.c'
            v(it, 'Next') == 'IfElse.Success'
        }

        v("['IfElse.Success']", 'Type') == 'Succeed'
    }


    def "test if else if"() {
        given:
        TestOutput output = runWithLambda('if.stg', 'ifelseif')
        ReadContext ctx = output.ctx

        when:
        Closure v = { name, param ->
            return ctx.read('$.States.' + name + '.' + param)
        }

        then:
        Object[] data = ctx.read('$.States.*')
        data.length == 13

        ctx.read('$.StartAt') == 'IfElse000'

        with('IfElse000') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 5
            v(it, 'ResultPath') == '$.a'
            v(it, 'Next') == 'abc'
        }

        with('abc') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'abc'
            v(it, "Parameters.['a.\$']") == '$.a'
            v(it, 'ResultPath') == '$.IfElsevar__000'
            v(it, 'Next') == 'IfElse001'
        }
        output.lambda.contains('const response = a<5;')

        with('IfElse001') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.IfElsevar__000'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'IfElse002'

            v(it, 'Choices[1].Variable') == '$.IfElsevar__000'
            v(it, 'Choices[1].BooleanEquals') == false
            v(it, 'Choices[1].Next') == 'def'
        }

        with('IfElse002') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 6
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'IfElse003'
        }

        with('IfElse003') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 0
            v(it, 'ResultPath') == '$.d'
            v(it, 'Next') == 'IfElse009'
        }

        with('def') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'def'
            v(it, "Parameters.['a.\$']") == '$.a'
            v(it, 'ResultPath') == '$.IfElsevar__001'
            v(it, 'Next') == 'IfElse004'
        }
        output.lambda.contains('const response = a>10;')

        with('IfElse004') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.IfElsevar__001'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'IfElse005'

            v(it, 'Choices[1].Variable') == '$.IfElsevar__001'
            v(it, 'Choices[1].BooleanEquals') == false
            v(it, 'Choices[1].Next') == 'IfElse007'
        }

        with('IfElse005') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 4
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'IfElse006'
        }

        with('IfElse006') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 1
            v(it, 'ResultPath') == '$.d'
            v(it, 'Next') == 'IfElse009'
        }

        with('IfElse007') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 10
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'IfElse008'
        }

        with('IfElse008') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 2
            v(it, 'ResultPath') == '$.d'
            v(it, 'Next') == 'IfElse009'
        }

        with('IfElse009') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 7
            v(it, 'ResultPath') == '$.c'
            v(it, 'Next') == 'IfElse.Success'
        }

        v("['IfElse.Success']", 'Type') == 'Succeed'
    }


    def "test nested if"() {
        given:
        TestOutput output = runWithLambda('if.stg', 'nestedif')
        ReadContext ctx = output.ctx

        when:
        Closure v = { name, param ->
            return ctx.read('$.States.' + name + '.' + param)
        }

        then:
        Object[] data = ctx.read('$.States.*')
        data.length == 13

        ctx.read('$.StartAt') == 'IfElse000'

        with('IfElse000') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 5
            v(it, 'ResultPath') == '$.a'
            v(it, 'Next') == 'abc'
        }

        with('abc') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'abc'
            v(it, "Parameters.['a.\$']") == '$.a'
            v(it, 'ResultPath') == '$.IfElsevar__000'
            v(it, 'Next') == 'IfElse001'
        }
        output.lambda.contains('const response = a<5;')

        with('IfElse001') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.IfElsevar__000'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'def'

            v(it, 'Choices[1].Variable') == '$.IfElsevar__000'
            v(it, 'Choices[1].BooleanEquals') == false
            v(it, 'Choices[1].Next') == 'pqr'
        }

        with('def') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'def'
            v(it, "Parameters.['a.\$']") == '$.a'
            v(it, 'ResultPath') == '$.IfElsevar__001'
            v(it, 'Next') == 'IfElse002'
        }
        output.lambda.contains('const response = a<2;')

        with('IfElse002') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.IfElsevar__001'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'IfElse003'

            v(it, 'Choices[1].Variable') == '$.IfElsevar__001'
            v(it, 'Choices[1].BooleanEquals') == false
            v(it, 'Choices[1].Next') == 'IfElse004'
        }

        with('IfElse003') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 3
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'IfElse008'
        }

        with('IfElse004') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 5
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'IfElse008'
        }

        with('pqr') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'pqr'
            v(it, "Parameters.['a.\$']") == '$.a'
            v(it, 'ResultPath') == '$.IfElsevar__002'
            v(it, 'Next') == 'IfElse005'
        }
        output.lambda.contains('const response = a>12;')

        with('IfElse005') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.IfElsevar__002'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'IfElse006'

            v(it, 'Choices[1].Variable') == '$.IfElsevar__002'
            v(it, 'Choices[1].BooleanEquals') == false
            v(it, 'Choices[1].Next') == 'IfElse007'
        }

        with('IfElse006') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 13
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'IfElse008'
        }

        with('IfElse007') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 15
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'IfElse008'
        }

        with('IfElse008') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 7
            v(it, 'ResultPath') == '$.c'
            v(it, 'Next') == 'IfElse.Success'
        }

        v("['IfElse.Success']", 'Type') == 'Succeed'
    }

}