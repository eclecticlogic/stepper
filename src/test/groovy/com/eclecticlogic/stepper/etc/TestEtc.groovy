package com.eclecticlogic.stepper.etc


import org.antlr.v4.runtime.CommonToken
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.v4.runtime.tree.TerminalNodeImpl
import spock.lang.Specification

class TestEtc extends Specification {

    def "string from token"() {
        given:
        Token t1 = new CommonToken(1, "test")

        expect:
        Etc.from(t1) == "test"
    }


    def "string from terminal node"() {
        given:
        TerminalNode t = new TerminalNodeImpl(new CommonToken(1, "test"))

        expect:
        Etc.from(t) == "test"
    }


    def "strip quotes"() {
        expect:
        Etc.strip('"abc"') == 'abc'
        Etc.strip('"ab"c"') == 'ab"c'
        Etc.strip('""ab"c"') == '"ab"c'
    }
}
