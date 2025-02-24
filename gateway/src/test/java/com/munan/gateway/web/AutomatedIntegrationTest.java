package com.munan.gateway.web;

import com.munan.gateway.GatewayApp;
import com.munan.gateway.config.AsyncSyncConfiguration;
import com.munan.gateway.config.EmbeddedSQL;
import com.munan.gateway.config.JacksonConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    classes = { GatewayApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class }
)
@EmbeddedSQL
public @interface AutomatedIntegrationTest {
}
