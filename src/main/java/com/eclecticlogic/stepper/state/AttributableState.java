package com.eclecticlogic.stepper.state;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Stack;

public class AttributableState extends AbstractState {

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

}

