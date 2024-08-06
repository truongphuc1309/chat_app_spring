package com.truongphuc.server;

import java.io.File;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

public class TomcatServer {
    public static void start() throws Exception {

        String webappDirLocation = "src/main/";
        Tomcat tomcat = new Tomcat();

        // The port that we should run on can be set into an environment variable
        // Look for that variable and default to 8080 if it isn't there.
        int webPort = 8080;

        tomcat.setPort(webPort);

        StandardContext ctx = (StandardContext) tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
        System.out.println("configuring app with basedir: " + new File("./" + webappDirLocation).getAbsolutePath());

        // Declare an alternative location for your "WEB-INF/classes" dir
        // Servlet 3.0 annotation will work

        File additionWebInfClasses = new File("target/classes");
        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                additionWebInfClasses.getAbsolutePath(), "/"));
        ctx.setResources(resources);

        tomcat.getConnector();
        tomcat.start();
        System.out.println("\n==================== SERVER RUNNING ON PORT " + webPort + "====================");
        tomcat.getServer().await();
    }
}
