package com.eclecticlogic.stepper.state;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Stack;

public class AttributableState extends AbstractState {

    private String currentAttribute;
    private JsonArray array;
    private final Stack<JsonObject> currentStack = new Stack<>();

    AttributableState() {
        currentStack.push(json);
    }

    public void captureAttribute(String attr) {
        currentAttribute = attr;
    }


    public void setProperty(boolean value) {
        if (array == null) {
            currentStack.peek().addProperty(currentAttribute, value);
        } else {
            array.add(value);
        }
    }


    public void setProperty(String value) {
        if (array == null) {
            currentStack.peek().addProperty(currentAttribute, value);
        } else {
            array.add(value);
        }
    }


    public void setProperty(Number value) {
        if (array == null) {
            currentStack.peek().addProperty(currentAttribute, value);
        } else {
            array.add(value);
        }
    }


    public void handleObject(Runnable closure) {
        JsonObject temp = new JsonObject();
        currentStack.peek().add(currentAttribute, temp);
        currentStack.push(temp);
        closure.run();
        currentStack.pop();
    }


    public void handleArray(Runnable closure) {
        array = new JsonArray();
        currentStack.peek().add(currentAttribute, array);
        closure.run();
        array = null;
    }
}

