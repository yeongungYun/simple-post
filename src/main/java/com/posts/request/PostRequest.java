package com.posts.request;

import com.posts.domain.Post;
import lombok.*;

/**
 * 글 작성 요청 dto
 */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostRequest {

    private String username;
    private String title;
    private String content;

    @Builder
    public PostRequest(String username, String title, String content) {
        this.username = username;
        this.title = title;
        this.content = content;
    }

    /**
     * 엔티티 인스턴스로 변환하여 리턴
     */
    public Post toEntity() {
        return Post.builder()
                .username(username)
                .title(title)
                .content(content)
                .build();
    }
}
