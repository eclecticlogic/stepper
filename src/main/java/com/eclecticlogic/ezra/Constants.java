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


/**
 * @author kabram.
 *
 */
public interface Constants {

    public interface Job {
        String WORKFLOW_TASK_LIST = "ezraJobWorkflowTaskList";
        String WORKFLOW_DEFAULT_TASK_START_TO_CLOSE = "60"; // Decisions should be made much faster ...
        String WORKFLOW_DESCRIPTION = "Workflow for Ezra jobs";
        String WORKFLOW_NAME = "EzraJobWorkflow";
        String WORKFLOW_VERSION = "0.0.2";
        String WORKFLOW_ID = "ezraJobWorkflow";
        
        String ACTIVITY_TASK_LIST = "ezraJobActivityTaskList";
        String ACTIVITY_DEFAULT_SCHEDULE_TO_START = "120"; // Expect a task to be picked up in 2 minutes.
        String ACTIVITY_DEFAULT_TASK_START_TO_CLOSE = "21600"; // Default activity timeout is 6 hours.
        String ACTIVITY_NAME_PREFIX = "ezraJob_";
        String ACTIVITY_VERSION = "0.0.1";
        
    }
}
