package com.bharath.jobtracker;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

// DTO: the API accepts these fields; the database generates the ID.
public record ApplicationRequest(
    @NotBlank @Size(max = 100) String company,
    @NotBlank @Size(max = 100) String role,
    @NotNull Status status,
    @NotNull @PastOrPresent LocalDate appliedOn,
    @Size(max = 1000) String notes
) {}
