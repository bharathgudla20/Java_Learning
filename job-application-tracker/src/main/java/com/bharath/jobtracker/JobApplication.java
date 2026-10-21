package com.bharath.jobtracker;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;

@Entity
@Table(name = "job_applications")
public class JobApplication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Nullable only to preserve old local single-user rows. Those rows remain inaccessible.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private AppUser owner;
    @Column(nullable = false, length = 100)
    private String company;
    @Column(nullable = false, length = 100)
    private String role;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private Status status;
    @Column(nullable = false)
    private LocalDate appliedOn;
    @Column(length = 1000)
    private String notes;

    protected JobApplication() {} // Hibernate needs a no-argument constructor.

    public JobApplication(ApplicationRequest request) { updateFrom(request); }

    public JobApplication(ApplicationRequest request, AppUser owner) {
        updateFrom(request);
        this.owner = owner;
    }

    public void updateFrom(ApplicationRequest request) {
        company = request.company().trim();
        role = request.role().trim();
        status = request.status();
        appliedOn = request.appliedOn();
        notes = request.notes() == null ? "" : request.notes().trim();
    }

    public Long getId() { return id; }
    public String getCompany() { return company; }
    public String getRole() { return role; }
    public Status getStatus() { return status; }
    public LocalDate getAppliedOn() { return appliedOn; }
    public String getNotes() { return notes; }
}
