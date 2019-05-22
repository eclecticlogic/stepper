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

import com.eclecticlogic.stepper.StateMachine;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.CreateStateMachineResponse;
import software.amazon.awssdk.services.sfn.model.ListStateMachinesResponse;
import software.amazon.awssdk.services.sfn.model.StateMachineListItem;

import java.util.Optional;

import static com.eclecticlogic.stepper.etc.Constants.LAMBDA_ARN_PLACEHOLDER;

public class StepFunctionInstaller {

    private final StateMachine machine;
    private final InstallConfig config;
    private final SfnClient client;


    public StepFunctionInstaller(StateMachine machine, InstallConfig config) {
        this.machine = machine;
        this.config = config;
        AwsCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();
        client = SfnClient.builder().credentialsProvider(DefaultCredentialsProvider.create()).build();
    }


    Optional<StateMachineListItem> getStateMachineMatch(ListStateMachinesResponse response) {
        return response.stateMachines()
                .stream()
                .filter(sm -> machine.getName().equals(sm.name()))
                .findFirst();
    }


    public String install(String lambdaArn) {
        ListStateMachinesResponse response = client.listStateMachines();
        Optional<StateMachineListItem> item = getStateMachineMatch(response);

        while (!item.isPresent() && response.nextToken() != null) {
            String token = response.nextToken();
            response = client.listStateMachines(b -> b.nextToken(token));
            item = getStateMachineMatch(response);
        }

        if (item.isPresent()) {
            String arn = item.get().stateMachineArn();
            client.updateStateMachine(b -> b.stateMachineArn(arn)
                    .definition(machine.getAsl().replaceAll(LAMBDA_ARN_PLACEHOLDER, lambdaArn)));
            return arn;
        } else {
            CreateStateMachineResponse cr = client.createStateMachine(b -> b.name(machine.getName())
                    .definition(machine.getAsl().replaceAll(LAMBDA_ARN_PLACEHOLDER, lambdaArn))
                    .roleArn(config.getStepFunction().getExecutionRole()));
            return cr.stateMachineArn();
        }
    }

}
