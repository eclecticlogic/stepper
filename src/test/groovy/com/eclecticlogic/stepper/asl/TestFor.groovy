package com.eclecticlogic.stepper.asl

import com.jayway.jsonpath.ReadContext

class TestFor extends AbstractStateMachineTester {

    def "test simple for"() {
        given:
        TestOutput output = runWithLambda('forloop.stg', 'simpleFor')
        ReadContext ctx = output.ctx

        when:
        Closure v = { name, param ->
            return ctx.read('$.States.' + name + '.' + param)
        }

        then:
        Object[] data = ctx.read('$.States.*')
        data.length == 6

        ctx.read('$.StartAt') == 'abc'

        with('abc') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'abc'
            v(it, 'ResultPath') == '$.i'
            v(it, 'Next') == 'For000'
        }
        output.lambda.contains('const response = 0;')

        with('For000') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'For000'
            v(it, "Parameters.['i.\$']") == '$.i'
            v(it, 'ResultPath') == '$.Forvar__000'
            v(it, 'Next') == 'For001'
        }
        output.lambda.contains('const response = i <= 10;')

        with('For001') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.Forvar__000'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'For003'

            v(it, 'Choices[1].Variable') == '$.Forvar__000'
            v(it, 'Choices[1].BooleanEquals') == false
            v(it, 'Choices[1].Next') == 'For.Success'
        }

        with('For003') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'For003'
            v(it, "Parameters.['i.\$']") == '$.i'
            v(it, 'ResultPath') == '$.a'
            v(it, 'Next') == 'For002'
        }
        output.lambda.contains('const response = a + (i);')

        with('For002') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'For002'
            v(it, "Parameters.['i.\$']") == '$.i'
            v(it, 'ResultPath') == '$.i'
            v(it, 'Next') == 'For000'
        }
        output.lambda.contains('const response = i + (2);')

        v("['For.Success']", 'Type') == 'Succeed'
    }


    def "test double for"() {
        given:
        TestOutput output = runWithLambda('forloop.stg', 'doubleFor')
        ReadContext ctx = output.ctx

        when:
        Closure v = { name, param ->
            return ctx.read('$.States.' + name + '.' + param)
        }

        then:
        Object[] data = ctx.read('$.States.*')
        data.length == 12

        ctx.read('$.StartAt') == 'For000'

        with('For000') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'For000'
            v(it, 'ResultPath') == '$.i'
            v(it, 'Next') == 'For001'
        }
        output.lambda.contains('const response = 0;')

        with('For001') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'For001'
            v(it, "Parameters.['i.\$']") == '$.i'
            v(it, 'ResultPath') == '$.Forvar__000'
            v(it, 'Next') == 'For002'
        }
        output.lambda.contains('const response = i <= 10;')

        with('For002') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.Forvar__000'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'For004'
            v(it, 'Choices[1].Variable') == '$.Forvar__000'
            v(it, 'Choices[1].BooleanEquals') == false
            v(it, 'Choices[1].Next') == 'For.Success'
        }
        // inner loop
        with('For004') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'For004'
            v(it, 'ResultPath') == '$.j'
            v(it, 'Next') == 'For005'
        }
        output.lambda.contains('const response = 2;')

        with('For005') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'For005'
            v(it, "Parameters.['j.\$']") == '$.j'
            v(it, 'ResultPath') == '$.Forvar__001'
            v(it, 'Next') == 'For006'
        }
        output.lambda.contains('const response = j <= 12;')

        with('For006') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.Forvar__001'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'For008'
            v(it, 'Choices[1].Variable') == '$.Forvar__001'
            v(it, 'Choices[1].BooleanEquals') == false
            v(it, 'Choices[1].Next') == 'For003'
        }
        with('For008') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'For008'
            v(it, "Parameters.['i.\$']") == '$.i'
            v(it, 'ResultPath') == '$.a'
            v(it, 'Next') == 'For009'
        }
        output.lambda.contains('const response = i;')
        with('For009') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'For009'
            v(it, "Parameters.['j.\$']") == '$.j'
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'For010'
        }
        output.lambda.contains('const response = j;')
        with('For010') {
            v(it, 'Type') == 'Task'
            v(it, "Parameters.cmd__sm") == 'For010'
            v(it, "Parameters.['a.\$']") == '$.a'
            v(it, "Parameters.['b.\$']") == '$.b'
            v(it, 'ResultPath') == '$.c'
            v(it, 'Next') == 'For007'
        }
        output.lambda.contains('const response = a*b;')
        with('For007') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'For007'
            v(it, "Parameters.['j.\$']") == '$.j'
            v(it, 'ResultPath') == '$.j'
            v(it, 'Next') == 'For005'
        }
        output.lambda.contains('const response = j + (1);')
        with('For003') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'For003'
            v(it, "Parameters.['i.\$']") == '$.i'
            v(it, 'ResultPath') == '$.i'
            v(it, 'Next') == 'For001'
        }
        output.lambda.contains('const response = i + (2);')
        v("['For.Success']", 'Type') == 'Succeed'
    }


    def "test simple iter"() {
        given:
        TestOutput output = runWithLambda('forloop.stg', 'simpleIter')
        ReadContext ctx = output.ctx

        when:
        Closure v = { name, param ->
            return ctx.read('$.States.' + name + '.' + param)
        }

        then:
        Object[] data = ctx.read('$.States.*')
        data.length == 6

        ctx.read('$.StartAt') == 'abc'

        with('abc') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result.idx') == -1
            v(it, 'ResultPath') == '$.Forvar__000'
            v(it, 'Next') == 'For000'
        }

        with('For000') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'For000'
            v(it, "Parameters.['b.\$']") == '$.b'
            v(it, "Parameters.['e.\$']") == '$.e'
            v(it, "Parameters.['Forvar__000.\$']") == '$.Forvar__000'
            v(it, 'ResultPath') == '$.Forvar__000'
            v(it, 'Next') == 'For001'
        }

        with('For001') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.Forvar__000.exists'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'For002'
            v(it, 'Choices[1].Variable') == '$.Forvar__000.exists'
            v(it, 'Choices[1].BooleanEquals') == false
            v(it, 'Choices[1].Next') == 'For.Success'
        }

        with('For002') {
            v(it, 'Type') == 'Pass'
            v(it, 'InputPath') == '$.Forvar__000.var'
            v(it, 'ResultPath') == '$.a'
            v(it, 'Next') == 'For003'
        }

        with('For003') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'For003'
            v(it, "Parameters.['a.\$']") == '$.a'
            v(it, 'ResultPath') == '$.j'
            v(it, 'Next') == 'For000'
        }

        with("['For.Success']") {
            v(it, 'Type') == 'Succeed'
        }
    }


    def "test double iter"() {
        given:
        TestOutput output = runWithLambda('forloop.stg', 'doubleIter')
        ReadContext ctx = output.ctx

        when:
        Closure v = { name, param ->
            return ctx.read('$.States.' + name + '.' + param)
        }

        then:
        Object[] data = ctx.read('$.States.*')
        data.length == 10

        ctx.read('$.StartAt') == 'abc'

        with('abc') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result.idx') == -1
            v(it, 'ResultPath') == '$.Doublevar__000'
            v(it, 'Next') == 'Double000'
        }

        with('Double000') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'Double000'
            v(it, "Parameters.['b.\$']") == '$.b'
            v(it, "Parameters.['Doublevar__000.\$']") == '$.Doublevar__000'
            v(it, 'ResultPath') == '$.Doublevar__000'
            v(it, 'Next') == 'Double001'
        }

        with('Double001') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.Doublevar__000.exists'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'Double002'
            v(it, 'Choices[1].Variable') == '$.Doublevar__000.exists'
            v(it, 'Choices[1].BooleanEquals') == false
            v(it, 'Choices[1].Next') == 'Double.Success'
        }

        with('Double002') {
            v(it, 'Type') == 'Pass'
            v(it, 'InputPath') == '$.Doublevar__000.var'
            v(it, 'ResultPath') == '$.a'
            v(it, 'Next') == 'def'
        }

        with('def') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result.idx') == -1
            v(it, 'ResultPath') == '$.Doublevar__001'
            v(it, 'Next') == 'Double003'
        }

        with('Double003') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'Double003'
            v(it, "Parameters.['d.\$']") == '$.d'
            v(it, "Parameters.['Doublevar__001.\$']") == '$.Doublevar__001'
            v(it, 'ResultPath') == '$.Doublevar__001'
            v(it, 'Next') == 'Double004'
        }

        with('Double004') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.Doublevar__001.exists'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'Double005'
            v(it, 'Choices[1].Variable') == '$.Doublevar__001.exists'
            v(it, 'Choices[1].BooleanEquals') == false
            v(it, 'Choices[1].Next') == 'Double000'
        }

        with('Double005') {
            v(it, 'Type') == 'Pass'
            v(it, 'InputPath') == '$.Doublevar__001.var'
            v(it, 'ResultPath') == '$.c'
            v(it, 'Next') == 'Double006'
        }

        with('Double006') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'Double006'
            v(it, "Parameters.['a.\$']") == '$.a'
            v(it, 'ResultPath') == '$.j'
            v(it, 'Next') == 'Double003'
        }

        with("['Double.Success']") {
            v(it, 'Type') == 'Succeed'
        }
    }

}