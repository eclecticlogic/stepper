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
package com.eclecticlogic.ezra.job;

import java.util.function.Consumer;

/**
 * Core interface to configure an EzraJob.
 * @author kabram.
 *
 */
public interface EzraJob<T> {

    /**
     * @param executor The instance on which the method needs to be executed.
     * @return Self reference for fluent interface.
     */
    public EzraJob<T> executing(Consumer<T> executor);


    /**
     * @param cronPattern Cron schedule according to the specification here: 
     * http://www.sauronsoftware.it/projects/cron4j/api/index.html?it/sauronsoftware/cron4j/CronParser.html
     * @return Self reference for fluent interface.
     */
    public EzraJob<T> withSchedule(String cronPattern);


    public EzraJob<T> withRetryPolicy();


    public EzraJob<T> withRecoveryPolicy();
}
