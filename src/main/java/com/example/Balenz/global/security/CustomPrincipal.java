package com.example.Balenz.global.security;

import com.example.Balenz.global.exception.BaseException;
import com.example.Balenz.global.exception.ErrorCode;
import com.example.Balenz.user.entity.Role;
import com.example.Balenz.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class CustomPrincipal implements UserDetails, OAuth2User {

    private final Long id;
    private final String email;
    private final String password;
    private final Role role;
    private final Map<String, Object> attributes;

    private CustomPrincipal(Long id, String email, String password, Role role, Map<String, Object> attributes) {
        if (id == null) throw new BaseException(ErrorCode.INVALID_PRINCIPAL, "id 값이 null입니다.");
        if (email == null || email.isBlank()) throw new BaseException(ErrorCode.INVALID_PRINCIPAL, "email이 null 또는 빈 값입니다.");
        if (role == null) throw new BaseException(ErrorCode.INVALID_PRINCIPAL, "role이 null입니다.");
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.attributes = (attributes == null) ? Map.of() : attributes;
    }

    /** DB의 User 기반으로 CustomPrincipal 생성 */
    public static CustomPrincipal fromUser(User user) {
        return new CustomPrincipal(
                user.getId(), user.getEmail(), user.getPassword(), user.getRole(), Map.of());
    }

    /** JWT 기반으로 CustomPrincipal 생성 */
    public static CustomPrincipal fromJwt(Long id, String email, Role role) {
        return new CustomPrincipal(id, email, null, role, Map.of());
    }

    /** OAuth용 생성 메서드 */
    public static CustomPrincipal fromOAuth(Long id, String email, Role role, Map<String, Object> attributes) {
        return new CustomPrincipal(id, email, null, role, attributes);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }

}
