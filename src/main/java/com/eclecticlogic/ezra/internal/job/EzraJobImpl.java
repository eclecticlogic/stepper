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

import it.sauronsoftware.cron4j.Predictor;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.eclecticlogic.ezra.internal.proxy.MethodCall;
import com.eclecticlogic.ezra.internal.proxy.Proxy;
import com.eclecticlogic.ezra.internal.proxy.ProxyCreator;

/**
 * @author kabram.
 *
 */
public class EzraJobImpl<T> implements EzraJobSpi<T> {

    private T instance;
    private MethodCall methodCall;
    private String cronPattern;


    /**
     * @param instance The object on which the method is to be executed.
     */
    public EzraJobImpl(T instance) {
        this.instance = instance;
    }


    /**
     * @param executor The instance on which the method needs to be executed.
     * @return Self reference for fluent interface.
     */
    @Override
    @SuppressWarnings("unchecked")
    public EzraJobImpl<T> executing(Consumer<T> executor) {
        Proxy<T> proxy = (Proxy<T>) ProxyCreator.create(instance.getClass());
        executor.accept(proxy.getProxy());
        if (proxy.getMethodCalls().size() != 1) {
            throw new IllegalStateException("Exactly one method (the target of the job) should be invoked in the "
                    + "executing consumer.");
        }
        methodCall = proxy.getMethodCalls().get(0);
        return this;
    }


    /**
     * @param cronPattern Cron schedule according to the specification here: 
     * http://www.sauronsoftware.it/projects/cron4j/api/index.html?it/sauronsoftware/cron4j/CronParser.html
     * @return Self reference for fluent interface.
     */
    @Override
    public EzraJobImpl<T> withSchedule(String cronPattern) {
        this.cronPattern = cronPattern;
        return this;
    }


    @Override
    public String getSchedule() {
        return cronPattern;
    }


    @Override
    public EzraJobImpl<T> withRetryPolicy() {
        throw new UnsupportedOperationException("nyi");
    }


    @Override
    public EzraJobImpl<T> withRecoveryPolicy() {
        throw new UnsupportedOperationException("nyi");
    }


    @Override
    public MethodCall getMethodCall() {
        return methodCall;
    }
    
    
    @Override
    public List<ZonedDateTime> getExecutions(int n) {
        Predictor predictor = new Predictor(getSchedule());
        List<ZonedDateTime> predictions = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            predictions.add(ZonedDateTime.ofInstant(predictor.nextMatchingDate().toInstant(), ZoneId.systemDefault()));    
        }
        return predictions;
    }
}
