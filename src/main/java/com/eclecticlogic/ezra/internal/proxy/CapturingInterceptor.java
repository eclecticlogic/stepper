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

import java.lang.reflect.Method;
import java.util.List;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * Captures method executions
 * @author kabram.
 *
 */
public class CapturingInterceptor implements MethodInterceptor {

    private List<MethodCall> methodCalls;


    public CapturingInterceptor(List<MethodCall> methodCalls) {
        super();
        this.methodCalls = methodCalls;
    }


    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        MethodCall methodCall = new MethodCall();
        methodCall.setMethod(method);
        if (args != null) {
            for (Object arg : args) {
                methodCall.getParameters().add(arg);
            }
        }
        methodCalls.add(methodCall);
        return null;
    }
}
