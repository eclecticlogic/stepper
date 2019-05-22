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

package com.eclecticlogic.stepper.install;

public class InstallConfig {

    private Lambda lambda;
    private StepFunction stepFunction;


    public StepFunction getStepFunction() {
        return stepFunction;
    }


    public void setStepFunction(StepFunction stepFunction) {
        this.stepFunction = stepFunction;
    }


    public Lambda getLambda() {
        return lambda;
    }


    public void setLambda(Lambda lambda) {
        this.lambda = lambda;
    }


    public static class StepFunction {
        private String executionRole;


        public String getExecutionRole() {
            return executionRole;
        }


        public void setExecutionRole(String executionRole) {
            this.executionRole = executionRole;
        }
    }

    public static class Lambda {
        private String executionRole;


        public String getExecutionRole() {
            return executionRole;
        }


        public void setExecutionRole(String executionRole) {
            this.executionRole = executionRole;
        }
    }
}
