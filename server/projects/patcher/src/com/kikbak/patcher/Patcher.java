package com.kikbak.patcher;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Patcher {
    static Logger log = Logger.getLogger(Patcher.class);

    private static final String LOCAL_CONFIG = "META-INF/local.properties";

    AbstractApplicationContext appCtx;

    private static void configLog4j() throws IOException {
        PropertyConfigurator.configureAndWatch("META-INF/log4j.properties");
    }

    public static void main(String[] args) throws IOException {
        configLog4j();

        boolean versiononly = false;

        for (String arg : args) {
        	if (arg.equalsIgnoreCase("version")) {
                versiononly = true;
            }
        }

        if (versiononly) {
            Properties properties = new Properties();
            properties.load(new ClassPathResource("version.properties").getInputStream());

            System.out.println(properties.get("server.version"));
            System.exit(0);
        }
        
        AbstractApplicationContext context = new FileSystemXmlApplicationContext("META-INF/db-autopatch.xml");

        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();

        configurer.setLocation(new FileSystemResource(LOCAL_CONFIG));

        context.addBeanFactoryPostProcessor(configurer);
        context.refresh();

        AutoPatcher autopatcher = (AutoPatcher) context.getBean("autopatch");
        autopatcher.start();

    }
}