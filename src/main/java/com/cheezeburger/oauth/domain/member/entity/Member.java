package com.cheezeburger.oauth.domain.member.entity;

import com.cheezeburger.oauth.global.entity.BaseLastModifiedEntity;
import com.cheezeburger.oauth.global.enums.Role;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "tb_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(of = {"seq", "email", "password", "role", "isDeleted"})
public class Member extends BaseLastModifiedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_seq", columnDefinition = "BIGINT UNSIGNED")
    private Long seq;

    @Column(nullable = false, length = 30, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private String ageRage;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String providerId;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private boolean isDeleted;

    @Builder
    public Member(String email, String password, Gender gender, String ageRage, Role role, String provider, String providerId) {
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.ageRage = ageRage;
        this.role = role;
        this.provider = provider;
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
}
