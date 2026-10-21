package com.bharath.jobtracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:api-test;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.show-sql=false"
})
@AutoConfigureMockMvc
class AccountSecurityTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired ApplicationRepository applications;
    @Autowired UserRepository users;
    @Autowired PasswordEncoder encoder;

    @BeforeEach void clean() { applications.deleteAll(); users.deleteAll(); }
    private String registration(String email, String password) throws Exception {
        return mapper.writeValueAsString(Map.of("email", email, "password", password));
    }
    private AppUser account(String email) {
        return users.save(new AppUser(email, encoder.encode("CorrectPassword123!")));
    }
    private ApplicationRequest entry(String company, Status status) {
        return new ApplicationRequest(company, "Java Developer", status, LocalDate.now(), "Private notes");
    }

    @Test void anonymousCannotReadApplicationsOrSummary() throws Exception {
        mvc.perform(get("/api/applications")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/applications/summary")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
        mvc.perform(get("/login.html")).andExpect(status().isOk());
        mvc.perform(get("/login.js")).andExpect(status().isOk());
        mvc.perform(get("/actuator/health")).andExpect(status().isOk())
            .andExpect(jsonPath("$.components").doesNotExist());
    }

    @Test void registerHashesPasswordAndNormalizesEmail() throws Exception {
        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(registration("Alice@Example.com", "CorrectPassword123!")))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.passwordHash").doesNotExist());
        AppUser saved = users.findByEmail("alice@example.com").orElseThrow();
        assertNotEquals("CorrectPassword123!", saved.getPasswordHash());
        assertTrue(encoder.matches("CorrectPassword123!", saved.getPasswordHash()));
        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(registration("ALICE@example.com", "CorrectPassword123!")))
            .andExpect(status().isConflict());
    }

    @Test void rejectsBadRegistrationAndMissingCsrf() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
            .content(registration("alice@example.com", "CorrectPassword123!")))
            .andExpect(status().isForbidden());
        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(registration("bad-email", "short"))).andExpect(status().isBadRequest());
        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(registration("alice@example.com", "\u20ac".repeat(30))))
            .andExpect(status().isBadRequest());
    }

    @Test void realLoginPersistsSessionAndLogoutInvalidatesIt() throws Exception {
        account("alice@example.com");
        var result = mvc.perform(post("/api/auth/login").with(csrf())
            .param("email", "ALICE@example.com").param("password", "CorrectPassword123!"))
            .andExpect(status().isOk()).andReturn();
        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
        assertNotNull(session);
        mvc.perform(get("/api/auth/me").session(session)).andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("alice@example.com"))
            .andExpect(jsonPath("$.passwordHash").doesNotExist());
        mvc.perform(post("/api/auth/logout").session(session).with(csrf()))
            .andExpect(status().isOk());
        assertTrue(session.isInvalid());
        mvc.perform(get("/api/applications")).andExpect(status().isUnauthorized());
    }

    @Test void wrongPasswordAndLoginWithoutCsrfAreRejected() throws Exception {
        account("alice@example.com");
        mvc.perform(post("/api/auth/login").with(csrf())
            .param("email", "alice@example.com").param("password", "WrongPassword123!"))
            .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/login")
            .param("email", "alice@example.com").param("password", "CorrectPassword123!"))
            .andExpect(status().isForbidden());
    }

    @Test void csrfEndpointProvidesUsableSessionBoundToken() throws Exception {
        var response = mvc.perform(get("/api/auth/csrf")).andExpect(status().isOk()).andReturn();
        var token = mapper.readTree(response.getResponse().getContentAsString());
        MockHttpSession session = (MockHttpSession) response.getRequest().getSession(false);
        mvc.perform(post("/api/auth/register").session(session)
            .header(token.get("headerName").asText(), token.get("token").asText())
            .contentType(MediaType.APPLICATION_JSON)
            .content(registration("alice@example.com", "CorrectPassword123!")))
            .andExpect(status().isCreated());
    }

    @Test void usersCannotReadChangeDeleteOrCountEachOthersRecords() throws Exception {
        AppUser alice = account("alice@example.com");
        AppUser bob = account("bob@example.com");
        JobApplication own = applications.save(new JobApplication(entry("Alice Company", Status.APPLIED), alice));
        JobApplication foreign = applications.save(new JobApplication(entry("Bob Company", Status.OFFER), bob));
        applications.save(new JobApplication(entry("Unowned legacy record", Status.OFFER)));
        var auth = user("alice@example.com");
        mvc.perform(get("/api/applications").with(auth)).andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(own.getId()))
            .andExpect(jsonPath("$[0].owner").doesNotExist());
        mvc.perform(get("/api/applications").param("search", "Bob").with(auth))
            .andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/applications/summary").with(auth))
            .andExpect(jsonPath("$.APPLIED").value(1)).andExpect(jsonPath("$.OFFER").value(0));
        mvc.perform(get("/api/applications/" + foreign.getId()).with(auth))
            .andExpect(status().isNotFound());
        mvc.perform(put("/api/applications/" + foreign.getId()).with(auth).with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(entry("Hacked", Status.APPLIED))))
            .andExpect(status().isNotFound());
        mvc.perform(delete("/api/applications/" + foreign.getId()).with(auth).with(csrf()))
            .andExpect(status().isNotFound());
        assertTrue(applications.existsById(foreign.getId()));
    }

    @Test void authenticatedWriteWithoutCsrfIsForbidden() throws Exception {
        account("alice@example.com");
        mvc.perform(post("/api/applications").with(user("alice@example.com"))
            .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(entry("Acme", Status.APPLIED))))
            .andExpect(status().isForbidden());
    }
}
