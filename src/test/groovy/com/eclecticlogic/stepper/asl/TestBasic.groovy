package com.eclecticlogic.stepper.asl

import com.jayway.jsonpath.ReadContext

/**
 * General structure tests.
 */
class TestBasic extends AbstractStateMachineTester {

    def "test simple"() {
        given:
        ReadContext ctx = runProgram('basic.stg', 'simple')

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
        ReadContext ctx = runProgram('basic.stg', 'annotationName')

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
        ReadContext ctx = runProgram('basic.stg', 'assignmentPrimitive')

        expect:
        ctx.read('$..assignment000.Result')[0] == 5.2
        ctx.read('$..assignment000.ResultPath')[0] == '$.a'

        ctx.read('$..assignment001.Result')[0] == 10
        ctx.read('$..assignment001.ResultPath')[0] == '$.b'

        ctx.read('$..assignment002.Result')[0] == 'Hello World'
        ctx.read('$..assignment002.ResultPath')[0] == '$.c'

        ctx.read('$..assignment003.Result')[0] == true
        ctx.read('$..assignment003.ResultPath')[0] == '$.d'

        ctx.read('$..assignment004.Result')[0] == false
        ctx.read('$..assignment004.ResultPath')[0] == '$.e'
    }

}
