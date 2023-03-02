package com.posts.service;


import com.posts.repository.PostRepository;
import com.posts.request.PostRequest;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("게시글 작성")
    void write() {
        // given
        PostRequest request = PostRequest.builder()
                .username("test username")
                .title("test title")
                .content("test content")
                .build();

        // when
        Long postId = postService.write(request);
        long count = postRepository.count();

        // then
        assertThat(postId).isEqualTo(1L);
        assertThat(count).isEqualTo(1L);

        log.info("postId={}", postId);
    }
}