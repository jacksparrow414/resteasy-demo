package com.demo.resteasy;

import com.demo.resteasy.endpoints.HelloResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/rest")
public class RestApplication extends Application {

    private static Set<Class<?>> resources = new HashSet<>();

    private static Set<Class<?>> providers = new HashSet<>();

    /**
     * initialize resources and providers
     */
    private static void initialize() {
        resources.add(HelloResource.class);
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> result = new HashSet<>();
        result.addAll(resources);
        return result;
    }
}
