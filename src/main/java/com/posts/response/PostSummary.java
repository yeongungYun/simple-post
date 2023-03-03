package com.posts.response;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostSummary {

    private Long id;
    private String username;
    private String title;

    @Builder
    public PostSummary(Long id, String username, String title) {
        this.id = id;
        this.username = username;
        this.title = title;
    }
}
