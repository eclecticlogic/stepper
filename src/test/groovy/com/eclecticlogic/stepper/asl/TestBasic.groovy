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
            read('$.StartAt') == 'Simple000'
            read('$.States').size() == 2

            read('$..Simple000.Type')[0] == 'Pass'
            read('$..Simple000.Result')[0] == 'Hello World'
            read('$..Simple000.ResultPath')[0] == '$.a'
            read('$..Simple000.Next')[0] == 'Simple001'

            read('$..Simple001.Type')[0] == 'Succeed'
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
            read('$..AnnotationTest000.Next')[0] == 'AnnotationTest001'

            read('$..AnnotationTest001.Type')[0] == 'Succeed'
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
        v('assignment000', 5.2, 'a')
        v('assignment001', 10, 'b')
        v('assignment002', 'Hello World', 'c')
        v('assignment003', true, 'd.e.f')
        v('assignment004', false, 'e.f.g')
    }


    def "test complex assignment"() {
        given:
        TestOutput output = runWithLambda('basic.stg', 'assignmentComplex')
        ReadContext ctx = output.ctx

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

        v('complex000', 'Type', 'Task')
        v('complex000', 'ResultPath', '$.value1')
        v('complex000', 'Parameters.cmd__sm', 'complex000')
        v('complex000', "Parameters.['a.\$']", '$.a')
        v('complex000', "Parameters.['b.\$']", '$.b')
        v('complex000', "Parameters.['c.\$']", '$.c')
        v('complex000', "Parameters.['d.\$']", '$.d')
        v('complex000', "Parameters.['e.\$']", '$.e')
        v('complex000', 'Next', 'complex001')
        output.lambda.contains('const response = a*b+c.calc(e)-d.length();')

        v('complex001', 'Type', 'Task')
        v('complex001', 'ResultPath', '$.value2')
        v('complex001', 'Parameters.cmd__sm', 'complex001')
        v('complex001', "Parameters.['a.\$']", '$.a')
        v('complex001', "Parameters.['b.\$']", '$.b')
        v('complex001', "Parameters.['c.\$']", '$.c')
        v('complex001', "Parameters.['d.\$']", '$.d')
        v('complex001', "Parameters.['e.\$']", '$.e')
        v('complex001', 'Next', 'complex002')
        output.lambda.contains('const response = value2 + (a*b+c.calc(e)-d.length());')

        v('complex002', 'Type', 'Task')
        v('complex002', 'ResultPath', '$.value3')
        v('complex002', 'Parameters.cmd__sm', 'complex002')
        v('complex002', "Parameters.['a.\$']", '$.a')
        v('complex002', "Parameters.['b.\$']", '$.b')
        v('complex002', "Parameters.['c.\$']", '$.c')
        v('complex002', "Parameters.['d.\$']", '$.d')
        v('complex002', "Parameters.['e.\$']", '$.e')
        v('complex002', 'Next', 'complex003')
        output.lambda.contains('const response = value3 - (a*b+c.calc(e)-d.length());')

        v('complex003', 'Type', 'Task')
        v('complex003', 'ResultPath', '$.value4')
        v('complex003', 'Parameters.cmd__sm', 'complex003')
        v('complex003', "Parameters.['a.\$']", '$.a')
        v('complex003', "Parameters.['b.\$']", '$.b')
        v('complex003', "Parameters.['c.\$']", '$.c')
        v('complex003', "Parameters.['d.\$']", '$.d')
        v('complex003', "Parameters.['e.\$']", '$.e')
        v('complex003', 'Next', 'complex004')
        output.lambda.contains('const response = value4 / (a*b+c.calc(e)-d.length());')

        v('complex004', 'Type', 'Task')
        v('complex004', 'ResultPath', '$.value5')
        v('complex004', 'Parameters.cmd__sm', 'complex004')
        v('complex004', "Parameters.['a.\$']", '$.a')
        v('complex004', "Parameters.['b.\$']", '$.b')
        v('complex004', "Parameters.['c.\$']", '$.c')
        v('complex004', "Parameters.['d.\$']", '$.d')
        v('complex004', "Parameters.['e.\$']", '$.e')
        v('complex004', 'Next', 'complex005')
        output.lambda.contains('const response = value5 * (a*b+c.calc(e)-d.length());')

        v('complex005', 'Type', 'Succeed')
    }


    def "test assignment json"() {
        given:
        TestOutput output = runWithLambda('basic.stg', 'assignmentJson')
        ReadContext ctx = output.ctx

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

        v('json000', 'Type', 'Pass')
        v('json000', 'Parameters.a', 5)
        v('json000', 'Parameters.b', 10)
        v('json000', 'Parameters.c.d', 'x')
        v('json000', 'Parameters.e[0]', 'p')
        v('json000', 'Parameters.e[1]', true)
        v('json000', 'Parameters.e[2]', false)
        v('json000', 'ResultPath', '$.value')
        v('json000', 'Next', 'json001')

        v('json001', 'Type', 'Succeed')
    }


    def "test assignment json array"() {
        given:
        TestOutput output = runWithLambda('basic.stg', 'assignmentJsonArray')
        ReadContext ctx = output.ctx

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

        v('array000', 'Type', 'Pass')
        v('array000', 'Result[0]', 'p')
        v('array000', 'Result[1]', true)
        v('array000', 'Result[2]', false)
        v('array000', 'Result[3]', 2.5)
        v('array000', 'Result[4]', 10)
        v('array000', 'ResultPath', '$.value')
        v('array000', 'Next', 'array001')

        v('array001', 'Type', 'Succeed')
    }


    def "test assignment task"() {
        given:
        TestOutput output = runWithLambda('basic.stg', 'assignmentTask')
        ReadContext ctx = output.ctx

        expect:
        Object[] data = ctx.read('$.States.*')
        data.length == 2

        ctx.read('$..mytask000.Type')[0] == 'Task'
        ctx.read('$..mytask000.ResultPath')[0] == '$.value'
        ctx.read('$..mytask000.a')[0] == 'b'
    }


    def "test task"() {
        given:
        TestOutput output = runWithLambda('basic.stg', 'task')
        ReadContext ctx = output.ctx

        expect:
        Object[] data = ctx.read('$.States.*')
        data.length == 2

        ctx.read('$..hello.Type')[0] == 'Task'
        ctx.read('$..hello.ResultPath')[0] == null
        ctx.read('$..hello.a')[0] == 'b'
    }
}
