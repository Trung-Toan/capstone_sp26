package com.example.identity_service.security.oauth2;

import com.example.identity_service.entity.AuthProvider;
import com.example.identity_service.entity.Role;
import com.example.identity_service.entity.User;
import com.example.identity_service.repository.RoleRepository;
import com.example.identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        return processOAuth2User(userRequest, oauth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        OAuth2UserInfo oauth2UserInfo = new GoogleOAuth2UserInfo(oauth2User.getAttributes());

        if (!StringUtils.hasText(oauth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(oauth2UserInfo.getEmail());
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Update existing user
            if (!user.getProvider().equals(AuthProvider.GOOGLE)) {
                user.setProvider(AuthProvider.GOOGLE);
                user.setProviderId(oauth2UserInfo.getId());
            }
            user.setFullName(oauth2UserInfo.getName());
            user = userRepository.save(user);
        } else {
            // Create new user
            user = registerNewUser(oauth2UserInfo);
        }

        return new CustomOAuth2User(oauth2User.getAuthorities(), oauth2User.getAttributes(), "sub", user);
    }

    private User registerNewUser(OAuth2UserInfo oauth2UserInfo) {
        // Get default USER role
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .userName(oauth2UserInfo.getEmail().split("@")[0])
                .email(oauth2UserInfo.getEmail())
                .fullName(oauth2UserInfo.getName())
                .provider(AuthProvider.GOOGLE)
                .providerId(oauth2UserInfo.getId())
                .roles(roles)
                .build();

        return userRepository.save(user);
    }
}
