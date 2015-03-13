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
package com.eclecticlogic.ezra.internal.poller;

import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;


/**
 * @author kabram.
 *
 */
public interface PollingClient {

    /**
     * Implementations should invoke the blocking polling call here.
     * @param workflow
     */
    void poll(AmazonSimpleWorkflow workflow);
}
