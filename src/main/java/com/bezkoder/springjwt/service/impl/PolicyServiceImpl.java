package com.bezkoder.springjwt.service.impl;

import com.bezkoder.springjwt.exception.DuplicatePolicyException;
import com.bezkoder.springjwt.exception.PolicyNotFoundException;
import com.bezkoder.springjwt.models.Policy;
import com.bezkoder.springjwt.repository.PolicyRepository;
import com.bezkoder.springjwt.service.PolicyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
public class PolicyServiceImpl implements PolicyService {

    private static final Logger logger = LoggerFactory.getLogger(PolicyServiceImpl.class);

    private final PolicyRepository policyRepository;

    @Autowired
    public PolicyServiceImpl(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Override
    @Transactional
    public Policy createPolicy(Policy policy) {
        logger.info("Creating new policy with number: {}", policy.getPolicyNumber());
        
        validatePolicy(policy);
        
        if (StringUtils.hasText(policy.getPolicyNumber()) && 
            policyRepository.existsByPolicyNumber(policy.getPolicyNumber())) {
            throw new DuplicatePolicyException("Policy with number " + policy.getPolicyNumber() + " already exists");
        }
        
        if (StringUtils.hasText(policy.getNiNumber()) && 
            policyRepository.existsByNiNumber(policy.getNiNumber())) {
            throw new DuplicatePolicyException("Policy with NI number " + policy.getNiNumber() + " already exists");
        }
        
        Policy savedPolicy = policyRepository.save(policy);
        logger.info("Policy created successfully with id: {}", savedPolicy.getId());
        return savedPolicy;
    }

    @Override
    @Transactional(readOnly = true)
    public Policy getPolicyById(Long id) {
        logger.info("Fetching policy by id: {}", id);
        return policyRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException("Policy not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Policy getPolicyByPolicyNumber(String policyNumber) {
        logger.info("Fetching policy by number: {}", policyNumber);
        if (!StringUtils.hasText(policyNumber)) {
            throw new IllegalArgumentException("Policy number cannot be empty");
        }
        return policyRepository.findByPolicyNumber(policyNumber)
                .orElseThrow(() -> new PolicyNotFoundException("Policy not found with number: " + policyNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public Policy getPolicyByExtPolicyNumber(String extPolicyNumber) {
        logger.info("Fetching policy by external number: {}", extPolicyNumber);
        if (!StringUtils.hasText(extPolicyNumber)) {
            throw new IllegalArgumentException("External policy number cannot be empty");
        }
        return policyRepository.findByExtPolicyNumber(extPolicyNumber)
                .orElseThrow(() -> new PolicyNotFoundException("Policy not found with external number: " + extPolicyNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Policy> getAllPolicies() {
        logger.info("Fetching all policies");
        return policyRepository.findAll();
    }

    @Override
    @Transactional
    public Policy updatePolicy(Long id, Policy policyDetails) {
        logger.info("Updating policy with id: {}", id);
        
        Policy existingPolicy = getPolicyById(id);
        
        // Check if policy number is being changed and if new number already exists
        if (StringUtils.hasText(policyDetails.getPolicyNumber()) && 
            !policyDetails.getPolicyNumber().equals(existingPolicy.getPolicyNumber())) {
            if (policyRepository.existsByPolicyNumber(policyDetails.getPolicyNumber())) {
                throw new DuplicatePolicyException("Policy number " + policyDetails.getPolicyNumber() + " already exists");
            }
            existingPolicy.setPolicyNumber(policyDetails.getPolicyNumber());
        }
        
        // Check if NI number is being changed and if new NI number already exists
        if (StringUtils.hasText(policyDetails.getNiNumber()) && 
            !policyDetails.getNiNumber().equals(existingPolicy.getNiNumber())) {
            if (policyRepository.existsByNiNumber(policyDetails.getNiNumber())) {
                throw new DuplicatePolicyException("NI number " + policyDetails.getNiNumber() + " already exists");
            }
            existingPolicy.setNiNumber(policyDetails.getNiNumber());
        }
        
        // Update all other fields
        updatePolicyFields(existingPolicy, policyDetails);
        
        Policy updatedPolicy = policyRepository.save(existingPolicy);
        logger.info("Policy updated successfully with id: {}", updatedPolicy.getId());
        return updatedPolicy;
    }

    @Override
    @Transactional
    public void deletePolicy(Long id) {
        logger.info("Deleting policy with id: {}", id);
        
        if (!policyRepository.existsById(id)) {
            throw new PolicyNotFoundException("Policy not found with id: " + id);
        }
        
        policyRepository.deleteById(id);
        logger.info("Policy deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Policy getPolicyByNiNumber(String niNumber) {
        logger.info("Fetching policy by NI number: {}", niNumber);
        if (!StringUtils.hasText(niNumber)) {
            throw new IllegalArgumentException("NI number cannot be empty");
        }
        return policyRepository.findByNiNumber(niNumber)
                .orElseThrow(() -> new PolicyNotFoundException("Policy not found with NI number: " + niNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPolicyNumber(String policyNumber) {
        return policyRepository.existsByPolicyNumber(policyNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNiNumber(String niNumber) {
        return policyRepository.existsByNiNumber(niNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAvailablePolicyTypes() {
        logger.info("Fetching available policy types");
        return policyRepository.findDistinctPolicyTypes();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAvailablePolicyStatuses(String policyType) {
        String normalizedPolicyType = normalizeNullableValue(policyType);
        if (!StringUtils.hasText(normalizedPolicyType)) {
            throw new IllegalArgumentException("Policy type is required to load policy statuses");
        }

        logger.info("Fetching policy statuses for policy type: {}", normalizedPolicyType);
        return policyRepository.findDistinctPolicyStatusesByPolicyType(normalizedPolicyType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Policy> searchPolicies(String policyType, String policyStatus) {
        String normalizedPolicyType = normalizeNullableValue(policyType);
        String normalizedPolicyStatus = normalizeNullableValue(policyStatus);

        if (!StringUtils.hasText(normalizedPolicyType)) {
            throw new IllegalArgumentException("Policy type is required to search policies");
        }

        logger.info("Searching policies for type: {} and status: {}", normalizedPolicyType, normalizedPolicyStatus);
        return policyRepository.findPoliciesByPolicyTypeAndStatus(normalizedPolicyType, normalizedPolicyStatus);
    }

    private void validatePolicy(Policy policy) {
        if (policy == null) {
            throw new IllegalArgumentException("Policy cannot be null");
        }
        if (!StringUtils.hasText(policy.getPolicyNumber())) {
            throw new IllegalArgumentException("Policy number is required");
        }
    }

    private String normalizeNullableValue(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private void updatePolicyFields(Policy existingPolicy, Policy policyDetails) {
        if (StringUtils.hasText(policyDetails.getExtPolicyNumber())) {
            existingPolicy.setExtPolicyNumber(policyDetails.getExtPolicyNumber());
        }
        if (StringUtils.hasText(policyDetails.getTitle())) {
            existingPolicy.setTitle(policyDetails.getTitle());
        }
        if (StringUtils.hasText(policyDetails.getForeName())) {
            existingPolicy.setForeName(policyDetails.getForeName());
        }
        if (StringUtils.hasText(policyDetails.getSurName())) {
            existingPolicy.setSurName(policyDetails.getSurName());
        }
        if (StringUtils.hasText(policyDetails.getAddressLine1())) {
            existingPolicy.setAddressLine1(policyDetails.getAddressLine1());
        }
        if (StringUtils.hasText(policyDetails.getAddressLine2())) {
            existingPolicy.setAddressLine2(policyDetails.getAddressLine2());
        }
        if (StringUtils.hasText(policyDetails.getAddressLine3())) {
            existingPolicy.setAddressLine3(policyDetails.getAddressLine3());
        }
        if (StringUtils.hasText(policyDetails.getAddressLine4())) {
            existingPolicy.setAddressLine4(policyDetails.getAddressLine4());
        }
        if (StringUtils.hasText(policyDetails.getAddressLine5())) {
            existingPolicy.setAddressLine5(policyDetails.getAddressLine5());
        }
        if (StringUtils.hasText(policyDetails.getAddressLine6())) {
            existingPolicy.setAddressLine6(policyDetails.getAddressLine6());
        }
        if (StringUtils.hasText(policyDetails.getPostCode())) {
            existingPolicy.setPostCode(policyDetails.getPostCode());
        }
        if (StringUtils.hasText(policyDetails.getProduct())) {
            existingPolicy.setProduct(policyDetails.getProduct());
        }
        if (policyDetails.getVersion() != null) {
            existingPolicy.setVersion(policyDetails.getVersion());
        }
        if (StringUtils.hasText(policyDetails.getPolicyStatus())) {
            existingPolicy.setPolicyStatus(policyDetails.getPolicyStatus());
        }
        if (StringUtils.hasText(policyDetails.getPolStatusDesc())) {
            existingPolicy.setPolStatusDesc(policyDetails.getPolStatusDesc());
        }
        if (StringUtils.hasText(policyDetails.getPolSuspended())) {
            existingPolicy.setPolSuspended(policyDetails.getPolSuspended());
        }
        if (StringUtils.hasText(policyDetails.getGroupChild())) {
            existingPolicy.setGroupChild(policyDetails.getGroupChild());
        }
        if (StringUtils.hasText(policyDetails.getGroupChildAgency())) {
            existingPolicy.setGroupChildAgency(policyDetails.getGroupChildAgency());
        }
        if (StringUtils.hasText(policyDetails.getPolicyAgency())) {
            existingPolicy.setPolicyAgency(policyDetails.getPolicyAgency());
        }
        if (StringUtils.hasText(policyDetails.getGroupParent())) {
            existingPolicy.setGroupParent(policyDetails.getGroupParent());
        }
        if (policyDetails.getPolStartDate() != null) {
            existingPolicy.setPolStartDate(policyDetails.getPolStartDate());
        }
        if (policyDetails.getPolNprMaturityDate() != null) {
            existingPolicy.setPolNprMaturityDate(policyDetails.getPolNprMaturityDate());
        }
        if (policyDetails.getPrMaturityDate() != null) {
            existingPolicy.setPrMaturityDate(policyDetails.getPrMaturityDate());
        }
        if (policyDetails.getNprOrigMatDte() != null) {
            existingPolicy.setNprOrigMatDte(policyDetails.getNprOrigMatDte());
        }
        if (policyDetails.getPrOrigMatDte() != null) {
            existingPolicy.setPrOrigMatDte(policyDetails.getPrOrigMatDte());
        }
        if (policyDetails.getRenewalDate() != null) {
            existingPolicy.setRenewalDate(policyDetails.getRenewalDate());
        }
        if (StringUtils.hasText(policyDetails.getSelfEmployed())) {
            existingPolicy.setSelfEmployed(policyDetails.getSelfEmployed());
        }
        if (StringUtils.hasText(policyDetails.getPaymentFreq())) {
            existingPolicy.setPaymentFreq(policyDetails.getPaymentFreq());
        }
        if (StringUtils.hasText(policyDetails.getPhPaymentMethod())) {
            existingPolicy.setPhPaymentMethod(policyDetails.getPhPaymentMethod());
        }
        if (StringUtils.hasText(policyDetails.getAccountNumber())) {
            existingPolicy.setAccountNumber(policyDetails.getAccountNumber());
        }
        if (StringUtils.hasText(policyDetails.getSortCode())) {
            existingPolicy.setSortCode(policyDetails.getSortCode());
        }
        if (policyDetails.getEmployeeRegular() != null) {
            existingPolicy.setEmployeeRegular(policyDetails.getEmployeeRegular());
        }
        if (policyDetails.getSelfEmpRegular() != null) {
            existingPolicy.setSelfEmpRegular(policyDetails.getSelfEmpRegular());
        }
        if (policyDetails.getThirdPartyRegular() != null) {
            existingPolicy.setThirdPartyRegular(policyDetails.getThirdPartyRegular());
        }
        if (StringUtils.hasText(policyDetails.getPrIndex())) {
            existingPolicy.setPrIndex(policyDetails.getPrIndex());
        }
        if (policyDetails.getPersonalSingle() != null) {
            existingPolicy.setPersonalSingle(policyDetails.getPersonalSingle());
        }
        if (policyDetails.getPersonalVariable() != null) {
            existingPolicy.setPersonalVariable(policyDetails.getPersonalVariable());
        }
        if (policyDetails.getPersonalWaiver() != null) {
            existingPolicy.setPersonalWaiver(policyDetails.getPersonalWaiver());
        }
        if (policyDetails.getEmployerRegular() != null) {
            existingPolicy.setEmployerRegular(policyDetails.getEmployerRegular());
        }
        if (policyDetails.getEmployerSingle() != null) {
            existingPolicy.setEmployerSingle(policyDetails.getEmployerSingle());
        }
        if (policyDetails.getEmployerVariable() != null) {
            existingPolicy.setEmployerVariable(policyDetails.getEmployerVariable());
        }
        if (policyDetails.getEmployerWaiver() != null) {
            existingPolicy.setEmployerWaiver(policyDetails.getEmployerWaiver());
        }
        if (StringUtils.hasText(policyDetails.getPpStandaloneTi())) {
            existingPolicy.setPpStandaloneTi(policyDetails.getPpStandaloneTi());
        }
        if (StringUtils.hasText(policyDetails.getPpIntegratedTi())) {
            existingPolicy.setPpIntegratedTi(policyDetails.getPpIntegratedTi());
        }
        if (StringUtils.hasText(policyDetails.getProtectedRights())) {
            existingPolicy.setProtectedRights(policyDetails.getProtectedRights());
        }
        if (StringUtils.hasText(policyDetails.getNico())) {
            existingPolicy.setNico(policyDetails.getNico());
        }
        if (policyDetails.getTransferIn() != null) {
            existingPolicy.setTransferIn(policyDetails.getTransferIn());
        }
        if (StringUtils.hasText(policyDetails.getTransferInInternal())) {
            existingPolicy.setTransferInInternal(policyDetails.getTransferInInternal());
        }
        if (policyDetails.getPrTransferIn() != null) {
            existingPolicy.setPrTransferIn(policyDetails.getPrTransferIn());
        }
        if (StringUtils.hasText(policyDetails.getPrTransferInInternal())) {
            existingPolicy.setPrTransferInInternal(policyDetails.getPrTransferInInternal());
        }
        if (policyDetails.getPcSgr() != null) {
            existingPolicy.setPcSgr(policyDetails.getPcSgr());
        }
        if (StringUtils.hasText(policyDetails.getPcSgrInternal())) {
            existingPolicy.setPcSgrInternal(policyDetails.getPcSgrInternal());
        }
        if (policyDetails.getPcNonSgr() != null) {
            existingPolicy.setPcNonSgr(policyDetails.getPcNonSgr());
        }
        if (policyDetails.getPcNonSgrInternal() != null) {
            existingPolicy.setPcNonSgrInternal(policyDetails.getPcNonSgrInternal());
        }
        if (StringUtils.hasText(policyDetails.getStandalonePhiWaiver())) {
            existingPolicy.setStandalonePhiWaiver(policyDetails.getStandalonePhiWaiver());
        }
        if (StringUtils.hasText(policyDetails.getDrawdown())) {
            existingPolicy.setDrawdown(policyDetails.getDrawdown());
        }
        if (StringUtils.hasText(policyDetails.getIncrementProcessed())) {
            existingPolicy.setIncrementProcessed(policyDetails.getIncrementProcessed());
        }
        if (StringUtils.hasText(policyDetails.getDecrementProcessed())) {
            existingPolicy.setDecrementProcessed(policyDetails.getDecrementProcessed());
        }
        if (StringUtils.hasText(policyDetails.getPremiumHolidayStarted())) {
            existingPolicy.setPremiumHolidayStarted(policyDetails.getPremiumHolidayStarted());
        }
        if (StringUtils.hasText(policyDetails.getPremiumHolidayEnded())) {
            existingPolicy.setPremiumHolidayEnded(policyDetails.getPremiumHolidayEnded());
        }
        if (StringUtils.hasText(policyDetails.getSwitchProcessed())) {
            existingPolicy.setSwitchProcessed(policyDetails.getSwitchProcessed());
        }
        if (StringUtils.hasText(policyDetails.getAutoswitchProcessed())) {
            existingPolicy.setAutoswitchProcessed(policyDetails.getAutoswitchProcessed());
        }
        if (StringUtils.hasText(policyDetails.getRedirection())) {
            existingPolicy.setRedirection(policyDetails.getRedirection());
        }
        if (StringUtils.hasText(policyDetails.getPafFund())) {
            existingPolicy.setPafFund(policyDetails.getPafFund());
        }
        if (StringUtils.hasText(policyDetails.getWpFund())) {
            existingPolicy.setWpFund(policyDetails.getWpFund());
        }
        if (StringUtils.hasText(policyDetails.getNonPafWpFund())) {
            existingPolicy.setNonPafWpFund(policyDetails.getNonPafWpFund());
        }
        if (StringUtils.hasText(policyDetails.getExternalFund())) {
            existingPolicy.setExternalFund(policyDetails.getExternalFund());
        }
        if (StringUtils.hasText(policyDetails.getPhasedSwi())) {
            existingPolicy.setPhasedSwi(policyDetails.getPhasedSwi());
        }
        if (StringUtils.hasText(policyDetails.getSppLoyaltyBonus())) {
            existingPolicy.setSppLoyaltyBonus(policyDetails.getSppLoyaltyBonus());
        }
        if (StringUtils.hasText(policyDetails.getLfr())) {
            existingPolicy.setLfr(policyDetails.getLfr());
        }
        if (StringUtils.hasText(policyDetails.getAlfr())) {
            existingPolicy.setAlfr(policyDetails.getAlfr());
        }
        if (StringUtils.hasText(policyDetails.getFbc())) {
            existingPolicy.setFbc(policyDetails.getFbc());
        }
        if (StringUtils.hasText(policyDetails.getCafcaRdr())) {
            existingPolicy.setCafcaRdr(policyDetails.getCafcaRdr());
        }
        if (policyDetails.getDateOfBirth() != null) {
            existingPolicy.setDateOfBirth(policyDetails.getDateOfBirth());
        }
        if (StringUtils.hasText(policyDetails.getSex())) {
            existingPolicy.setSex(policyDetails.getSex());
        }
    }
}
