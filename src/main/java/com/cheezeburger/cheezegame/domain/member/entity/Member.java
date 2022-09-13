package com.cheezeburger.cheezegame.domain.member.entity;

import com.cheezeburger.cheezegame.global.entity.BaseLastModifiedEntity;
import com.cheezeburger.cheezegame.global.oauth2.entity.ProviderType;
import com.cheezeburger.cheezegame.global.oauth2.entity.Role;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "tb_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(of = {"seq", "email", "password", "gender", "ageRange", "role", "providerType", "providerId", "isDeleted"})
public class Member extends BaseLastModifiedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_seq", columnDefinition = "BIGINT UNSIGNED")
    private Long seq;

    @Column(nullable = false, length = 30, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    private String ageRange;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @Column(nullable = false)
    private String providerId;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private boolean isDeleted;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "member", cascade = CascadeType.ALL, optional = true)
    private RefreshToken refreshToken;

    @Builder
    public Member(String email, String password, Gender gender, String ageRange, Role role, ProviderType providerType, String providerId) {
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.ageRange = ageRange;
        this.role = role;
        this.providerType = providerType;
        this.providerId = providerId;
        this.isDeleted = false;
    }

    @Getter
    public enum Gender {

        MALE("male"),
        FEMALE("female");

        private final String description;

        Gender(String description) {
            this.description = description;
        }
    }

    public void changeRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }
}
