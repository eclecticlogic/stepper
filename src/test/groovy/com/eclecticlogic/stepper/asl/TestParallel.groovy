package com.eclecticlogic.stepper.asl

import com.jayway.jsonpath.ReadContext


class TestParallel extends AbstractStateMachineTester {

    def "test parallel1"() {
        given:
        TestOutput output = runWithLambda('parallel.stg', 'parallel1')
        ReadContext ctx = output.ctx

        when:
        Closure v = { name, param ->
            return ctx.read('$.States.' + name + '.' + param)
        }
        Closure b = { name, branch, param ->
            return v(name + '.' + branch, param)
        }

        then:
        Object[] data = ctx.read('$.States.*')
        data.length == 3

        ctx.read('$.StartAt') == 'Para000'

        with('Para000') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'Para000'
            v(it, 'ResultPath') == '$.a'
            v(it, 'Next') == 'a'
        }

        with('a') {
            v(it, 'Type') == 'Parallel'
            v(it, 'Retry[0].ErrorEquals[0]') == 'pqr'
            v(it, 'Retry[0].IntervalSeconds') == 6
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'Para.Success'
            v(it, 'Branches[0].StartAt') == 'First000'
            def name = it
            with('Branches[0].States.First000') {
                b(name, it, 'Type') == 'Task'
                b(name, it, 'Parameters.cmd__sm') == 'First000'
                b(name, it, 'ResultPath') == '$.a'
                b(name, it, 'Next') == 'First001'
            }
            with('Branches[0].States.First001') {
                b(name, it, 'Type') == 'Pass'
                b(name, it, 'Result') == 10
                b(name, it, 'ResultPath') == '$.b'
                b(name, it, 'Next') == 'First.Success'
            }
            with("Branches[0].States.['First.Success']") {
                b(name, it, 'Type') == 'Succeed'
            }

            v(it, 'Branches[1].StartAt') == 'Second000'
            with('Branches[1].States.Second000') {
                b(name, it, 'Type') == 'Task'
                b(name, it, 'Parameters.cmd__sm') == 'Second000'
                b(name, it, 'ResultPath') == '$.a'
                b(name, it, 'Next') == 'Second001'
            }
            with('Branches[1].States.Second001') {
                b(name, it, 'Type') == 'Pass'
                b(name, it, 'Result') == 20
                b(name, it, 'ResultPath') == '$.b'
                b(name, it, 'Next') == 'Second.Success'
            }
            with("Branches[1].States.['Second.Success']") {
                b(name, it, 'Type') == 'Succeed'
            }
        }

        v("['Para.Success']", 'Type') == 'Succeed'

        output.lambda.contains('if (event.cmd__sm == "Para000") {')
        output.lambda.contains('if (event.cmd__sm == "First000") {')
        output.lambda.contains('if (event.cmd__sm == "Second000") {')
        output.lambda.contains('const response = 5*2;')
        output.lambda.contains('const response = 5*5;')
        output.lambda.contains('const response = 15*5;')

    }


}