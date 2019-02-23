package com.eclecticlogic.stepper.construct

import com.eclecticlogic.stepper.etc.WeaveContext
import com.eclecticlogic.stepper.state.State
import spock.lang.Specification

class TestConstruct extends Specification {

    class MockConstruct extends Construct {

        @Override
        List<State> getStates() {
            return []
        }

        @Override
        protected String getFirstStateName() {
            return null
        }

        @Override
        protected void setNextStateName(String name) {

        }

        @Override
        void weave(WeaveContext context) {

        }
    }

    def "get last in chain"() {
        given:
        Construct a = new MockConstruct()
        Construct b = new MockConstruct()
        Construct c = new MockConstruct()
        a.next = b
        b.next = c

        expect:
        a.getLastInChain(a) == c
    }
}
