package com.eclecticlogic.stepper.construct

import com.eclecticlogic.stepper.state.Task
import spock.lang.Specification

class TestIfConstruct extends Specification {

    def "wiring choice next state if no else"() {
        given:
        IfConstruct ic = new IfConstruct()
        StateConstruct c1 = new StateConstruct(new Task())
        ic.symbols = ['a', 'b', 'c']
        ic.firstIf = c1

        when:
        ic.setupCondition()

        then:
        ic.choice.objIf.get('Next').asString == c1.state.name
        ic.choice.objElse.get('Next').asString == 'none'
    }


    def "wiring choice next state if and else"() {
        given:
        IfConstruct ic = new IfConstruct()
        StateConstruct c1 = new StateConstruct(new Task())
        StateConstruct c2 = new StateConstruct(new Task())
        ic.symbols = ['a', 'b', 'c']
        ic.firstIf = c1
        ic.firstElse = c2

        when:
        ic.setupCondition()

        then:
        ic.choice.objIf.get('Next').asString == c1.state.name
        ic.choice.objElse.get('Next').asString == c2.state.name
    }


    def "set next state no else"() {
        given:
        IfConstruct ic = new IfConstruct()
        StateConstruct c1 = new StateConstruct(new Task())
        ic.symbols = ['a', 'b', 'c']
        ic.firstIf = c1

        when:
        ic.setupCondition()
        ic.nextStateName = 'abc'

        then:
        c1.state.jsonRepresentation.contains('"Next": "abc"')
        ic.choice.objElse.get('Next').asString == 'abc'
    }


    def "set next state with else"() {
        given:
        IfConstruct ic = new IfConstruct()
        StateConstruct c1 = new StateConstruct(new Task())
        StateConstruct c2 = new StateConstruct(new Task())
        ic.symbols = ['a', 'b', 'c']
        ic.firstIf = c1
        ic.firstElse = c2

        when:
        ic.setupCondition()
        ic.nextStateName = 'abc'

        then:
        c1.state.jsonRepresentation.contains('"Next": "abc"')
        c2.state.jsonRepresentation.contains('"Next": "abc"')
        ic.choice.objElse.get('Next').asString != 'abc'
    }
}
