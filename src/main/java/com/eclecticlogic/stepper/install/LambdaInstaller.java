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
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.Runtime;
import software.amazon.awssdk.services.lambda.model.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class LambdaInstaller {

    private final static String LAMBDA_NAME_SUFFIX = "_stepperLambda";

    private final StateMachine machine;
    private final InstallConfig config;
    private final LambdaClient lambdaClient;


    public LambdaInstaller(StateMachine machine, InstallConfig config) {
        this.machine = machine;
        this.config = config;
        lambdaClient = LambdaClient.builder().credentialsProvider(DefaultCredentialsProvider.create()).build();
    }


    public String getLambdaName() {
        return machine.getName() + LAMBDA_NAME_SUFFIX;
    }


    boolean isLambdaExists() {
        try {
            lambdaClient.getFunction(b -> b.functionName(getLambdaName()));
            return true;
        } catch (ResourceConflictException e) {
            return false;
        }
    }


    /**
     * TODO: Check if we can update instead.
     */
    void deleteLambda() {
        lambdaClient.deleteFunction(b -> b.functionName(getLambdaName()));
    }


    FunctionCode createZippedLambdaCode() {
        Path zipFile;
        try {
            zipFile = Files.createTempFile("stepper", ".zip");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile.toFile()))) {
            ZipEntry e = new ZipEntry("index.js");
            out.putNextEntry(e);

            byte[] data = machine.getLambda().getBytes();
            out.write(data, 0, data.length);
            out.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            return FunctionCode.builder().zipFile(SdkBytes.fromByteArray(Files.readAllBytes(zipFile))).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public String install() {
        if (isLambdaExists()) {
            deleteLambda();
        }

        CreateFunctionRequest.Builder lambdaBuilder = CreateFunctionRequest.builder()
                .functionName(machine.getName() + LAMBDA_NAME_SUFFIX)
                .code(createZippedLambdaCode())
                .handler("index.handler")
                .runtime(Runtime.NODEJS10_X)
                .role(config.getLambda().getExecutionRole());
        CreateFunctionResponse response = lambdaClient.createFunction(lambdaBuilder.build());
        return response.functionArn();
    }
}
