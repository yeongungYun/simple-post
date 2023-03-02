package com.posts.request;

import lombok.*;

/**
 * 글 수정, 삭제시 비밀번호 확인을 위한 dto
 */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class PasswordCheckRequest {

    private Long postId;
    private String rawPassword;

    @Builder
    public PasswordCheckRequest(Long postId, String rawPassword) {
        this.postId = postId;
        this.rawPassword = rawPassword;
    }
}
