package com.cheezeburger.cheezegame.domain.member.repository;

import com.cheezeburger.cheezegame.domain.member.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query("SELECT rt FROM RefreshToken rt WHERE rt.member.seq = :memberSeq AND rt.token = :token AND rt.member.isDeleted = FALSE")
    Optional<RefreshToken> findByMemberSeqAndToken(@Param("memberSeq") Long memberSeq, @Param("token") String token);
}
