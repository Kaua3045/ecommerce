package com.kaua.ecommerce.infrastructure.idempotency;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test/idempotency-key-helper")
public class IdempotencyKeyHelperControllerTest {

    @IdempotencyKey
    @PostMapping(
            value = "/success",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<?> testIdempotencyKeyHelperWithReturnSuccess(@RequestBody IdempotencyKeyBodyTest body) {
        return ResponseEntity.status(201)
                .header(HttpHeaders.LOCATION, "/test/idempotency-key-helper/" + body.getId())
                .body(body);
    }

    @GetMapping(
            value = "/{id}",
            produces = "application/json"
    )
    public ResponseEntity<?> testWithoutIdempotencyKeyGETHelperWithReturnSuccess(@PathVariable String id) {
        return ResponseEntity.ok(new IdempotencyKeyBodyTest(id));
    }

    @IdempotencyKey
    @PatchMapping(
            value = "patch/{id}",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<?> testWitIdempotencyKeyPATCHHelperWithReturnSuccess(@PathVariable String id, @RequestBody IdempotencyKeyBodyTest body) {
        return ResponseEntity.ok(body);
    }

    @IdempotencyKey
    @PutMapping(
            value = "put/idempotency/{id}",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<?> testWithIdempotencyKeyPUTHelperWithReturnSuccess(@PathVariable String id, @RequestBody IdempotencyKeyBodyTest body) {
        return ResponseEntity.ok(body);
    }

    @PutMapping(
            value = "put/{id}",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<?> testWithoutIdempotencyKeyPATCHHelperWithReturnSuccess(@PathVariable String id, @RequestBody IdempotencyKeyBodyTest body) {
        return ResponseEntity.ok(body);
    }

    @IdempotencyKey
    @GetMapping(
            value = "/get/{id}",
            produces = "application/json"
    )
    public ResponseEntity<?> testWithIdempotencyKeyGETHelperWithReturnSuccessButInvalid(@PathVariable String id) {
        return ResponseEntity.ok(new IdempotencyKeyBodyTest(id));
    }
}
