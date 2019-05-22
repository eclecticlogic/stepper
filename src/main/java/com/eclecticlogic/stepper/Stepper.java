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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.eclecticlogic.stepper.antlr.StepperLexer;
import com.eclecticlogic.stepper.antlr.StepperParser;
import com.eclecticlogic.stepper.install.InstallConfig;
import com.eclecticlogic.stepper.install.LambdaInstaller;
import com.eclecticlogic.stepper.install.StepFunctionInstaller;
import com.eclecticlogic.stepper.visitor.StepperVisitor;
import com.google.common.collect.Lists;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.CreateStateMachineRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Stepper {

    @Parameter(names = {"-i", "--install"}, description = "Compiles and installs Step Function ASL and Lambda using information provided in yaml file.")
    String install;

    @Parameter(description = "Stepper program file", required = true)
    String stepperProgramFile;


    void run() throws IOException {
        CharStream input = CharStreams.fromFileName(stepperProgramFile);
        StepperLexer lexer = new StepperLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        StepperParser parser = new StepperParser(tokens);

        StepperVisitor visitor = new StepperVisitor();
        StateMachine machine = visitor.visitProgram(parser.program());

        if (install == null) {
            Path parent = Paths.get(stepperProgramFile).getParent();
            Path asl = parent.resolve(machine.getName() + ".asl");
            Path lambda = parent.resolve(machine.getName() + ".js");
            Files.write(asl, Lists.newArrayList(machine.getAsl()));
            Files.write(lambda, Lists.newArrayList(machine.getLambda()));
            System.out.println("Wrote asl to " + asl + ", lambda to " + lambda);
        } else {
            Yaml yaml = new Yaml(new Constructor(InstallConfig.class));
            String yamlData = String.join("\n", Files.readAllLines(Paths.get(install)));
            InstallConfig installConfig = yaml.load(yamlData);

            LambdaInstaller lambdaInstaller = new LambdaInstaller(machine, installConfig);
            String lambdaArn = lambdaInstaller.install();

            StepFunctionInstaller sfInstaller = new StepFunctionInstaller(machine, installConfig);
            String arn = sfInstaller.install(lambdaArn);
            System.out.println("Lambda installed. Arn = " + arn);
        }
    }


    public static void main(String[] args) {
        Stepper stepper = new Stepper();
        JCommander commander = JCommander
                .newBuilder()
                .addObject(stepper)
                .build();
        commander.setProgramName("stepper");
        commander.parse(args);
        try {
            stepper.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
