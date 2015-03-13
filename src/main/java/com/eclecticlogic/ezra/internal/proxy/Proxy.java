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
package com.eclecticlogic.ezra.internal.proxy;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Models a proxy and a collection of MethodCall instances that will be populated with actual calls.
 * @author kabram.
 *
 */
public class Proxy<T> {

    private T proxy;
    private List<MethodCall> methodCalls = Lists.newArrayList();


    public T getProxy() {
        return proxy;
    }


    public void setProxy(T proxy) {
        this.proxy = proxy;
    }


    public List<MethodCall> getMethodCalls() {
        return methodCalls;
    }

}
