package com.bharath.jobtracker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

// Runs in GitHub Actions (or locally when the POSTGRES_TEST_* variables are set).
// Use a disposable database: Flyway creates its schema. Never point this at live user data.
@EnabledIfEnvironmentVariable(named = "POSTGRES_TEST_URL", matches = ".+")
@SpringBootTest(properties = {
    "spring.flyway.enabled=true",
    "spring.flyway.locations=classpath:db/migration/postgresql",
    "spring.jpa.hibernate.ddl-auto=validate", "spring.jpa.show-sql=false"
})
class PostgresMigrationTest {
    @DynamicPropertySource static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> System.getenv("POSTGRES_TEST_URL"));
        registry.add("spring.datasource.username", () -> System.getenv("POSTGRES_TEST_USER"));
        registry.add("spring.datasource.password", () -> System.getenv("POSTGRES_TEST_PASSWORD"));
    }
    @Autowired UserRepository users;
    @Autowired ApplicationRepository applications;
    @Autowired ApplicationService service;

    @Test @WithMockUser(username = "postgres-test@example.com")
    void migrationsMatchEntitiesAndPersistOwnedRecords() {
        applications.deleteAll(); users.deleteAll();
        users.save(new AppUser("postgres-test@example.com", "unused-by-mock-auth"));
        var saved = service.create(new ApplicationRequest("PostgreSQL test", "Java Developer",
            Status.APPLIED, LocalDate.now(), "Disposable test"));
        assertEquals(saved.getId(), service.get(saved.getId()).getId());
        assertEquals(1L, service.summary().get(Status.APPLIED));
        service.delete(saved.getId());
        users.deleteAll();
    }
}
