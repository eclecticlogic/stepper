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

import net.sf.cglib.proxy.Enhancer;


/**
 * @author kabram.
 *
 */
public class ProxyCreator {

    /**
     * @param clz Class for which a proxy needs to be created.
     * @return Proxy wrapper with proxied instance and method call capture list. The list is populated as methods are
     * called and is empty at the end of this method.
     */
    @SuppressWarnings("unchecked")
    public static <T> Proxy<T> create(Class<T> clz) {
        
        // Ensure we are not working with a proxy.
        Class<?> z = clz;
        // The enhancer isn't sufficient to determine proxy when spring packaged cglib proxies are encountered.
        while (Enhancer.isEnhanced(z) || z.getName().toLowerCase().contains("cglib")) {
            z = z.getSuperclass();
        }
        
        Proxy<T> proxy = new Proxy<T>();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(z);
        enhancer.setCallback(new CapturingInterceptor(proxy.getMethodCalls()));
        proxy.setProxy((T)enhancer.create());
        return proxy;
    }
}
