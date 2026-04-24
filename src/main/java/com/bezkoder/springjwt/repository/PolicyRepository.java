package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    Optional<Policy> findByPolicyNumber(String policyNumber);
    Optional<Policy> findByExtPolicyNumber(String extPolicyNumber);
    Optional<Policy> findByNiNumber(String niNumber);
    boolean existsByPolicyNumber(String policyNumber);
    boolean existsByNiNumber(String niNumber);

    @Query("""
            select distinct p.product
            from Policy p
            where p.product is not null
              and length(trim(p.product)) > 0
            order by p.product
            """)
    List<String> findDistinctPolicyTypes();

    @Query("""
            select distinct p.policyStatus
            from Policy p
            where p.product = :policyType
              and p.policyStatus is not null
              and length(trim(p.policyStatus)) > 0
            order by p.policyStatus
            """)
    List<String> findDistinctPolicyStatusesByPolicyType(@Param("policyType") String policyType);

    @Query("""
            select p
            from Policy p
            where p.product = :policyType
              and (:policyStatus is null or p.policyStatus = :policyStatus)
            order by p.policyNumber
            """)
    List<Policy> findPoliciesByPolicyTypeAndStatus(@Param("policyType") String policyType,
                                                   @Param("policyStatus") String policyStatus);
}
