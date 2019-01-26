package com.eclecticlogic.ezra.machine;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Stack;

public class AttributableStepState extends AbstractStepState {

    private String currentAttribute;
    JsonArray array;
    Stack<JsonObject> currentStack = new Stack<>();

    public AttributableStepState() {
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


    public void startObject() {
        JsonObject temp = new JsonObject();
        currentStack.peek().add(currentAttribute, temp);
        currentStack.push(temp);
    }


    public void stopObject() {
        currentStack.pop();
    }


    public void startArray() {
        array = new JsonArray();
        currentStack.peek().add(currentAttribute, array);
    }

    public void endArray() {
        array = null;
    }
}
