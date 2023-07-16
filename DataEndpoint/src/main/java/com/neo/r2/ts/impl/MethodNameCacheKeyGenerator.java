package com.neo.r2.ts.impl;

import com.neo.util.framework.api.cache.CacheKeyGenerator;
import com.neo.util.framework.api.cache.spi.CompositeCacheKey;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Incorporates the method name with the parameters into the CompositeCacheKey
 */
public class MethodNameCacheKeyGenerator implements CacheKeyGenerator {

    @Override
    public Object generate(Method method, Object... methodParams) {
        Object[] newArray = Arrays.copyOf(methodParams, methodParams.length + 1);
        newArray[newArray.length -1] = method.getName();
        return new CompositeCacheKey(newArray);
    }
}
