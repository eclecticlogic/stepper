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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.eclecticlogic.ezra.EzraManager;
import com.eclecticlogic.ezra.internal.util.BackoffThrottler;

/**
 * Manages all polling.
 * 
 * @author kabram.
 *
 */
public class Poller {

    private ExecutorService executorService;
    private AtomicBoolean running = new AtomicBoolean();
    private BackoffThrottler throttler = new BackoffThrottler();
    private EzraManager manager;


    public Poller(EzraManager manager) {
        super();
        this.manager = manager;
        executorService = Executors.newFixedThreadPool(5, r -> {
            Thread t = new Thread(r, "ezra-poller");
            t.setDaemon(true);
            return t;
        });
        running.set(true);
    }


    public void start(PollingClient client) {
        executorService.submit(() -> this.pollRunner(client));
    }


    void pollRunner(PollingClient client) {
        while (running.get()) {
            try {
                throttler.throttle();
                if (running.get()) {
                    client.poll(manager.getWorkflowClient());
                }
                throttler.success();
            } catch (Exception e) {
                throttler.failure();
            }
        }
    }
}
