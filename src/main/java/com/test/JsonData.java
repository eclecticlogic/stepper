package com.test;

import java.util.*;

public class JsonData {

    Map<String, Object> jsonData = new HashMap<>();

    String currentAttribute;
    List<Object> listEntry;
    Stack<JsonData> currentStack = new Stack<>();

    public JsonData() {
        currentStack.push(this);
    }

    public void captureQuotedAttribute(String attr) {
        captureAttribute("\"" + attr + "\"");
    }


    public void captureAttribute(String attr) {
        currentAttribute = attr;
    }

    public void setQuotedAttributeValue(String value) {
        setAttributeValue("\"" + value + "\"");
    }
    public void setAttributeValue(Object value) {
        if (listEntry == null) {
            currentStack.peek().jsonData.put(currentAttribute, value);
        } else {
            listEntry.add(value);
        }
    }

    public void startArray() {
        listEntry = new ArrayList<>();
        currentStack.peek().jsonData.put(currentAttribute, listEntry);
    }

    public void endArray() {
        listEntry = null;
    }

    public void startObject() {
        JsonData temp = new JsonData();
        currentStack.peek().jsonData.put(currentAttribute, temp);
        currentStack.push(temp);
    }

    public void stopObject() {
        currentStack.pop();
    }

    @Override
    public String toString() {
        if (jsonData.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, Object> entry : jsonData.entrySet()) {
            builder.append(",");
            builder.append(entry.getKey()).append(": ");
            builder.append(getAttributeValue(entry.getValue())).append("\n");
        }
        return "{" + builder.substring(1) + "}";
    }


    String getAttributeValue(Object value) {
        StringBuilder builder = new StringBuilder();
        if (value instanceof List) {
            builder.append("[");
            StringBuilder listBuilder = new StringBuilder();
            for (Object v : (List) value) {
                listBuilder.append(",");
                listBuilder.append(getAttributeValue(v));
            }
            builder.append(listBuilder.substring(1));
            builder.append("]");
        } else if (value instanceof JsonData) {
            builder.append(value.toString());
        } else {
            builder.append(value.toString());
        }
        return builder.toString();
    }



}
