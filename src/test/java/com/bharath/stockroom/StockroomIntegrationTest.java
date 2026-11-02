package com.bharath.stockroom;

import com.bharath.stockroom.api.Contracts.*;
import com.bharath.stockroom.domain.*;
import com.bharath.stockroom.repository.*;
import com.bharath.stockroom.service.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000","spring.jpa.hibernate.ddl-auto=create-drop"})
@AutoConfigureMockMvc
class StockroomIntegrationTest {
    @Autowired StockroomService service;
    @Autowired AccountService auth;
    @Autowired AccountRepository accounts;
    @Autowired ProductRepository products;
    @Autowired MockMvc mvc;
    UUID owner;
    @BeforeEach void workspace() {owner=accounts.save(new Account(null,"unused","Test",true)).id;}
    ProductView product(int stock) {return service.createProduct(owner,new ProductInput("TEST-1","Test product","Accessories",new BigDecimal("12.50"),stock,2));}
    OrderInput order(UUID id,int quantity) {return new OrderInput("Test customer","test@example.com",List.of(new LineInput(id,quantity)));}

    @Test void createsOrderAndCalculatesPriceOnServer() {
        ProductView p=product(10);OrderView o=service.placeOrder(owner,"request-0001",order(p.id(),3));
        assertEquals(0,new BigDecimal("37.50").compareTo(o.total()));assertEquals(7,products.findById(p.id()).orElseThrow().stock);
    }
    @Test void insufficientStockRollsBackWholeOrder() {
        ProductView p=product(10);ProductView q=service.createProduct(owner,new ProductInput("TEST-2","Second","Accessories",BigDecimal.TEN,0,2));
        OrderInput input=new OrderInput("Test","test@example.com",List.of(new LineInput(p.id(),2),new LineInput(q.id(),1)));
        assertThrows(ResponseStatusException.class,()->service.placeOrder(owner,"request-0002",input));
        assertEquals(10,products.findById(p.id()).orElseThrow().stock);assertTrue(service.orders(owner).isEmpty());
    }
    @Test void duplicateRequestDoesNotDeductStockTwice() {
        ProductView p=product(10);OrderView a=service.placeOrder(owner,"request-0003",order(p.id(),2));OrderView b=service.placeOrder(owner,"request-0003",order(p.id(),2));
        assertEquals(a.id(),b.id());assertEquals(8,products.findById(p.id()).orElseThrow().stock);assertEquals(1,service.orders(owner).size());
    }
    @Test void reusedKeyWithDifferentPayloadConflicts() {
        ProductView p=product(10);service.placeOrder(owner,"request-0004",order(p.id(),2));
        assertEquals(409,assertThrows(ResponseStatusException.class,()->service.placeOrder(owner,"request-0004",order(p.id(),3))).getStatusCode().value());
    }
    @Test void cancellationRestoresStockExactlyOnce() {
        ProductView p=product(10);OrderView o=service.placeOrder(owner,"request-0005",order(p.id(),4));
        service.changeStatus(owner,o.id(),"CANCELLED");service.changeStatus(owner,o.id(),"CANCELLED");
        assertEquals(10,products.findById(p.id()).orElseThrow().stock);
        assertEquals(409,assertThrows(ResponseStatusException.class,()->service.changeStatus(owner,o.id(),"SHIPPED")).getStatusCode().value());
    }
    @Test void cannotCancelShippedOrder() {
        ProductView p=product(10);OrderView o=service.placeOrder(owner,"request-0006",order(p.id(),1));service.changeStatus(owner,o.id(),"SHIPPED");
        assertThrows(ResponseStatusException.class,()->service.changeStatus(owner,o.id(),"CANCELLED"));assertEquals(9,products.findById(p.id()).orElseThrow().stock);
    }
    @Test void cannotAccessAnotherWorkspace() {
        ProductView p=product(10);UUID other=accounts.save(new Account(null,"unused","Other",true)).id;
        assertTrue(service.catalog(other).isEmpty());
        assertEquals(404,assertThrows(ResponseStatusException.class,()->service.adjustStock(other,p.id(),new StockInput(10,"Restock"))).getStatusCode().value());
    }
    @Test void rejectsNegativeStockAndDuplicateSku() {
        ProductView p=product(2);assertThrows(ResponseStatusException.class,()->service.adjustStock(owner,p.id(),new StockInput(-3,"Damage")));
        assertThrows(ResponseStatusException.class,()->product(2));assertEquals(2,products.findById(p.id()).orElseThrow().stock);
    }
    @Test void archivedProductRetainsOrderHistory() {
        ProductView p=product(10);OrderView o=service.placeOrder(owner,"request-0007",order(p.id(),2));service.archive(owner,p.id());
        assertTrue(service.catalog(owner).isEmpty());assertEquals("Test product",service.orders(owner).getFirst().lines().getFirst().name());
        service.changeStatus(owner,o.id(),"CANCELLED");assertEquals(10,products.findById(p.id()).orElseThrow().stock);
    }
    @Test void concurrentOrdersCannotOversell() throws Exception {
        ProductView p=product(1);var executor=Executors.newFixedThreadPool(2);var ready=new CountDownLatch(2);var start=new CountDownLatch(1);
        try {
            List<Future<Boolean>> results=new ArrayList<>();
            for(int i=0;i<2;i++){final int n=i;results.add(executor.submit(()->{ready.countDown();start.await();try{service.placeOrder(owner,"concurrent-"+n,order(p.id(),1));return true;}catch(ResponseStatusException ex){assertEquals(409,ex.getStatusCode().value());return false;}}));}
            assertTrue(ready.await(5,TimeUnit.SECONDS));start.countDown();int successes=0;for(var f:results)if(f.get(15,TimeUnit.SECONDS))successes++;
            assertEquals(1,successes);assertEquals(0,products.findById(p.id()).orElseThrow().stock);assertEquals(1,service.orders(owner).size());
        }finally{executor.shutdownNow();}
    }
    @Test void concurrentDuplicateRequestCreatesOneOrder() throws Exception {
        ProductView p=product(10);var executor=Executors.newFixedThreadPool(2);var start=new CountDownLatch(1);
        try {Callable<OrderView> task=()->{start.await();return service.placeOrder(owner,"same-request-01",order(p.id(),2));};var a=executor.submit(task);var b=executor.submit(task);start.countDown();assertEquals(a.get(15,TimeUnit.SECONDS).id(),b.get(15,TimeUnit.SECONDS).id());assertEquals(8,products.findById(p.id()).orElseThrow().stock);}finally{executor.shutdownNow();}
    }
    @Test void searchEscapesWildcardsAndPaginates() {product(10);assertEquals(1,service.products(owner,"test","",false,0).totalElements());assertEquals(0,service.products(owner,"%","",false,0).totalElements());assertEquals(0,service.products(owner,"","",true,0).totalElements());}
    @Test void cancellationExcludedFromDashboardRevenue() {ProductView p=product(10);OrderView o=service.placeOrder(owner,"request-0008",order(p.id(),2));service.changeStatus(owner,o.id(),"CANCELLED");assertEquals(0,BigDecimal.ZERO.compareTo(service.overview(owner).revenue()));}
    @Test void resetReplacesDemoDataWithoutDuplicates() {service.seed(owner);service.reset(owner);service.reset(owner);assertEquals(12,service.catalog(owner).size());assertEquals(14,service.orders(owner).size());}
    @Test void authenticationAndCsrfAreRequired() throws Exception {
        mvc.perform(get("/api/overview")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/demo")).andExpect(status().isForbidden());
        mvc.perform(post("/api/products").with(user(owner.toString())).contentType("application/json").content("{}" )).andExpect(status().isForbidden());
    }
    @Test void apiValidatesInput() throws Exception {
        mvc.perform(post("/api/products").with(user(owner.toString())).with(csrf()).contentType("application/json").content("{\"sku\":\"x\",\"name\":\"\",\"category\":\"\",\"price\":-1,\"stock\":-1,\"reorderPoint\":0}")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.fields.name").exists());
    }
    @Test void apiCannotMutateAnotherWorkspacesProduct() throws Exception {
        ProductView p=product(10);UUID other=accounts.save(new Account(null,"unused","Other",true)).id;
        mvc.perform(patch("/api/products/"+p.id()+"/stock").with(user(other.toString())).with(csrf()).contentType("application/json").content("{\"change\":1,\"reason\":\"Test\"}")).andExpect(status().isNotFound());
    }
    @Test void registrationHashesPasswordAndLoginChecksIt() {
        String email=UUID.randomUUID()+"@example.com";Account a=auth.register(new Register("Tester",email,"a-safe-password"));
        assertNotEquals("a-safe-password",a.passwordHash);assertEquals(a.id,auth.login(new Login(email,"a-safe-password")).id);
        assertThrows(ResponseStatusException.class,()->auth.login(new Login(email,"wrong-password")));
    }
}
