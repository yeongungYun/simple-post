package com.posts.service;


import com.posts.domain.Post;
import com.posts.exception.NotFoundPostException;
import com.posts.repository.PostRepository;
import com.posts.request.PostRequest;
import com.posts.response.PostDetailResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
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

    @AfterEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("게시글 작성")
    void write() {
        // given
        PostRequest request = PostRequest.builder()
                .username("test username")
                .rawPassword("test password")
                .title("test title")
                .content("test content")
                .build();

        // when
        Long postId = postService.write(request);
        long count = postRepository.count();

        // then
        assertThat(count).isEqualTo(1L);

        log.info("postId={}", postId);
    }

    @Test
    @DisplayName("게시글 단건 조회")
    void get() {
        // given
        Post post = Post.builder()
                .username("test username")
                .title("test title")
                .content("test content")
                .build();
        postRepository.save(post);

        // when
        PostDetailResponse response = postService.get(post.getId());

        // then
        assertThat(response.getId()).isEqualTo(post.getId());
        assertThat(response.getUsername()).isEqualTo(post.getUsername());
        assertThat(response.getTitle()).isEqualTo(post.getTitle());
        assertThat(response.getContent()).isEqualTo(post.getContent());

        log.info("response={}", response);
    }

    @Test
    @DisplayName("존재하지 않는 id 조회로 예외 발생")
    void getException() {
        // expected
        assertThatThrownBy(() -> postService.get(1_000L))
                .isInstanceOf(NotFoundPostException.class);
    }
}