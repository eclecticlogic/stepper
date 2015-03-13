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

import java.time.ZonedDateTime;
import java.util.List;

import com.eclecticlogic.ezra.internal.proxy.MethodCall;
import com.eclecticlogic.ezra.job.EzraJob;


/**
 * @author kabram.
 *
 */
public interface EzraJobSpi<T> extends EzraJob<T> {

    /**
     * @return Method call associated with this job.
     */
    MethodCall getMethodCall();

    /**
     * @return Scheduling pattern.
     */
    String getSchedule();
    
    
    /**
     * @param n  
     * @return Next n execution times.
     */
    List<ZonedDateTime> getExecutions(int n); 
}
