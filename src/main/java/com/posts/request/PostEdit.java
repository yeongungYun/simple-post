package com.posts.request;

import lombok.*;

@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PostEdit {

    private Long id;
    private String title;
    private String content;

    @Builder
    public PostEdit(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
}
