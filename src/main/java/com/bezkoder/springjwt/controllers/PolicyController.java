package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.Policy;
import com.bezkoder.springjwt.payload.response.MessageResponse;
import com.bezkoder.springjwt.payload.response.PolicySummaryResponse;
import com.bezkoder.springjwt.service.PolicyService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private static final Logger logger = LoggerFactory.getLogger(PolicyController.class);

    private final PolicyService policyService;

    @Autowired
    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    // CREATE
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> createPolicy(@Valid @RequestBody Policy policy) {
        logger.info("Creating new policy: {}", policy.getPolicyNumber());
        Policy createdPolicy = policyService.createPolicy(policy);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPolicy);
    }

    // READ ALL
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('USER')")
    public ResponseEntity<List<Policy>> getAllPolicies() {
        logger.info("Fetching all policies");
        List<Policy> policies = policyService.getAllPolicies();
        return ResponseEntity.ok(policies);
    }

    @GetMapping("/policy-types")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('USER')")
    public ResponseEntity<List<String>> getAvailablePolicyTypes() {
        logger.info("Fetching available policy types");
        return ResponseEntity.ok(policyService.getAvailablePolicyTypes());
    }

    @GetMapping("/policy-statuses")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('USER')")
    public ResponseEntity<List<String>> getAvailablePolicyStatuses(@RequestParam String policyType) {
        logger.info("Fetching available policy statuses for type: {}", policyType);
        return ResponseEntity.ok(policyService.getAvailablePolicyStatuses(policyType));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('USER')")
    public ResponseEntity<List<PolicySummaryResponse>> searchPolicies(
            @RequestParam String policyType,
            @RequestParam(required = false) String policyStatus) {
        logger.info("Searching policies for type: {} and status: {}", policyType, policyStatus);
        List<PolicySummaryResponse> policySummaries = policyService.searchPolicies(policyType, policyStatus)
                .stream()
                .map(PolicySummaryResponse::fromPolicy)
                .toList();
        return ResponseEntity.ok(policySummaries);
    }

    // READ ONE by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('USER')")
    public ResponseEntity<Policy> getPolicyById(@PathVariable Long id) {
        logger.info("Fetching policy by id: {}", id);
        Policy policy = policyService.getPolicyById(id);
        return ResponseEntity.ok(policy);
    }

    // READ ONE by Policy Number
    @GetMapping("/number/{policyNumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('USER')")
    public ResponseEntity<Policy> getPolicyByPolicyNumber(@PathVariable String policyNumber) {
        logger.info("Fetching policy by number: {}", policyNumber);
        Policy policy = policyService.getPolicyByPolicyNumber(policyNumber);
        return ResponseEntity.ok(policy);
    }

    // READ ONE by External Policy Number
    @GetMapping("/ext-number/{extPolicyNumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('USER')")
    public ResponseEntity<Policy> getPolicyByExtPolicyNumber(@PathVariable String extPolicyNumber) {
        logger.info("Fetching policy by external number: {}", extPolicyNumber);
        Policy policy = policyService.getPolicyByExtPolicyNumber(extPolicyNumber);
        return ResponseEntity.ok(policy);
    }

    // READ ONE by NI Number
    @GetMapping("/ni/{niNumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR') or hasRole('USER')")
    public ResponseEntity<Policy> getPolicyByNiNumber(@PathVariable String niNumber) {
        logger.info("Fetching policy by NI number: {}", niNumber);
        Policy policy = policyService.getPolicyByNiNumber(niNumber);
        return ResponseEntity.ok(policy);
    }

    // CHECK EXISTS by Policy Number
    @GetMapping("/exists/number/{policyNumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Boolean> existsByPolicyNumber(@PathVariable String policyNumber) {
        logger.info("Checking if policy exists by number: {}", policyNumber);
        boolean exists = policyService.existsByPolicyNumber(policyNumber);
        return ResponseEntity.ok(exists);
    }

    // CHECK EXISTS by NI Number
    @GetMapping("/exists/ni/{niNumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Boolean> existsByNiNumber(@PathVariable String niNumber) {
        logger.info("Checking if policy exists by NI number: {}", niNumber);
        boolean exists = policyService.existsByNiNumber(niNumber);
        return ResponseEntity.ok(exists);
    }

    // UPDATE
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Policy> updatePolicy(@PathVariable Long id, @Valid @RequestBody Policy policy) {
        logger.info("Updating policy with id: {}", id);
        Policy updatedPolicy = policyService.updatePolicy(id, policy);
        return ResponseEntity.ok(updatedPolicy);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePolicy(@PathVariable Long id) {
        logger.info("Deleting policy with id: {}", id);
        policyService.deletePolicy(id);
        return ResponseEntity.ok(new MessageResponse("Policy deleted successfully"));
    }
}
