package com.test;

import com.eclecticlogic.ezra.antlr.EzraLexer;
import com.eclecticlogic.ezra.antlr.EzraParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Test {

    public static void main(String[] args) throws Exception {
        CharStream input = CharStreams.fromFileName("c:/users/kabram/Downloads/test.txt");
        EzraLexer lexer = new EzraLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        EzraParser parser = new EzraParser(tokens);

        ParseTree tree = parser.program();
        ParseTreeWalker walker = new ParseTreeWalker();
        StepperCoreListener listener = new StepperCoreListener();
        walker.walk(listener, tree);
    }
}
