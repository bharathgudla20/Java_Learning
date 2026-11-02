package com.bharath.stockroom.config;
import com.bharath.stockroom.repository.AccountRepository;
import com.bharath.stockroom.service.StockroomService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.*;
@Component
public class DemoCleanup {
    private final AccountRepository accounts;private final StockroomService service;
    public DemoCleanup(AccountRepository a,StockroomService s) {accounts=a;service=s;}
    @Scheduled(fixedDelay=1800000) public void removeExpiredDemos() {
        for(var a:accounts.findByDemoTrueAndCreatedAtBefore(Instant.now().minus(Duration.ofHours(2)))) service.deleteWorkspace(a.id);
    }
}
