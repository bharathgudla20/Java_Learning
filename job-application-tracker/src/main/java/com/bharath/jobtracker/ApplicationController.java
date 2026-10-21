package com.bharath.jobtracker;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    private final ApplicationService service;
    public ApplicationController(ApplicationService service) { this.service = service; }

    @GetMapping
    public List<JobApplication> list(@RequestParam(defaultValue = "") String search,
                                    @RequestParam(required = false) Status status) {
        return service.list(search, status);
    }

    @GetMapping("/summary")
    public Map<Status, Long> summary() { return service.summary(); }

    @GetMapping("/{id}")
    public JobApplication get(@PathVariable Long id) { return service.get(id); }

    @PostMapping
    public ResponseEntity<JobApplication> create(@Valid @RequestBody ApplicationRequest request) {
        JobApplication saved = service.create(request);
        return ResponseEntity.created(URI.create("/api/applications/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public JobApplication update(@PathVariable Long id, @Valid @RequestBody ApplicationRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
