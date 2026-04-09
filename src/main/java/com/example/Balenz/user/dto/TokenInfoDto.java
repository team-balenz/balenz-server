package com.example.Balenz.user.dto;

import com.example.Balenz.user.entity.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenInfoDto {

    private Long id;

    private String email;

    private Role role;

}
