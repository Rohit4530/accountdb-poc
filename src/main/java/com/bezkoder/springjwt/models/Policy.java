package com.bezkoder.springjwt.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "POLICY")
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "EXT_POLICY_NUMBER", length = 50)
    private String extPolicyNumber;

    @Column(name = "POLICY_NUMBER", length = 50, nullable = false, unique = true)
    private String policyNumber;

    @Column(name = "NI_NUMBER", length = 20)
    private String niNumber;

    @Column(name = "TITLE", length = 20)
    private String title;

    @Column(name = "FORE_NAME", length = 50)
    private String foreName;

    @Column(name = "SUR_NAME", length = 50)
    private String surName;

    @Column(name = "ADDRESS_LINE_1", length = 100)
    private String addressLine1;

    @Column(name = "ADDRESS_LINE_2", length = 100)
    private String addressLine2;

    @Column(name = "ADDRESS_LINE_3", length = 100)
    private String addressLine3;

    @Column(name = "ADDRESS_LINE_4", length = 100)
    private String addressLine4;

    @Column(name = "ADDRESS_LINE_5", length = 100)
    private String addressLine5;

    @Column(name = "ADDRESS_LINE_6", length = 100)
    private String addressLine6;

    @Column(name = "POST_CODE", length = 20)
    private String postCode;

    @Column(name = "PRODUCT", length = 50)
    private String product;

    @Column(name = "VERSION")
    private Integer version;

    @Column(name = "PRODUCTSTATUS", length = 50)
    private String policyStatus;

    @Column(name = "POL_STATUS_DESC", length = 100)
    private String polStatusDesc;

    @Column(name = "POL_SUSPENDED", length = 1)
    private String polSuspended;

    @Column(name = "GROUP_CHILD", length = 50)
    private String groupChild;

    @Column(name = "GROUP_CHILD_AGENCY", length = 50)
    private String groupChildAgency;

    @Column(name = "POLICY_AGENCY", length = 50)
    private String policyAgency;

    @Column(name = "GROUP_PARENT", length = 50)
    private String groupParent;

    @Column(name = "POL_START_DATE")
    private LocalDate polStartDate;

    @Column(name = "POL_NPR_MATURITY_DATE")
    private LocalDate polNprMaturityDate;

    @Column(name = "PR_MATURITY_DATE")
    private LocalDate prMaturityDate;

    @Column(name = "NPR_ORIG_MAT_DTE")
    private LocalDate nprOrigMatDte;

    @Column(name = "PR_ORIG_MAT_DTE")
    private LocalDate prOrigMatDte;

    @Column(name = "RENEWAL_DATE")
    private LocalDate renewalDate;

    @Column(name = "SELF_EMPLOYED", length = 1)
    private String selfEmployed;

    @Column(name = "PAYMENT_FREQ", length = 20)
    private String paymentFreq;

    @Column(name = "PH_PAYMENT_METHOD", length = 20)
    private String phPaymentMethod;

    @Column(name = "ACCOUNT_NUMBER", length = 30)
    private String accountNumber;

    @Column(name = "SORT_CODE", length = 20)
    private String sortCode;

    @Column(name = "EMPLOYEE_REGULAR", precision = 20, scale = 2)
    private BigDecimal employeeRegular;

    @Column(name = "SELF_EMP_REGULAR", precision = 20, scale = 2)
    private BigDecimal selfEmpRegular;

    @Column(name = "THIRD_PARTY_REGULAR", precision = 20, scale = 2)
    private BigDecimal thirdPartyRegular;

    @Column(name = "PR_INDEX", length = 20)
    private String prIndex;

    @Column(name = "PERSONAL_SINGLE", precision = 20, scale = 2)
    private BigDecimal personalSingle;

    @Column(name = "PERSONAL_VARIABLE", precision = 20, scale = 2)
    private BigDecimal personalVariable;

    @Column(name = "PERSONAL_WAIVER", precision = 20, scale = 2)
    private BigDecimal personalWaiver;

    @Column(name = "EMPLOYER_REGULAR", precision = 20, scale = 2)
    private BigDecimal employerRegular;

    @Column(name = "EMPLOYER_SINGLE", precision = 20, scale = 2)
    private BigDecimal employerSingle;

    @Column(name = "EMPLOYER_VARIABLE", precision = 20, scale = 2)
    private BigDecimal employerVariable;

    @Column(name = "EMPLOYER_WAIVER", precision = 20, scale = 2)
    private BigDecimal employerWaiver;

    @Column(name = "PP_STANDALONE_TI", length = 20)
    private String ppStandaloneTi;

    @Column(name = "PP_INTEGRATED_TI", length = 20)
    private String ppIntegratedTi;

    @Column(name = "PROTECTED_RIGHTS", length = 20)
    private String protectedRights;

    @Column(name = "NICO", length = 20)
    private String nico;

    @Column(name = "TRANSFER_IN", precision = 20, scale = 2)
    private BigDecimal transferIn;

    @Column(name = "TRANSFER_IN_INTERNAL", length = 1)
    private String transferInInternal;

    @Column(name = "PR_TRANSFER_IN", precision = 20, scale = 2)
    private BigDecimal prTransferIn;

    @Column(name = "PR_TRANSFER_IN_INTERNAL", length = 1)
    private String prTransferInInternal;

    @Column(name = "PC_SGR", precision = 20, scale = 2)
    private BigDecimal pcSgr;

    @Column(name = "PC_SGR_INTERNAL", length = 1)
    private String pcSgrInternal;

    @Column(name = "PC_NON_SGR", precision = 20, scale = 2)
    private BigDecimal pcNonSgr;

    @Column(name = "PC_NON_SGR_INTERNAL", precision = 20, scale = 2)
    private BigDecimal pcNonSgrInternal;

    @Column(name = "STANDALONE_PHI_WAIVER", length = 20)
    private String standalonePhiWaiver;

    @Column(name = "DRAWDOWN", length = 20)
    private String drawdown;

    @Column(name = "INCREMENT_PROCESSED", length = 20)
    private String incrementProcessed;

    @Column(name = "DECREMENT_PROCESSED", length = 20)
    private String decrementProcessed;

    @Column(name = "PREMIUM_HOLIDAY_STARTED", length = 20)
    private String premiumHolidayStarted;

    @Column(name = "PREMIUM_HOLIDAY_ENDED", length = 20)
    private String premiumHolidayEnded;

    @Column(name = "SWITCH_PROCESSED", length = 20)
    private String switchProcessed;

    @Column(name = "AUTOSWITCH_PROCESSED", length = 20)
    private String autoswitchProcessed;

    @Column(name = "REDIRECTION", length = 20)
    private String redirection;

    @Column(name = "PAF_FUND", length = 20)
    private String pafFund;

    @Column(name = "WP_FUND", length = 20)
    private String wpFund;

    @Column(name = "NON_PAF_WP_FUND", length = 20)
    private String nonPafWpFund;

    @Column(name = "EXTERNAL_FUND", length = 20)
    private String externalFund;

    @Column(name = "PHASED_SWI", length = 20)
    private String phasedSwi;

    @Column(name = "SPP_LOYALTY_BONUS", length = 20)
    private String sppLoyaltyBonus;

    @Column(name = "LFR", length = 20)
    private String lfr;

    @Column(name = "ALFR", length = 20)
    private String alfr;

    @Column(name = "FBC", length = 20)
    private String fbc;

    @Column(name = "CAFCA_RDR", length = 20)
    private String cafcaRdr;

    @Column(name = "DATE_OF_BIRTH")
    private LocalDate dateOfBirth;

    @Column(name = "SEX", length = 1)
    private String sex;

    // Default constructor
    public Policy() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExtPolicyNumber() {
        return extPolicyNumber;
    }

    public void setExtPolicyNumber(String extPolicyNumber) {
        this.extPolicyNumber = extPolicyNumber;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getNiNumber() {
        return niNumber;
    }

    public void setNiNumber(String niNumber) {
        this.niNumber = niNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getForeName() {
        return foreName;
    }

    public void setForeName(String foreName) {
        this.foreName = foreName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getAddressLine4() {
        return addressLine4;
    }

    public void setAddressLine4(String addressLine4) {
        this.addressLine4 = addressLine4;
    }

    public String getAddressLine5() {
        return addressLine5;
    }

    public void setAddressLine5(String addressLine5) {
        this.addressLine5 = addressLine5;
    }

    public String getAddressLine6() {
        return addressLine6;
    }

    public void setAddressLine6(String addressLine6) {
        this.addressLine6 = addressLine6;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getPolicyStatus() {
        return policyStatus;
    }

    public void setPolicyStatus(String policyStatus) {
        this.policyStatus = policyStatus;
    }

    public String getPolStatusDesc() {
        return polStatusDesc;
    }

    public void setPolStatusDesc(String polStatusDesc) {
        this.polStatusDesc = polStatusDesc;
    }

    public String getPolSuspended() {
        return polSuspended;
    }

    public void setPolSuspended(String polSuspended) {
        this.polSuspended = polSuspended;
    }

    public String getGroupChild() {
        return groupChild;
    }

    public void setGroupChild(String groupChild) {
        this.groupChild = groupChild;
    }

    public String getGroupChildAgency() {
        return groupChildAgency;
    }

    public void setGroupChildAgency(String groupChildAgency) {
        this.groupChildAgency = groupChildAgency;
    }

    public String getPolicyAgency() {
        return policyAgency;
    }

    public void setPolicyAgency(String policyAgency) {
        this.policyAgency = policyAgency;
    }

    public String getGroupParent() {
        return groupParent;
    }

    public void setGroupParent(String groupParent) {
        this.groupParent = groupParent;
    }

    public LocalDate getPolStartDate() {
        return polStartDate;
    }

    public void setPolStartDate(LocalDate polStartDate) {
        this.polStartDate = polStartDate;
    }

    public LocalDate getPolNprMaturityDate() {
        return polNprMaturityDate;
    }

    public void setPolNprMaturityDate(LocalDate polNprMaturityDate) {
        this.polNprMaturityDate = polNprMaturityDate;
    }

    public LocalDate getPrMaturityDate() {
        return prMaturityDate;
    }

    public void setPrMaturityDate(LocalDate prMaturityDate) {
        this.prMaturityDate = prMaturityDate;
    }

    public LocalDate getNprOrigMatDte() {
        return nprOrigMatDte;
    }

    public void setNprOrigMatDte(LocalDate nprOrigMatDte) {
        this.nprOrigMatDte = nprOrigMatDte;
    }

    public LocalDate getPrOrigMatDte() {
        return prOrigMatDte;
    }

    public void setPrOrigMatDte(LocalDate prOrigMatDte) {
        this.prOrigMatDte = prOrigMatDte;
    }

    public LocalDate getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(LocalDate renewalDate) {
        this.renewalDate = renewalDate;
    }

    public String getSelfEmployed() {
        return selfEmployed;
    }

    public void setSelfEmployed(String selfEmployed) {
        this.selfEmployed = selfEmployed;
    }

    public String getPaymentFreq() {
        return paymentFreq;
    }

    public void setPaymentFreq(String paymentFreq) {
        this.paymentFreq = paymentFreq;
    }

    public String getPhPaymentMethod() {
        return phPaymentMethod;
    }

    public void setPhPaymentMethod(String phPaymentMethod) {
        this.phPaymentMethod = phPaymentMethod;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public BigDecimal getEmployeeRegular() {
        return employeeRegular;
    }

    public void setEmployeeRegular(BigDecimal employeeRegular) {
        this.employeeRegular = employeeRegular;
    }

    public BigDecimal getSelfEmpRegular() {
        return selfEmpRegular;
    }

    public void setSelfEmpRegular(BigDecimal selfEmpRegular) {
        this.selfEmpRegular = selfEmpRegular;
    }

    public BigDecimal getThirdPartyRegular() {
        return thirdPartyRegular;
    }

    public void setThirdPartyRegular(BigDecimal thirdPartyRegular) {
        this.thirdPartyRegular = thirdPartyRegular;
    }

    public String getPrIndex() {
        return prIndex;
    }

    public void setPrIndex(String prIndex) {
        this.prIndex = prIndex;
    }

    public BigDecimal getPersonalSingle() {
        return personalSingle;
    }

    public void setPersonalSingle(BigDecimal personalSingle) {
        this.personalSingle = personalSingle;
    }

    public BigDecimal getPersonalVariable() {
        return personalVariable;
    }

    public void setPersonalVariable(BigDecimal personalVariable) {
        this.personalVariable = personalVariable;
    }

    public BigDecimal getPersonalWaiver() {
        return personalWaiver;
    }

    public void setPersonalWaiver(BigDecimal personalWaiver) {
        this.personalWaiver = personalWaiver;
    }

    public BigDecimal getEmployerRegular() {
        return employerRegular;
    }

    public void setEmployerRegular(BigDecimal employerRegular) {
        this.employerRegular = employerRegular;
    }

    public BigDecimal getEmployerSingle() {
        return employerSingle;
    }

    public void setEmployerSingle(BigDecimal employerSingle) {
        this.employerSingle = employerSingle;
    }

    public BigDecimal getEmployerVariable() {
        return employerVariable;
    }

    public void setEmployerVariable(BigDecimal employerVariable) {
        this.employerVariable = employerVariable;
    }

    public BigDecimal getEmployerWaiver() {
        return employerWaiver;
    }

    public void setEmployerWaiver(BigDecimal employerWaiver) {
        this.employerWaiver = employerWaiver;
    }

    public String getPpStandaloneTi() {
        return ppStandaloneTi;
    }

    public void setPpStandaloneTi(String ppStandaloneTi) {
        this.ppStandaloneTi = ppStandaloneTi;
    }

    public String getPpIntegratedTi() {
        return ppIntegratedTi;
    }

    public void setPpIntegratedTi(String ppIntegratedTi) {
        this.ppIntegratedTi = ppIntegratedTi;
    }

    public String getProtectedRights() {
        return protectedRights;
    }

    public void setProtectedRights(String protectedRights) {
        this.protectedRights = protectedRights;
    }

    public String getNico() {
        return nico;
    }

    public void setNico(String nico) {
        this.nico = nico;
    }

    public BigDecimal getTransferIn() {
        return transferIn;
    }

    public void setTransferIn(BigDecimal transferIn) {
        this.transferIn = transferIn;
    }

    public String getTransferInInternal() {
        return transferInInternal;
    }

    public void setTransferInInternal(String transferInInternal) {
        this.transferInInternal = transferInInternal;
    }

    public BigDecimal getPrTransferIn() {
        return prTransferIn;
    }

    public void setPrTransferIn(BigDecimal prTransferIn) {
        this.prTransferIn = prTransferIn;
    }

    public String getPrTransferInInternal() {
        return prTransferInInternal;
    }

    public void setPrTransferInInternal(String prTransferInInternal) {
        this.prTransferInInternal = prTransferInInternal;
    }

    public BigDecimal getPcSgr() {
        return pcSgr;
    }

    public void setPcSgr(BigDecimal pcSgr) {
        this.pcSgr = pcSgr;
    }

    public String getPcSgrInternal() {
        return pcSgrInternal;
    }

    public void setPcSgrInternal(String pcSgrInternal) {
        this.pcSgrInternal = pcSgrInternal;
    }

    public BigDecimal getPcNonSgr() {
        return pcNonSgr;
    }

    public void setPcNonSgr(BigDecimal pcNonSgr) {
        this.pcNonSgr = pcNonSgr;
    }

    public BigDecimal getPcNonSgrInternal() {
        return pcNonSgrInternal;
    }

    public void setPcNonSgrInternal(BigDecimal pcNonSgrInternal) {
        this.pcNonSgrInternal = pcNonSgrInternal;
    }

    public String getStandalonePhiWaiver() {
        return standalonePhiWaiver;
    }

    public void setStandalonePhiWaiver(String standalonePhiWaiver) {
        this.standalonePhiWaiver = standalonePhiWaiver;
    }

    public String getDrawdown() {
        return drawdown;
    }

    public void setDrawdown(String drawdown) {
        this.drawdown = drawdown;
    }

    public String getIncrementProcessed() {
        return incrementProcessed;
    }

    public void setIncrementProcessed(String incrementProcessed) {
        this.incrementProcessed = incrementProcessed;
    }

    public String getDecrementProcessed() {
        return decrementProcessed;
    }

    public void setDecrementProcessed(String decrementProcessed) {
        this.decrementProcessed = decrementProcessed;
    }

    public String getPremiumHolidayStarted() {
        return premiumHolidayStarted;
    }

    public void setPremiumHolidayStarted(String premiumHolidayStarted) {
        this.premiumHolidayStarted = premiumHolidayStarted;
    }

    public String getPremiumHolidayEnded() {
        return premiumHolidayEnded;
    }

    public void setPremiumHolidayEnded(String premiumHolidayEnded) {
        this.premiumHolidayEnded = premiumHolidayEnded;
    }

    public String getSwitchProcessed() {
        return switchProcessed;
    }

    public void setSwitchProcessed(String switchProcessed) {
        this.switchProcessed = switchProcessed;
    }

    public String getAutoswitchProcessed() {
        return autoswitchProcessed;
    }

    public void setAutoswitchProcessed(String autoswitchProcessed) {
        this.autoswitchProcessed = autoswitchProcessed;
    }

    public String getRedirection() {
        return redirection;
    }

    public void setRedirection(String redirection) {
        this.redirection = redirection;
    }

    public String getPafFund() {
        return pafFund;
    }

    public void setPafFund(String pafFund) {
        this.pafFund = pafFund;
    }

    public String getWpFund() {
        return wpFund;
    }

    public void setWpFund(String wpFund) {
        this.wpFund = wpFund;
    }

    public String getNonPafWpFund() {
        return nonPafWpFund;
    }

    public void setNonPafWpFund(String nonPafWpFund) {
        this.nonPafWpFund = nonPafWpFund;
    }

    public String getExternalFund() {
        return externalFund;
    }

    public void setExternalFund(String externalFund) {
        this.externalFund = externalFund;
    }

    public String getPhasedSwi() {
        return phasedSwi;
    }

    public void setPhasedSwi(String phasedSwi) {
        this.phasedSwi = phasedSwi;
    }

    public String getSppLoyaltyBonus() {
        return sppLoyaltyBonus;
    }

    public void setSppLoyaltyBonus(String sppLoyaltyBonus) {
        this.sppLoyaltyBonus = sppLoyaltyBonus;
    }

    public String getLfr() {
        return lfr;
    }

    public void setLfr(String lfr) {
        this.lfr = lfr;
    }

    public String getAlfr() {
        return alfr;
    }

    public void setAlfr(String alfr) {
        this.alfr = alfr;
    }

    public String getFbc() {
        return fbc;
    }

    public void setFbc(String fbc) {
        this.fbc = fbc;
    }

    public String getCafcaRdr() {
        return cafcaRdr;
    }

    public void setCafcaRdr(String cafcaRdr) {
        this.cafcaRdr = cafcaRdr;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
