package com.eclecticlogic.stepper.etc

import com.eclecticlogic.stepper.etc.StringHelper
import org.antlr.v4.runtime.CommonToken
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.v4.runtime.tree.TerminalNodeImpl
import spock.lang.Specification

class TestStringHelper extends Specification {

    def "string from token"() {
        given:
        Token t1 = new CommonToken(1, "test")

        expect:
        StringHelper.from(t1) == "test"
    }


    def "string from terminal node"() {
        given:
        TerminalNode t = new TerminalNodeImpl(new CommonToken(1, "test"))

        expect:
        StringHelper.from(t) == "test"
    }


    def "strip quotes"() {
        expect:
        StringHelper.strip('"abc"') == 'abc'
        StringHelper.strip('"ab"c"') == 'ab"c'
        StringHelper.strip('""ab"c"') == '"ab"c'
    }
}
