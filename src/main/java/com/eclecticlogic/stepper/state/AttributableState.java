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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Stack;

public class AttributableState extends AbstractState {

    public static final String ATTRIB_CATCH = "Catch";

    private String currentAttribute;
    private JsonElement current;
    private final Stack<JsonElement> currentStack = new Stack<>();


    AttributableState() {
        this(null);
    }


    AttributableState(String stateName) {
        super(stateName);
        current = json;
    }


    public void captureAttribute(String attr) {
        currentAttribute = attr;
    }


    public void setProperty(boolean value) {
        if (current instanceof JsonObject) {
            ((JsonObject)current).addProperty(currentAttribute, value);
        } else {
            ((JsonArray)current).add(value);
        }
    }


    public void setProperty(String value) {
        if (current instanceof JsonObject) {
            ((JsonObject)current).addProperty(currentAttribute, value);
        } else {
            ((JsonArray)current).add(value);
        }
    }


    public void setProperty(Number value) {
        if (current instanceof JsonObject) {
            ((JsonObject)current).addProperty(currentAttribute, value);
        } else {
            ((JsonArray)current).add(value);
        }
    }


    public void setObject(JsonObject object) {
        if (current instanceof JsonObject) {
            ((JsonObject) current).add(currentAttribute, object);
        } else {
            ((JsonArray) current).add(object);
        }
    }


    public JsonObject handleObject(Runnable closure) {
        JsonObject obj = new JsonObject();
        if (current instanceof JsonObject) {
            ((JsonObject)current).add(currentAttribute, obj);
        } else {
            ((JsonArray)current).add(obj);
        }
        currentStack.push(current);
        current = obj;
        closure.run();
        current = currentStack.pop();
        return obj;
    }


    public JsonArray handleArray(Runnable closure) {
        JsonArray array = new JsonArray();
        if (current instanceof JsonObject) {
            ((JsonObject)current).add(currentAttribute, array);
        } else {
            ((JsonArray)current).add(array);
        }
        currentStack.push(current);
        current = array;
        closure.run();
        current = currentStack.pop();
        return array;
    }


    public void addCatch(Runnable closure) {
        if (json.get(ATTRIB_CATCH) == null) {
            json.add(ATTRIB_CATCH, new JsonArray());
        }
        JsonArray array = (JsonArray) json.get(ATTRIB_CATCH);
        currentStack.push(current);
        current = array;
        closure.run();
        current = currentStack.pop();
    }


}

