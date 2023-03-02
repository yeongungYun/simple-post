package com.posts.domain;

import jakarta.persistence.*;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String title;

    @Lob
    private String content;

    @Builder
    public Post(String username, String password, String title, String content) {
        this.username = username;
        this.password = password;
        this.title = title;
        this.content = content;
    }
}
