/*
 * Copyright © 2024 Brinvex (dev@brinvex.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.brinvex.util.ecb.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ServiceLoader;

public enum EcbObjectFactory {

    INSTANCE;

    private volatile HttpClientFacade defaultHttpClientFacade;

    public EcbFetchService getEcbFetchService() {
        return constructEcbFetchService(getDefaultHttpClient());
    }

    public EcbFetchService getEcbFetchService(HttpClientFacade httpClientFacade) {
        return constructEcbFetchService(httpClientFacade);
    }

    @SuppressWarnings({"UnnecessaryLocalVariable", "unchecked"})
    private EcbFetchService constructEcbFetchService(HttpClientFacade instance) {
        try {
            String implClassName = "com.brinvex.util.ecb.impl.EcbFetchServiceImpl";
            Class<? extends EcbFetchService> implClass = (Class<? extends EcbFetchService>) Class.forName(implClassName);
            Constructor<? extends EcbFetchService> implConstructor = implClass.getConstructor(HttpClientFacade.class);
            EcbFetchService inst = implConstructor.newInstance(instance);
            return inst;
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpClientFacade getDefaultHttpClient() {
        if (defaultHttpClientFacade == null) {
            synchronized (this) {
                if (defaultHttpClientFacade == null) {
                    defaultHttpClientFacade = ServiceLoader.load(HttpClientFacade.class)
                            .stream()
                            .map(ServiceLoader.Provider::get)
                            .findAny()
                            .orElseThrow(() -> new IllegalStateException("Not found any implementation of '%s'".formatted(HttpClientFacade.class)));
                }
            }
        }
        return defaultHttpClientFacade;
    }

}
