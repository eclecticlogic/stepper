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
package com.eclecticlogic.ezra;

import javax.annotation.PostConstruct;

import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.model.DomainAlreadyExistsException;
import com.amazonaws.services.simpleworkflow.model.RegisterDomainRequest;
import com.eclecticlogic.ezra.internal.job.EzraJobSpi;
import com.eclecticlogic.ezra.internal.job.JobManager;
import com.eclecticlogic.ezra.internal.poller.Poller;
import com.eclecticlogic.ezra.job.EzraJob;

/**
 * This is the main class that manages jobs, workflows and activities.
 * 
 * @author kabram.
 *
 */
public class EzraManager {

    private String domain;
    private AmazonSimpleWorkflow workflowClient;
    private Poller poller;
    private JobManager jobManager = new JobManager(this);


    public String getDomain() {
        return domain;
    }


    public void setDomain(String domain) {
        this.domain = domain;
    }


    public AmazonSimpleWorkflow getWorkflowClient() {
        return workflowClient;
    }


    public void setWorkflowClient(AmazonSimpleWorkflow workflowClient) {
        this.workflowClient = workflowClient;
    }


    /**
     * @param job Add to the manager for scheduling.
     */
    public <T> void addJob(EzraJob<T> job) {
        jobManager.add((EzraJobSpi<?>) job);
    }


    @PostConstruct
    public void start() {
        registerDomain();
        jobManager.performRegistrations();

        poller = new Poller(this);
        jobManager.start(poller);
    }


    /**
     * Registers the domain if it is not already registered.
     */
    void registerDomain() {
        RegisterDomainRequest request = new RegisterDomainRequest() //
                .withName(this.domain) //
                // TODO: Allow customization.
                .withWorkflowExecutionRetentionPeriodInDays("90");
        try {
            workflowClient.registerDomain(request);
        } catch (DomainAlreadyExistsException e) {
            // Ignore
        }
    }
}
