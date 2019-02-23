package com.eclecticlogic.stepper.etc

import org.antlr.v4.runtime.CommonToken
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.v4.runtime.tree.TerminalNodeImpl
import spock.lang.Specification

class TestStringer extends Specification {

    def "string from token"() {
        given:
        Token t1 = new CommonToken(1, "test")

        expect:
        Stringer.from(t1) == "test"
    }


    def "string from terminal node"() {
        given:
        TerminalNode t = new TerminalNodeImpl(new CommonToken(1, "test"))

        expect:
        Stringer.from(t) == "test"
    }


    def "strip quotes"() {
        expect:
        Stringer.strip('"abc"') == 'abc'
        Stringer.strip('"ab"c"') == 'ab"c'
        Stringer.strip('""ab"c"') == '"ab"c'
    }
}
