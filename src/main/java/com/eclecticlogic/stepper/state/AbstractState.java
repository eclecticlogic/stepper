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

package com.eclecticlogic.stepper.state;

import com.eclecticlogic.stepper.state.observer.StateObserver;
import com.google.gson.JsonObject;

public abstract class AbstractState implements State {

    private String stateName;

    JsonObject json = new JsonObject();


    AbstractState() {
        this(null);
    }


    AbstractState(String stateName) {
        this.stateName = stateName == null ? NameProvider.getName() : stateName;
        StateObserver.event(this);
    }


    @Override
    public String getName() {
        return stateName;
    }


    @Override
    public JsonObject toJson() {
        return json;
    }


    void setType(StateType type) {
        json.addProperty("Type", type.getName());
    }


    public void setupLambdaHelper() {
        json.addProperty("Resource", "@@@lambda_helper_arn@@@");
    }


    public void setResultPath(String value) {
        json.addProperty("ResultPath", value);
    }


    public void setNextState(String value) {
        json.addProperty("Next", value);
    }
}
