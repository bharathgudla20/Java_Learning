package com.bharath.stockroom.api;
import com.bharath.stockroom.api.Contracts.*;
import com.bharath.stockroom.service.StockroomService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api")
public class WorkspaceController {
    private final StockroomService service;
    public WorkspaceController(StockroomService s) {service=s;}
    private UUID owner(Authentication a) {return UUID.fromString(a.getName());}
    @GetMapping("/overview") public Overview overview(Authentication a) {return service.overview(owner(a));}
    @GetMapping("/products") public PageView<ProductView> products(Authentication a,@RequestParam(defaultValue="") @Size(max=100) String q,@RequestParam(defaultValue="") @Size(max=40) String category,@RequestParam(defaultValue="false") boolean low,@RequestParam(defaultValue="0") int page) {return service.products(owner(a),q,category,low,page);}
    @GetMapping("/catalog") public List<ProductView> catalog(Authentication a) {return service.catalog(owner(a));}
    @PostMapping("/products") @ResponseStatus(HttpStatus.CREATED) public ProductView create(Authentication a,@Valid @RequestBody ProductInput input) {return service.createProduct(owner(a),input);}
    @PatchMapping("/products/{id}/stock") public ProductView stock(Authentication a,@PathVariable UUID id,@Valid @RequestBody StockInput input) {return service.adjustStock(owner(a),id,input);}
    @DeleteMapping("/products/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void archive(Authentication a,@PathVariable UUID id) {service.archive(owner(a),id);}
    @GetMapping("/orders") public List<OrderView> orders(Authentication a) {return service.orders(owner(a));}
    @PostMapping("/orders") @ResponseStatus(HttpStatus.CREATED) public OrderView order(Authentication a,@RequestHeader(value="Idempotency-Key",required=false) String key,@Valid @RequestBody OrderInput input) {return service.placeOrder(owner(a),key,input);}
    @PatchMapping("/orders/{id}/status") public OrderView status(Authentication a,@PathVariable UUID id,@Valid @RequestBody StatusInput input) {return service.changeStatus(owner(a),id,input.status());}
    @PostMapping("/demo/reset") @ResponseStatus(HttpStatus.NO_CONTENT) public void reset(Authentication a) {service.reset(owner(a));}
}
