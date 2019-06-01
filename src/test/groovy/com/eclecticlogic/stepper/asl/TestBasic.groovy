package com.eclecticlogic.stepper.asl

import com.jayway.jsonpath.ReadContext

/**
 * General structure and assignment tests.
 */
class TestBasic extends AbstractStateMachineTester {

    def "test simple"() {
        given:
        ReadContext ctx = run('basic.stg', 'simple')

        expect:
        with(ctx) {
            read('$.StartAt') == 'xyz'
            read('$.States').size() == 2

            read('$..xyz.Type')[0] == 'Pass'
            read('$..xyz.Result')[0] == 'Hello World'
            read('$..xyz.ResultPath')[0] == '$.a'
            read('$..xyz.Next')[0] == 'Simple.Success'

            read("\$..['Simple.Success'].Type")[0] == 'Succeed'
        }
    }


    def "test annotation and state name"() {
        given:
        ReadContext ctx = run('basic.stg', 'annotationName')

        expect:
        with(ctx) {
            read('$.Comment') == 'this is a comment'
            read('$.TimeoutSeconds') == 3600
            read('$.Version') == '1.0'

            read('$.StartAt') == 'AnnotationTest000'
            read('$..AnnotationTest000.Type')[0] == 'Pass'
            read('$..AnnotationTest000.Result')[0] == 5
            read('$..AnnotationTest000.ResultPath')[0] == '$.c'
            read('$..AnnotationTest000.Next')[0] == 'AnnotationTest.Success'

            read("\$..['AnnotationTest.Success'].Type")[0] == 'Succeed'
        }
    }


    def "test primitive assignment"() {
        given:
        ReadContext ctx = run('basic.stg', 'assignmentPrimitive')

        when:
        Closure v = { key, value, resultVar ->
            verifyAll(ctx) {
                read('$..' + key + '.Type')[0] == 'Pass'
                read('$..' + key + '.Result')[0] == value
                read('$..' + key + '.ResultPath')[0] == '$.' + resultVar
            }
            return true
        }

        then:
        v('abc', 5.2, 'a')
        v('assignment000', 10, 'b')
        v('assignment001', 'Hello World', 'c')
        v('assignment002', true, 'd.e.f')
        v('xyz', false, 'e.f.g')
    }


    def "test complex assignment"() {
        given:
        TestOutput output = runWithLambda('basic.stg', 'assignmentComplex')
        ReadContext ctx = output.ctx
        def name1 = 'complex000'
        def name2 = 'one'
        def name3 = 'complex001'
        def name4 = 'complex002'
        def name5 = 'two'

        when:
        Closure v = { stateName, key, value ->
            verifyAll(ctx) {
                read('$.States.' + stateName + '.' + key) == value
            }
            return true
        }

        then:
        Object[] data = ctx.read('$.States.*')
        data.length == 6

        v(name1, 'Type', 'Task')
        v(name1, 'ResultPath', '$.value1')
        v(name1, 'Parameters.cmd__sm', name1)
        v(name1, "Parameters.['a.\$']", '$.a')
        v(name1, "Parameters.['b.\$']", '$.b')
        v(name1, "Parameters.['c.\$']", '$.c')
        v(name1, "Parameters.['d.\$']", '$.d')
        v(name1, "Parameters.['e.\$']", '$.e')
        v(name1, 'Next', name2)
        output.lambda.contains('const response = a*b+c.calc(e)-d.length();')

        v(name2, 'Type', 'Task')
        v(name2, 'ResultPath', '$.value2')
        v(name2, 'Parameters.cmd__sm', name2)
        v(name2, "Parameters.['a.\$']", '$.a')
        v(name2, "Parameters.['b.\$']", '$.b')
        v(name2, "Parameters.['c.\$']", '$.c')
        v(name2, "Parameters.['d.\$']", '$.d')
        v(name2, "Parameters.['e.\$']", '$.e')
        v(name2, 'Next', name3)
        output.lambda.contains('const response = value2 + (a*b+c.calc(e)-d.length());')

        v(name3, 'Type', 'Task')
        v(name3, 'ResultPath', '$.value3')
        v(name3, 'Parameters.cmd__sm', name3)
        v(name3, "Parameters.['a.\$']", '$.a')
        v(name3, "Parameters.['b.\$']", '$.b')
        v(name3, "Parameters.['c.\$']", '$.c')
        v(name3, "Parameters.['d.\$']", '$.d')
        v(name3, "Parameters.['e.\$']", '$.e')
        v(name3, 'Next', name4)
        output.lambda.contains('const response = value3 - (a*b+c.calc(e)-d.length());')

        v(name4, 'Type', 'Task')
        v(name4, 'ResultPath', '$.value4')
        v(name4, 'Parameters.cmd__sm', name4)
        v(name4, "Parameters.['a.\$']", '$.a')
        v(name4, "Parameters.['b.\$']", '$.b')
        v(name4, "Parameters.['c.\$']", '$.c')
        v(name4, "Parameters.['d.\$']", '$.d')
        v(name4, "Parameters.['e.\$']", '$.e')
        v(name4, 'Next', name5)
        output.lambda.contains('const response = value4 / (a*b+c.calc(e)-d.length());')

        v(name5, 'Type', 'Task')
        v(name5, 'ResultPath', '$.value5')
        v(name5, 'Parameters.cmd__sm', name5)
        v(name5, "Parameters.['a.\$']", '$.a')
        v(name5, "Parameters.['b.\$']", '$.b')
        v(name5, "Parameters.['c.\$']", '$.c')
        v(name5, "Parameters.['d.\$']", '$.d')
        v(name5, "Parameters.['e.\$']", '$.e')
        v(name5, 'Next', 'complex.Success')
        output.lambda.contains('const response = value5 * (a*b+c.calc(e)-d.length());')

        v("['complex.Success']", 'Type', 'Succeed')
    }


