package com.cheezeburger.cheezegame.domain.member.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "tb_refresh_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(of = {"seq", "token"})
public class RefreshToken {

    @Id
    @Column(name = "refresh_token_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @OneToOne
    @JoinColumn(name = "member_seq", nullable = false)
    private Member member;

    @Column(name = "token", nullable = false)
    private String token;

    @Builder
    public RefreshToken(Member member, String token) {
        this.member = member;
        this.token = token;

        member.changeRefreshToken(this);
    }

    public void changeToken(String token) {
        this.token = token;
    }
}
