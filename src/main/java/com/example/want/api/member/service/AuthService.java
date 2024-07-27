package com.example.want.api.member.service;

import com.example.want.api.member.login.dto.GoogleLoginRqDto;
import com.example.want.api.member.login.jwt.TokenProvider;
import com.example.want.api.member.login.jwt.TokenResponse;
import com.example.want.api.member.login.oauth.GoogleOAuth2UserInfo;
import com.example.want.api.member.domain.Member;
import com.example.want.api.member.domain.Role;
import com.example.want.api.member.repository.MemberRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    @Transactional
    public TokenResponse googleLogin(GoogleLoginRqDto idToken){
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        try {
            GoogleIdToken googleIdToken = verifier.verify(idToken.getIdToken());
            System.out.println("googleIdToken : " + googleIdToken);

            if (googleIdToken == null) {
                throw new InternalAuthenticationServiceException("토큰이 없습니다.");
            } else {
                GoogleOAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(googleIdToken.getPayload());
                String email = userInfo.getEmail();

                // 이미 가입된 회원인지 확인
//                가입 안되어있으면 가입
                if (!memberRepository.existsByEmail(userInfo.getEmail())) {
//                    Member member = new Member(userInfo, randomNickname());
                    Member member = new Member(userInfo);
                    memberRepository.save(member);
                } else { // 가입되어있으면 닉네임이 있는지 확인
                    Member member = memberRepository.findByEmail(email).orElseThrow(() ->
                            new EntityNotFoundException("가입되어있지 않은 회원입니다."));
//                    랜덤 닉네임 생성
//                    if (member.getNickname() == null) {
//                        UserEntity userEntity = new UserEntity(userInfo, randomNickname());
//                        userRepository.save(userEntity);
//                    }
                }
                return sendGenerateJwtToken(userInfo.getEmail(), userInfo.getName());
            }
        }catch (InternalAuthenticationServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException("토큰이 유효하지 않습니다.");
        }
    }

    private TokenResponse sendGenerateJwtToken(String email, String name ) {
        return createToken(email, name );
    }

    private TokenResponse createToken(String email, String name) {
        return tokenProvider.generateJwtToken(email, name, Role.MEMBER);
    }

//    private String randomNickname() {
//        final String[] adjectives = {
//                "친절한", "잘생긴", "똑똑한", "용감한", "우아한", "행복한"
//        };
//
//        final String[] nouns = {
//                "고양이", "강아지", "호랑이", "사자", "펭귄", "악어"
//        };
//
//        Random random = new Random();
//        String adjective = adjectives[random.nextInt(adjectives.length)];
//        String noun = nouns[random.nextInt(nouns.length)];
//        int number;
//
//        // 중복되지 않는 닉네임을 생성하기 위해 중복 체크
//        String nickname;
//        do {
//            number = random.nextInt(1000);
//            nickname = adjective + noun + "#" + number;
//        } while (memberRepository.existsByNickname(nickname));
//
//        return nickname;
//    }

//    @Transactional
//    public TokenResponse reissue(String email, String name, String refreshToken) {
//        validateRefreshToken(refreshToken);
//        message = UserResponseMessage.REISSUE_SUCCESS.getMessage();
//        return createToken(email, name );
//    }



//    private void validateRefreshToken(String refreshToken){
//        if(!tokenProvider.validateToken(refreshToken))
//            throw new ApiException(UserResponseMessage.REFRESH_TOKEN_INVALID);
//    }




//    @Transactional
//    public void withdrawUser(String email){
//        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() ->
//                new ApiException(UserResponseMessage.USER_NOT_FOUND));
//
//        userEntity.setWithdrawalDate(LocalDateTime.now());
//        userRepository.save(userEntity);
//    }

}
