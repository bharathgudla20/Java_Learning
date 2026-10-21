package com.bharath.jobtracker;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ApplicationService {
    private final ApplicationRepository repository;
    private final AccountService accounts;

    // Constructor injection: Spring supplies the repository dependency.
    public ApplicationService(ApplicationRepository repository, AccountService accounts) {
        this.repository = repository;
        this.accounts = accounts;
    }

    public List<JobApplication> list(String search, Status status) {
        return repository.search(search.trim(), status, accounts.currentUser().getId());
    }

    public JobApplication get(Long id) {
        // findById returns Optional; translate absence into a meaningful exception.
        return repository.findByIdAndOwnerId(id, accounts.currentUser().getId())
            .orElseThrow(() -> new ApplicationNotFoundException(id));
    }

    @Transactional
    public JobApplication create(ApplicationRequest request) {
        return repository.save(new JobApplication(request, accounts.currentUser()));
    }

    @Transactional
    public JobApplication update(Long id, ApplicationRequest request) {
        JobApplication application = get(id);
        application.updateFrom(request);
        return repository.save(application);
    }

    @Transactional
    public void delete(Long id) { repository.delete(get(id)); }

    public Map<Status, Long> summary() {
        // For this small learning app, use Streams + groupingBy. At scale, use SQL GROUP BY.
        Map<Status, Long> counts = repository.findByOwnerId(accounts.currentUser().getId()).stream()
            .collect(Collectors.groupingBy(JobApplication::getStatus, Collectors.counting()));
        Map<Status, Long> result = new EnumMap<>(Status.class);
        for (Status status : Status.values()) result.put(status, counts.getOrDefault(status, 0L));
        return result;
    }
}
