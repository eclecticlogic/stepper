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


    public String getName() {
        return program.getProgramName();
    }


    public String getAsl() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(toJson());
    }


    public boolean isLambdaRequired() {
        return !program.getContext().getLambdaHelper().getBranches().isEmpty();
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
