package com.cheezeburger.cheezegame.global.oauth2.service;

import com.cheezeburger.cheezegame.domain.member.entity.Member;
import com.cheezeburger.cheezegame.domain.member.repository.MemberRepository;
import com.cheezeburger.cheezegame.global.oauth2.entity.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Can not find email"));

        return MemberPrincipal.create(member);
    }
}
