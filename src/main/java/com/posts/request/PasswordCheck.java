package com.posts.request;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordCheck {

    private Long id;
    private String rawPassword;

    @Builder
    public PasswordCheck(Long id, String rawPassword) {
        this.id = id;
        this.rawPassword = rawPassword;
    }
}
