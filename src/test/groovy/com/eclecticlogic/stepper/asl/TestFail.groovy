package com.eclecticlogic.stepper.asl

import com.jayway.jsonpath.ReadContext


class TestFail extends AbstractStateMachineTester {

    def "test simple fail"() {
        given:
        TestOutput output = runWithLambda('fail.stg', 'simple')
        ReadContext ctx = output.ctx

        when:
        Closure v = { name, param ->
            return ctx.read('$.States.' + name + '.' + param)
        }

        then:
        Object[] data = ctx.read('$.States.*')
        data.length == 5

        ctx.read('$.StartAt') == 'Fail000'

        with('Fail000') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 5
            v(it, 'ResultPath') == '$.a'
            v(it, 'Next') == 'Fail001'
        }

        with('Fail001') {
            v(it, 'Type') == 'Fail'
        }

        with('Fail002') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 7
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'Fail003'
        }

        with('Fail003') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 5
            v(it, 'ResultPath') == '$.d'
            v(it, 'Next') == 'Fail.Success'
        }

        v("['Fail.Success']", 'Type') == 'Succeed'
    }


    def "test fail with attributes"() {
        given:
        TestOutput output = runWithLambda('fail.stg', 'failWithAttributes')
        ReadContext ctx = output.ctx

        when:
        Closure v = { name, param ->
            return ctx.read('$.States.' + name + '.' + param)
        }

        then:
        Object[] data = ctx.read('$.States.*')
        data.length == 5

        ctx.read('$.StartAt') == 'Fail000'

        with('Fail000') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 5
            v(it, 'ResultPath') == '$.a'
            v(it, 'Next') == 'a'
        }

        with('a') {
            v(it, 'Type') == 'Fail'
            v(it, 'Cause') == 'blah1'
            v(it, 'Error') == 'blah2'
        }

        with('Fail001') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 7
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'Fail002'
        }

        with('Fail002') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 5
            v(it, 'ResultPath') == '$.d'
            v(it, 'Next') == 'Fail.Success'
        }

        v("['Fail.Success']", 'Type') == 'Succeed'
    }
}