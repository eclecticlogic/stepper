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

package com.eclecticlogic.stepper;

import org.yaml.snakeyaml.Yaml;
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
    private final Yaml yaml;
    private final LambdaClient lambdaClient;


    public LambdaInstaller(StateMachine machine, Yaml yaml) {
        this.machine = machine;
        this.yaml = yaml;
        lambdaClient = LambdaClient.builder().credentialsProvider(DefaultCredentialsProvider.create()).build();
    }


    boolean isLambdaExists() {
        try {
            lambdaClient.getFunction(b -> b.functionName(machine.getName() + LAMBDA_NAME_SUFFIX));
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }


    void deleteLambda() {

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
                .role("arn:aws:iam::account-id:role/lambda_basic_execution");
        CreateFunctionResponse response = lambdaClient.createFunction(lambdaBuilder.build());
        return response.functionArn();
    }
}
