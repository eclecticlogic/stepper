/**
 * Copyright (c) 2014-2015 Eclectic Logic LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.eclecticlogic.ezra.prototype;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;
import com.eclecticlogic.ezra.EzraFactory;
import com.eclecticlogic.ezra.EzraManager;
import com.eclecticlogic.ezra.job.EzraJob;


/**
 * @author kabram.
 *
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {
        EzraJob<MyWorkflow> job = EzraFactory.createJob(new MyWorkflow()) //
                .withSchedule("") //
                .executing(m -> m.runThis());
        
        EzraManager manager = EzraFactory.createManager();
        manager.setDomain("test1");
        AmazonSimpleWorkflow client = new AmazonSimpleWorkflowClient(new AWSCredentialsProviderChain( 
                new ClasspathPropertiesFileCredentialsProvider("aws.credentials.properties")));
        manager.setWorkflowClient(client);
        manager.addJob(job);
        manager.start();
        
        Thread.sleep(3000_000);
    }
}
