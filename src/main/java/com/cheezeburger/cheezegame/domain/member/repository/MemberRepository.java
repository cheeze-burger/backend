package com.cheezeburger.cheezegame.domain.member.repository;

import com.cheezeburger.cheezegame.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT m FROM Member m WHERE m.email = :email AND m.isDeleted = FALSE")
    Optional<Member> findByEmail(@Param("email") String email);

    @Query("SELECT m FROM Member m WHERE m.providerId = :providerId AND m.isDeleted = FALSE")
    Optional<Member> findByProviderId(@Param("providerId") String providerId);
}
