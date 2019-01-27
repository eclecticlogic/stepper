package com.eclecticlogic.ezra.machine.construct;

import com.eclecticlogic.ezra.DereferenceListener;

import java.util.ArrayList;
import java.util.List;

public class IfConstruct extends CompositeStepConstruct implements DereferenceListener {

    private String conditionText;

    private final List<String> conditionDeferences = new ArrayList<>();

    public void setCondition(String condition) {
        conditionText = condition;
    }

    @Override
    public void onDeference(String value) {
        conditionDeferences.add(value);
    }
}
