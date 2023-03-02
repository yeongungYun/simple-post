package com.posts.response;

import lombok.*;

/**
 * 글 단건 조회 dto
 */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostDetailResponse {

    private Long id;
    private String username;
    private String title;
    private String content;

    @Builder
    public PostDetailResponse(Long id, String username, String title, String content) {
        this.id = id;
        this.username = username;
        this.title = title;
        this.content = content;
    }
}