    def "test assignment json"() {
        given:
        TestOutput output = runWithLambda('basic.stg', 'assignmentJson')
        ReadContext ctx = output.ctx
        def name = 'xyz'

        when:
        Closure v = { stateName, key, value ->
            verifyAll(ctx) {
                read('$.States.' + stateName + '.' + key) == value
            }
            return true
        }

        then:
        Object[] data = ctx.read('$.States.*')
        data.length == 2

        v(name, 'Type', 'Pass')
        v(name, 'Parameters.a', 5)
        v(name, 'Parameters.b', 10)
        v(name, 'Parameters.c.d', 'x')
        v(name, 'Parameters.e[0]', 'p')
        v(name, 'Parameters.e[1]', true)
        v(name, 'Parameters.e[2]', false)
        v(name, 'ResultPath', '$.value')
        v(name, 'Next', 'json.Success')

        v("['json.Success']", 'Type', 'Succeed')
    }


    def "test assignment json array"() {
        given:
        TestOutput output = runWithLambda('basic.stg', 'assignmentJsonArray')
        ReadContext ctx = output.ctx
        def name = 'xyz'

        when:
        Closure v = { stateName, key, value ->
            verifyAll(ctx) {
                read('$.States.' + stateName + '.' + key) == value
            }
            return true
        }

        then:
        Object[] data = ctx.read('$.States.*')
        data.length == 2

        v(name, 'Type', 'Pass')
        v(name, 'Result[0]', 'p')
        v(name, 'Result[1]', true)
        v(name, 'Result[2]', false)
        v(name, 'Result[3]', 2.5)
        v(name, 'Result[4]', 10)
        v(name, 'ResultPath', '$.value')
        v(name, 'Next', 'array.Success')

        v("['array.Success']", 'Type', 'Succeed')
    }


    def "test assignment task"() {
        given:
        TestOutput output = runWithLambda('basic.stg', 'assignmentTask')
        ReadContext ctx = output.ctx

        expect:
        Object[] data = ctx.read('$.States.*')
        data.length == 2

        ctx.read('$..x.Type')[0] == 'Task'
        ctx.read('$..x.ResultPath')[0] == '$.value'
        ctx.read('$..x.a')[0] == 'b'
    }


    def "test task"() {
        given:
        TestOutput output = runWithLambda('basic.stg', 'task')
        ReadContext ctx = output.ctx

        expect:
        Object[] data = ctx.read('$.States.*')
        data.length == 2

        ctx.read('$..hello.Type')[0] == 'Task'
        ctx.read('$..hello.a')[0] == 'b'
        ctx.read('$..hello.ResultPath')[0] == '$.hello'
        ctx.read('$.States.hello.Retry[0].BackoffRate') == 5
        ctx.read('$.States.hello.Retry[0].IntervalSeconds') == 3
        ctx.read('$.States.hello.Retry[0].ErrorEquals')[0] == 'abc'
        ctx.read('$.States.hello.Retry[0].ErrorEquals')[1] == 'def'
        ctx.read('$.States.hello.Retry[0].MaxAttempts') == 4
        ctx.read('$.States.hello.Retry[1].IntervalSeconds') == 6
        ctx.read('$.States.hello.Retry[1].ErrorEquals')[0] == 'pqr'
    }


    def "test wait"() {
        given:
        TestOutput output = runWithLambda('basic.stg', 'wait')
        ReadContext ctx = output.ctx

        expect:
        Object[] data = ctx.read('$.States.*')
        data.length == 2

        ctx.read('$..abc.Type')[0] == 'Wait'
        ctx.read('$..abc.Seconds')[0] == 10
        ctx.read('$..abc.Next')[0] == 'waiter.Success'
        ctx.read("\$..['waiter.Success'].Type")[0] == 'Succeed'
    }
}
