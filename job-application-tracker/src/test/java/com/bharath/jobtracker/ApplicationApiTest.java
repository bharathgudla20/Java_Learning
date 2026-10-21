package com.bharath.jobtracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.MediaType;
import java.time.LocalDate;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

// Real controller -> service -> repository -> H2 tests, isolated from your saved data.
@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:api-test;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.show-sql=false"
})
@AutoConfigureMockMvc
@WithMockUser(username = "tester@example.com")
class ApplicationApiTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired ApplicationRepository repository;

    @Autowired UserRepository users;
    private AppUser owner;
    @BeforeEach void clean() {
        repository.deleteAll(); users.deleteAll();
        owner = users.save(new AppUser("tester@example.com", "not-used-in-mock-auth-tests"));
    }

    private String body(String company, Status status) throws Exception {
        return mapper.writeValueAsString(new ApplicationRequest(company, "Java Developer", status,
            LocalDate.now(), "Practice Java"));
    }

    @Test void fullLifecycleAndSummary() throws Exception {
        var response = mvc.perform(post("/api/applications").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(body("Acme", Status.APPLIED)))
            .andExpect(status().isCreated()).andExpect(header().exists("Location"))
            .andReturn().getResponse();
        long id = mapper.readTree(response.getContentAsString()).get("id").asLong();
        mvc.perform(get("/api/applications/" + id)).andExpect(status().isOk())
            .andExpect(jsonPath("$.company").value("Acme"));
        mvc.perform(put("/api/applications/" + id).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(body("Acme", Status.INTERVIEW))).andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("INTERVIEW"));
        mvc.perform(get("/api/applications/summary")).andExpect(status().isOk())
            .andExpect(jsonPath("$.INTERVIEW").value(1)).andExpect(jsonPath("$.APPLIED").value(0));
        mvc.perform(delete("/api/applications/" + id).with(csrf())).andExpect(status().isNoContent());
        mvc.perform(get("/api/applications/" + id)).andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test void filtersByCompanyOrRoleAndStatus() throws Exception {
        repository.save(new JobApplication(new ApplicationRequest("Acme", "Java Developer", Status.APPLIED, LocalDate.now(), ""), owner));
        repository.save(new JobApplication(new ApplicationRequest("Other", "Designer", Status.OFFER, LocalDate.now(), ""), owner));
        mvc.perform(get("/api/applications").param("search", "aCmE").param("status", "APPLIED"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/applications").param("search", "Java"))
            .andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/applications").param("search", "%"))
            .andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/applications").param("status", "REJECTED"))
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test void rejectsInvalidFieldsAndMalformedInput() throws Exception {
        mvc.perform(post("/api/applications").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(body("   ", Status.APPLIED))).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
        String future = mapper.writeValueAsString(new ApplicationRequest("Acme", "Java", Status.APPLIED,
            LocalDate.now().plusDays(1), ""));
        mvc.perform(post("/api/applications").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(future))
            .andExpect(status().isBadRequest());
        mvc.perform(post("/api/applications").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
        mvc.perform(post("/api/applications").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{broken"))
            .andExpect(status().isBadRequest());
        mvc.perform(get("/api/applications").param("status", "WRONG"))
            .andExpect(status().isBadRequest());
        mvc.perform(get("/api/applications/not-a-number")).andExpect(status().isBadRequest());
    }

    @Test void missingUpdatesAndDeletesReturn404() throws Exception {
        mvc.perform(put("/api/applications/999999").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(body("Acme", Status.APPLIED))).andExpect(status().isNotFound());
        mvc.perform(delete("/api/applications/999999").with(csrf())).andExpect(status().isNotFound());
    }

    @Test void servesFrontend() throws Exception {
        mvc.perform(get("/index.html")).andExpect(status().isOk());
        mvc.perform(get("/app.js")).andExpect(status().isOk());
        mvc.perform(get("/styles.css")).andExpect(status().isOk());
    }
}
