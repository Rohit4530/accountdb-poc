package com.bezkoder.springjwt.payload.response;

import com.bezkoder.springjwt.models.Policy;

import java.time.LocalDate;
import java.util.StringJoiner;

public class PolicySummaryResponse {

    private Long id;
    private String policyNumber;
    private String extPolicyNumber;
    private String policyType;
    private String policyStatus;
    private String policyStatusDescription;
    private String customerName;
    private String niNumber;
    private String policyAgency;
    private LocalDate startDate;
    private LocalDate renewalDate;

    public static PolicySummaryResponse fromPolicy(Policy policy) {
        PolicySummaryResponse summary = new PolicySummaryResponse();
        summary.setId(policy.getId());
        summary.setPolicyNumber(policy.getPolicyNumber());
        summary.setExtPolicyNumber(policy.getExtPolicyNumber());
        summary.setPolicyType(policy.getProduct());
        summary.setPolicyStatus(policy.getPolicyStatus());
        summary.setPolicyStatusDescription(policy.getPolStatusDesc());
        summary.setCustomerName(buildCustomerName(policy));
        summary.setNiNumber(policy.getNiNumber());
        summary.setPolicyAgency(policy.getPolicyAgency());
        summary.setStartDate(policy.getPolStartDate());
        summary.setRenewalDate(policy.getRenewalDate());
        return summary;
    }

    private static String buildCustomerName(Policy policy) {
        StringJoiner joiner = new StringJoiner(" ");

        if (hasText(policy.getTitle())) {
            joiner.add(policy.getTitle().trim());
        }
        if (hasText(policy.getForeName())) {
            joiner.add(policy.getForeName().trim());
        }
        if (hasText(policy.getSurName())) {
            joiner.add(policy.getSurName().trim());
        }

        String customerName = joiner.toString();
        return customerName.isBlank() ? "Unknown policy holder" : customerName;
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getExtPolicyNumber() {
        return extPolicyNumber;
    }

    public void setExtPolicyNumber(String extPolicyNumber) {
        this.extPolicyNumber = extPolicyNumber;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public String getPolicyStatus() {
        return policyStatus;
    }

    public void setPolicyStatus(String policyStatus) {
        this.policyStatus = policyStatus;
    }

    public String getPolicyStatusDescription() {
        return policyStatusDescription;
    }

    public void setPolicyStatusDescription(String policyStatusDescription) {
        this.policyStatusDescription = policyStatusDescription;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getNiNumber() {
        return niNumber;
    }

    public void setNiNumber(String niNumber) {
        this.niNumber = niNumber;
    }

    public String getPolicyAgency() {
        return policyAgency;
    }

    public void setPolicyAgency(String policyAgency) {
        this.policyAgency = policyAgency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(LocalDate renewalDate) {
        this.renewalDate = renewalDate;
    }
}
