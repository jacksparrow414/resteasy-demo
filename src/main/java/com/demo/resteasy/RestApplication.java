package com.demo.resteasy;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * there is no need to use web.xml, use ResteasyServletInitializer instead
 */
@ApplicationPath("/rest")
public class RestApplication extends Application {
}
