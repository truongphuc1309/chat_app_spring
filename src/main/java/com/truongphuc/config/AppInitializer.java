package com.truongphuc.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class AppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // Load .env
        DotenvInitializer.initialize();

        System.out.println("\n\n======================== Starting Web Application ==================");
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        // register our config class
        ctx.register(ApplicationContextConfig.class);
        ctx.register(MvcConfig.class);
        ctx.register(JpaConfig.class);
        ctx.register(SecurityWebApplicationInitializer.class);
        ctx.register(SecurityConfig.class);
        ctx.register(WebSocketConfig.class);
        ctx.register(MailConfig.class);
        ctx.register(ThymeleafConfig.class);
        ctx.register(RedisConfig.class);

        servletContext.addListener(new ContextLoaderListener(ctx));
        // using servlet 3 api to dynamically create
        // spring dispatcher servlet
        ServletRegistration.Dynamic servlet = servletContext.addServlet("mvc", new DispatcherServlet(ctx));
        servlet.setMultipartConfig(MultipartConfig.getMultipartConfigElement());
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/");
    }
}
