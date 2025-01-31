package com.munan.gateway.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

//import org.testcontainers.containers.JdbcDatabaseContainer;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.containers.output.Slf4jLogConsumer;

public class PostgreSqlTestContainer implements SqlTestContainer {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreSqlTestContainer.class);

    private Connection connection;

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @PostConstruct
    public void initialize() {
        try {
            LOG.info("Connecting to real PostgreSQL database at {}", databaseUrl);
            System.out.println("######################ENTER HERE " + databaseUrl + " " + username + " " + password);
            connection = DriverManager.getConnection(databaseUrl, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to PostgreSQL", e);
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                LOG.info("Closed connection to real PostgreSQL database.");
            }
        } catch (SQLException e) {
            LOG.error("Error while closing PostgreSQL connection", e);
        }
    }

    //    @Override
    //    public JdbcDatabaseContainer<?> getTestContainer() {
    //        throw new UnsupportedOperationException("Real database does not use Testcontainers.");
    //    }

    @Override
    public void afterPropertiesSet() throws Exception {}
    /*
    private PostgreSQLContainer<?> postgreSQLContainer;

    @Override
    public void destroy() {
        if (null != postgreSQLContainer && postgreSQLContainer.isRunning()) {
            postgreSQLContainer.stop();
        }
    }

    @Override
    public void afterPropertiesSet() {
        if (null == postgreSQLContainer) {
            postgreSQLContainer = new PostgreSQLContainer<>("postgres:17.2")
                .withDatabaseName("gateway")
                .withTmpFs(Collections.singletonMap("/testtmpfs", "rw"))
                .withLogConsumer(new Slf4jLogConsumer(LOG))
                .withReuse(true);
        }
        if (!postgreSQLContainer.isRunning()) {
            postgreSQLContainer.start();
        }
    }

    @Override
    public JdbcDatabaseContainer<?> getTestContainer() {
        return postgreSQLContainer;
    }

     */
}
