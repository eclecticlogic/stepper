package com.eclecticlogic.stepper.state;

import com.google.common.collect.Maps;

import java.util.Map;

public class Retry {

    private final Map<String, Object> attributes = Maps.newHashMap();


    public void put(String key, Object value) {
        attributes.put(key, value);
    }


    public void setup(Task task) {
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String key = entry.getKey();
            task.captureAttribute(key);
            Object v = entry.getValue();
            if (v instanceof Number) {
                task.setProperty((Number) v);
            } else if (v instanceof Boolean) {
                task.setProperty((Boolean) v);
            } else if ("ErrorEquals".equals(key)) {
                task.handleArray(() -> task.setProperty(v.toString()));
            } else {
                task.setProperty(v.toString());
            }
        }
    }
}
