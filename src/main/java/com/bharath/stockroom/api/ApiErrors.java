package com.bharath.stockroom.api;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.http.*;
import org.springframework.dao.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.*;

@RestControllerAdvice
public class ApiErrors {
    @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ProblemDetail> validation(MethodArgumentNotValidException ex) {
        ProblemDetail p=ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Check the highlighted values and try again.");
        Map<String,String> fields=new LinkedHashMap<>();ex.getBindingResult().getFieldErrors().forEach(e->fields.put(e.getField(),e.getDefaultMessage()));p.setProperty("fields",fields);return ResponseEntity.badRequest().body(p);
    }
    @ExceptionHandler({HttpMessageNotReadableException.class,MethodArgumentTypeMismatchException.class,HandlerMethodValidationException.class}) ResponseEntity<ProblemDetail> malformed(Exception ex) {return ResponseEntity.badRequest().body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Request contains an invalid value or format."));}
    @ExceptionHandler({DataIntegrityViolationException.class,ConcurrencyFailureException.class}) ResponseEntity<ProblemDetail> conflict(Exception ex) {return ResponseEntity.status(409).body(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,"This action conflicts with a recent change. Refresh and try again."));}
    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class) ResponseEntity<ProblemDetail> status(org.springframework.web.server.ResponseStatusException ex) {return ResponseEntity.status(ex.getStatusCode()).body(ProblemDetail.forStatusAndDetail(ex.getStatusCode(),Objects.requireNonNullElse(ex.getReason(),"Request failed.")));}
}
