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
package com.eclecticlogic.ezra.internal.job;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.model.ChildPolicy;
import com.amazonaws.services.simpleworkflow.model.DecisionTask;
import com.amazonaws.services.simpleworkflow.model.HistoryEvent;
import com.amazonaws.services.simpleworkflow.model.PollForDecisionTaskRequest;
import com.amazonaws.services.simpleworkflow.model.RegisterActivityTypeRequest;
import com.amazonaws.services.simpleworkflow.model.RegisterWorkflowTypeRequest;
import com.amazonaws.services.simpleworkflow.model.StartWorkflowExecutionRequest;
import com.amazonaws.services.simpleworkflow.model.TaskList;
import com.amazonaws.services.simpleworkflow.model.TypeAlreadyExistsException;
import com.amazonaws.services.simpleworkflow.model.WorkflowExecutionAlreadyStartedException;
import com.amazonaws.services.simpleworkflow.model.WorkflowType;
import com.eclecticlogic.ezra.Constants;
import com.eclecticlogic.ezra.EzraManager;
import com.eclecticlogic.ezra.internal.poller.Poller;
import com.eclecticlogic.ezra.internal.poller.PollingClient;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * This class manages ezra-jobs and the workflows/activity definitions for them. This class should not be directly 
 * used. Client code should only interact with EzraManager or its derivatives.
 * 
 * @author kabram.
 *
 */
public class JobManager implements PollingClient {

    private List<EzraJobSpi<?>> jobs = Lists.newArrayList();
    private EzraManager manager;


    public JobManager(EzraManager manager) {
        super();
        this.manager = manager;
    }


    public void add(EzraJobSpi<?> job) {
        jobs.add(job);
    }


    public void performRegistrations() {
        if (!jobs.isEmpty()) {
            performWorkflowRegistration();
            performActivityRegistrations();
        }
    }


    private TaskList getWorkflowTaskList() {
        return new TaskList().withName(Constants.Job.WORKFLOW_TASK_LIST);
    }


    private TaskList getActivityTaskList() {
        return new TaskList().withName(Constants.Job.ACTIVITY_TASK_LIST);
    }


    private void performWorkflowRegistration() {
        int oneYearSeconds = 366 * 24 * 3600; // Account for leap
        RegisterWorkflowTypeRequest request = new RegisterWorkflowTypeRequest()
                .withDefaultExecutionStartToCloseTimeout(Integer.toString(oneYearSeconds))
                .withDefaultTaskList(getWorkflowTaskList())
                .withDefaultTaskStartToCloseTimeout(Constants.Job.WORKFLOW_DEFAULT_TASK_START_TO_CLOSE)
                .withDescription(Constants.Job.WORKFLOW_DESCRIPTION) //
                .withDomain(manager.getDomain()) //
                .withName(Constants.Job.WORKFLOW_NAME) //
                .withVersion(Constants.Job.WORKFLOW_VERSION);

        try {
            manager.getWorkflowClient().registerWorkflowType(request);
        } catch (TypeAlreadyExistsException e) {
            // Ignore
        }
    }


    /**
     * Register an activity, one for each job.
     */
    private void performActivityRegistrations() {
        for (EzraJobSpi<?> job : jobs) {
            RegisterActivityTypeRequest request = new RegisterActivityTypeRequest() //
                    .withDefaultTaskList(getActivityTaskList()) //
                    .withDefaultTaskScheduleToStartTimeout(Constants.Job.ACTIVITY_DEFAULT_SCHEDULE_TO_START) //
                    .withDefaultTaskStartToCloseTimeout(Constants.Job.ACTIVITY_DEFAULT_TASK_START_TO_CLOSE) //
                    .withDomain(manager.getDomain()) //
                    .withName(Constants.Job.ACTIVITY_NAME_PREFIX + getJobName(job)) //
                    .withVersion(Constants.Job.ACTIVITY_VERSION);

            try {
                manager.getWorkflowClient().registerActivityType(request);
            } catch (TypeAlreadyExistsException e) {
                // Ignore
            }
        }
    }


    private String getJobName(EzraJobSpi<?> job) {
        StringBuilder builder = new StringBuilder();
        builder.append(job.getClass().getName());
        builder.append(".");
        builder.append(job.getMethodCall().getMethod().getName());
        return builder.toString();
    }


    public void start(Poller poller) {
        if (!jobs.isEmpty()) {
            // Start the workflow. Other instances may also attempt to start it resulting in failure that should be
            // handled.
            StartWorkflowExecutionRequest request = new StartWorkflowExecutionRequest() //
                    .withDomain(manager.getDomain()) //
                    .withChildPolicy(ChildPolicy.TERMINATE) //
                    .withWorkflowId(Constants.Job.WORKFLOW_ID) //
                    .withWorkflowType(new WorkflowType().withName(Constants.Job.WORKFLOW_NAME) //
                            .withVersion(Constants.Job.WORKFLOW_VERSION));

            try {
                manager.getWorkflowClient().startWorkflowExecution(request);
            } catch (WorkflowExecutionAlreadyStartedException e) {
                // Ignore
            }
            poller.start(this);
        }
    }


    @Override
    public void poll(AmazonSimpleWorkflow workflow) {
        PollForDecisionTaskRequest request = new PollForDecisionTaskRequest() //
                .withDomain(manager.getDomain()) //
                .withIdentity(ManagementFactory.getRuntimeMXBean().getName()) //
                .withTaskList(getWorkflowTaskList());

        DecisionTask task = workflow.pollForDecisionTask(request);
        if (task != null) {
//            List<Decision> decisions = Lists.newArrayList();
//            for (int i = 0; i < 5; i++) {
//                StartTimerDecisionAttributes attributes = new StartTimerDecisionAttributes() //
//                        .withStartToFireTimeout("30") //
//                        .withTimerId(SwfIdGenerator.getId());
//                Decision decision = new Decision() //
//                        .withDecisionType(DecisionType.StartTimer) //
//                        .withStartTimerDecisionAttributes(attributes);
//                decisions.add(decision);
//            }
//
//            RespondDecisionTaskCompletedRequest completion = new RespondDecisionTaskCompletedRequest() //
//                    .withDecisions(decisions) //
//                    .withTaskToken(task.getTaskToken());
//
//            workflow.respondDecisionTaskCompleted(completion);

             System.out.println(task.getPreviousStartedEventId());
             System.out.println(task.getStartedEventId());
             System.out.println("----");
             for (HistoryEvent event : task.getEvents()) {
             System.out.println(event.getEventType());
             System.out.println(event.getEventId());
             }
        }
    }


    /**
     * @return Map of jobs to be scheduled and the times at which they need to be scheduled. It returns the earliest 
     * task and all tasks within a 10-minute window from the earliest task.
     */
    public Multimap<ZonedDateTime, EzraJobSpi<?>> getNextJobExecutionTime() {
        ZonedDateTime closest = ZonedDateTime.now().plusYears(2);
        
        Multimap<ZonedDateTime, EzraJobSpi<?>> map = HashMultimap.create();
        for (EzraJobSpi<?> job : jobs) {
            for (ZonedDateTime zdt : job.getExecutions(10)) {
                map.put(zdt, job);
                if (zdt.isBefore(closest)) {
                    closest = zdt;
                }
            }
        }
        
        // Keep only the earliest and those that fall within a 10 minute window.
        for (ZonedDateTime zdt : map.keySet()) {
            if (Duration.between(closest, zdt).toMinutes() > 10) {
                map.removeAll(zdt);
            }
        }
        
        return map;
    }

}
