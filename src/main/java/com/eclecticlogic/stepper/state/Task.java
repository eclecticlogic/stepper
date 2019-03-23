package com.eclecticlogic.stepper.state;


import java.util.List;

public class Task extends AttributableState {

    public Task() {
        this(null);
    }


    public Task(String stateName) {
        super(stateName);
        setType(StateType.TASK);
    }


    public void setRetries(List<Retry> retries) {
        if (!retries.isEmpty()) {
            captureAttribute("Retry");
            handleArray(() -> {
                for (Retry retry : retries) {
                    handleObject(() -> retry.setup(this));
                }
            });
        }
    }
}
