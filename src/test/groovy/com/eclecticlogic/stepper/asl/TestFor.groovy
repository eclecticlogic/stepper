package com.eclecticlogic.stepper.asl

import com.jayway.jsonpath.ReadContext

/**
 * General structure and assignment tests.
 */
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
            v(it, 'ResultPath') == '$.var__000'
            v(it, 'Next') == 'For001'
        }
        output.lambda.contains('const response = i <= 10;')

        with('For001') {
            v(it, 'Type') == 'Choice'
            v(it, 'Choices[0].Variable') == '$.var__000'
            v(it, 'Choices[0].BooleanEquals') == true
            v(it, 'Choices[0].Next') == 'For003'

            v(it, 'Choices[1].Variable') == '$.var__000'
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


}
