package com.test;

public enum StateType {
    PASS("Pass"),
    TASK("Task"),
    CHOICE("Choice"),
    WAIT("Wait"),
    SUCCEED("Succeed"),
    FAIL("Fail"),
    PARALLEL("Parallel"),
    ;


    private String name;

    StateType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
