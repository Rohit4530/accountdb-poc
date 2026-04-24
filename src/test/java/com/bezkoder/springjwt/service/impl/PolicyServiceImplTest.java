package com.bezkoder.springjwt.service.impl;

import com.bezkoder.springjwt.models.Policy;
import com.bezkoder.springjwt.repository.PolicyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolicyServiceImplTest {

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private PolicyServiceImpl policyService;

    @Test
    void getAvailablePolicyTypesReturnsRepositoryValues() {
        when(policyRepository.findDistinctPolicyTypes()).thenReturn(List.of("Pension", "Savings"));

        List<String> policyTypes = policyService.getAvailablePolicyTypes();

        assertEquals(List.of("Pension", "Savings"), policyTypes);
        verify(policyRepository).findDistinctPolicyTypes();
    }

    @Test
    void getAvailablePolicyStatusesRequiresPolicyType() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> policyService.getAvailablePolicyStatuses("   ")
        );

        assertEquals("Policy type is required to load policy statuses", exception.getMessage());
    }

    @Test
    void getAvailablePolicyStatusesUsesTrimmedPolicyType() {
        when(policyRepository.findDistinctPolicyStatusesByPolicyType("Pension"))
                .thenReturn(List.of("Active", "Paid Up"));

        List<String> policyStatuses = policyService.getAvailablePolicyStatuses("  Pension  ");

        assertEquals(List.of("Active", "Paid Up"), policyStatuses);
        verify(policyRepository).findDistinctPolicyStatusesByPolicyType("Pension");
    }

    @Test
    void searchPoliciesRequiresPolicyType() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> policyService.searchPolicies(null, "Active")
        );

        assertEquals("Policy type is required to search policies", exception.getMessage());
    }

    @Test
    void searchPoliciesReturnsMatchingPolicies() {
        Policy policy = new Policy();
        policy.setId(1L);
        policy.setPolicyNumber("POL-1001");
        policy.setProduct("Pension");
        policy.setPolicyStatus("Active");

        when(policyRepository.findPoliciesByPolicyTypeAndStatus("Pension", "Active"))
                .thenReturn(List.of(policy));

        List<Policy> policies = policyService.searchPolicies("Pension", "Active");

        assertEquals(1, policies.size());
        assertEquals("POL-1001", policies.get(0).getPolicyNumber());
        verify(policyRepository).findPoliciesByPolicyTypeAndStatus("Pension", "Active");
    }
}
