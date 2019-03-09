package com.eclecticlogic.stepper;

import com.eclecticlogic.stepper.construct.ProgramConstruct;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

public class StateMachine {
    private final ProgramConstruct program;


    public StateMachine(ProgramConstruct program) {
        this.program = program;
    }


    public String getAsl() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(toJson());
    }


    public String getLambda() {
        STGroup group = new STGroupFile("stepper/template/lambda.stg");
        ST st = group.getInstanceOf("scaffolding");
        st.add("code", program.getContext().getLambdaHelper());
        return st.render();
    }


    public JsonObject toJson() {
        return program.toJson();
    }
}
