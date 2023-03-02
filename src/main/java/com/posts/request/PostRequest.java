package com.posts.request;

import lombok.*;

/**
 * 글 작성 요청 dto
 */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostRequest {

    private String username;
    private String rawPassword;
    private String title;
    private String content;

    @Builder
    public PostRequest(String username, String rawPassword, String title, String content) {
        this.username = username;
        this.rawPassword = rawPassword;
        this.title = title;
        this.content = content;
    }
}
