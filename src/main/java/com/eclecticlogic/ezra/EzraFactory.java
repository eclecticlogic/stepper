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

import com.eclecticlogic.ezra.internal.job.EzraJobImpl;
import com.eclecticlogic.ezra.job.EzraJob;

/**
 * @author kabram.
 *
 */
public class EzraFactory {

    /**
     * @return Ezra manager instance.
     */
    public static EzraManager createManager() {
        return new EzraManager();
    }


    /**
     * @param instance Create a job based on the given instance.
     * @return Configuration interface to specify the job method, schedule etc.
     */
    public static <T> EzraJob<T> createJob(T instance) {
        return new EzraJobImpl<T>(instance);
    }
}
