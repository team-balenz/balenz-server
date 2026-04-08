package com.example.Balenz.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignUpDto {

    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "password1은 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@$!%*?&])[A-Za-z[0-9]@$!%*?&]{8,20}$",
            message = "비밀번호는 8~ 20자, 대소문자/숫자/특수문자(@$!%*?&)를 모두 포함해야합니다."
    )
    private String password1;

    @NotBlank(message = "password2는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@$!%*?&])[A-Za-z[0-9]@$!%*?&]{8,20}$",
            message = "비밀번호는 8~20자, 대소문자/숫자/특수문자(@$!%*?&)를 포함해야합니다."
    )
    private String password2;

}
