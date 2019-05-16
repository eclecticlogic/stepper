/*
Copyright 2015-2019 KR Abram

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.eclecticlogic.stepper.asl

import com.jayway.jsonpath.ReadContext


class TestTryCatch extends AbstractStateMachineTester {

    def "test trycatch none affected"() {
        given:
        TestOutput output = runWithLambda('trycatch.stg', 'simple0')
        ReadContext ctx = output.ctx

        when:
        Closure v = { name, param ->
            return ctx.read('$.States.' + name + '.' + param)
        }

        then:
        Object[] data = ctx.read('$.States.*')
        data.length == 5

        ctx.read('$.StartAt') == 'Simple000'

        with('Simple000') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 5
            v(it, 'ResultPath') == '$.a'
            v(it, 'Next') == 'Simple001'
        }

        with('Simple001') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 3
            v(it, 'ResultPath') == '$.b'
            v(it, 'Next') == 'Simple.Success'
        }

        with('Simple002') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 3
            v(it, 'ResultPath') == '$.c'
            v(it, 'Next') == 'Simple003'
        }

        with('Simple003') {
            v(it, 'Type') == 'Fail'
        }

        v("['Simple.Success']", 'Type') == 'Succeed'
    }


    def "test try single catch"() {
        given:
        TestOutput output = runWithLambda('trycatch.stg', 'simple1')
        ReadContext ctx = output.ctx

        when:
        Closure v = { name, param ->
            return ctx.read('$.States.' + name + '.' + param)
        }

        then:
        Object[] data = ctx.read('$.States.*')
        data.length == 5

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
            v(it, 'ResultPath') == '$.b'
            v(it, 'Catch[0].ErrorEquals[0]') == 'e1'
            v(it, 'Catch[0].ErrorEquals[1]') == 'e2'
            v(it, 'Catch[0].ResultPath') == '$.errorInfo'
            v(it, 'Catch[0].Next') == 'Simple002'
            v(it, 'Next') == 'Simple.Success'
        }
        output.lambda.contains('const response = 3*a;')

        with('Simple002') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 3
            v(it, 'ResultPath') == '$.c'
            v(it, 'Next') == 'Simple003'
        }

        with('Simple003') {
            v(it, 'Type') == 'Fail'
        }

        v("['Simple.Success']", 'Type') == 'Succeed'
    }


    def "test try double catch"() {
        given:
        TestOutput output = runWithLambda('trycatch.stg', 'simple2')
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
            v(it, 'Next') == 'Simple001'
        }

        with('Simple001') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'Simple001'
            v(it, "Parameters.['a.\$']") == '$.a'
            v(it, 'ResultPath') == '$.b'
            v(it, 'Catch[0].ErrorEquals[0]') == 'e1'
            v(it, 'Catch[0].ErrorEquals[1]') == 'e2'
            v(it, 'Catch[0].ResultPath') == '$.errorInfo'
            v(it, 'Catch[0].Next') == 'Simple002'
            v(it, 'Catch[1].ErrorEquals[0]') == 'e3'
            v(it, 'Catch[1].ErrorEquals[1]') == 'e4'
            v(it, 'Catch[1].Next') == 'Simple004'
            v(it, 'Next') == 'g1'
        }

        with('Simple002') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 3
            v(it, 'ResultPath') == '$.c'
            v(it, 'Next') == 'Simple003'
        }

        with('Simple003') {
            v(it, 'Type') == 'Pass'
            v(it, 'Next') == 'g1'
        }

        with('Simple004') {
            v(it, 'Type') == 'Fail'
        }

        with('g1') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 10
            v(it, 'ResultPath') == '$.d'
            v(it, 'Next') == 'Simple.Success'
        }

        v("['Simple.Success']", 'Type') == 'Succeed'
    }


    def "test try catch nested"() {
        given:
        TestOutput output = runWithLambda('trycatch.stg', 'nested')
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
            v(it, 'Next') == 'Nested001'
        }

        with('Nested001') {
            v(it, 'Type') == 'Task'
            v(it, 'Parameters.cmd__sm') == 'Nested001'
            v(it, "Parameters.['a.\$']") == '$.a'
            v(it, 'ResultPath') == '$.b'
            v(it, 'Catch[0].ErrorEquals[0]') == 'e1'
            v(it, 'Catch[0].ErrorEquals[1]') == 'e2'
            v(it, 'Catch[0].ResultPath') == '$.errorInfo'
            v(it, 'Catch[0].Next') == 'Nested002'
            v(it, 'Catch[1].ErrorEquals[0]') == 'e0'
            v(it, 'Catch[1].Next') == 'Nested004'
            v(it, 'Next') == 'g1'
        }

        with('Nested002') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 3
            v(it, 'ResultPath') == '$.c'
            v(it, 'Next') == 'Nested003'
        }

        with('Nested003') {
            v(it, 'Type') == 'Pass'
            v(it, 'Next') == 'g1'
        }

        with('g1') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 10
            v(it, 'ResultPath') == '$.d'
            v(it, 'Next') == 'Nested006'
        }

        with('Nested004') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 10
            v(it, 'ResultPath') == '$.x'
            v(it, 'Next') == 'Nested005'
        }
        v('Nested005', 'Type') == 'Fail'

        with('Nested006') {
            v(it, 'Type') == 'Pass'
            v(it, 'Result') == 5
            v(it, 'ResultPath') == '$.y'
            v(it, 'Next') == 'Nested.Success'
        }

        v("['Nested.Success']", 'Type') == 'Succeed'
    }
}