package com.bezkoder.springjwt.service;

import com.bezkoder.springjwt.models.Policy;
import java.util.List;

public interface PolicyService {
    
    Policy createPolicy(Policy policy);
    
    Policy getPolicyById(Long id);
    
    Policy getPolicyByPolicyNumber(String policyNumber);
    
    Policy getPolicyByExtPolicyNumber(String extPolicyNumber);
    
    List<Policy> getAllPolicies();
    
    Policy updatePolicy(Long id, Policy policy);
    
    void deletePolicy(Long id);
    
    Policy getPolicyByNiNumber(String niNumber);
    
    boolean existsByPolicyNumber(String policyNumber);
    
    boolean existsByNiNumber(String niNumber);

    List<String> getAvailablePolicyTypes();

    List<String> getAvailablePolicyStatuses(String policyType);

    List<Policy> searchPolicies(String policyType, String policyStatus);
}
