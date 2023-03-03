package com.posts.service;


import com.posts.domain.Post;
import com.posts.exception.NotFoundPostException;
import com.posts.repository.PostRepository;
import com.posts.request.PasswordCheck;
import com.posts.request.PostEdit;
import com.posts.request.PostWrite;
import com.posts.response.PostDetail;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        PostWrite request = PostWrite.builder()
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
                .password(passwordEncoder.encode("test password"))
                .title("test title")
                .content("test content")
                .build();
        postRepository.save(post);

        // when
        PostDetail response = postService.get(post.getId());

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

    @Test
    @DisplayName("작성된 글의 제목과 내용 수정")
    void edit() {
        // given
        Post post = Post.builder()
                .username("test username")
                .password(passwordEncoder.encode("test password"))
                .title("test title")
                .content("test content")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .id(post.getId())
                .title("edited title")
                .content("edited content")
                .build();

        // when
        Long postId = postService.edit(postEdit);

        // then
        Post editedPost = postRepository.findById(postId).get();
        assertThat(editedPost.getTitle()).isEqualTo("edited title");
        assertThat(editedPost.getContent()).isEqualTo("edited content");

        log.info("editedPost={}", editedPost);
    }

    @Test
    @DisplayName("존재하지 않는 글 수정으로 예외 발생")
    void editException() {
        // given
        PostEdit postEdit = PostEdit.builder()
                .id(1_000L)
                .title("edited title")
                .content("edited content")
                .build();

        // expected
        assertThatThrownBy(() -> postService.edit(postEdit))
                .isInstanceOf(NotFoundPostException.class);
    }

    @Test
    @DisplayName("작성한 글 삭제")
    void delete() {
        // given
        Post post = Post.builder()
                .username("test username")
                .password(passwordEncoder.encode("test password"))
                .title("test title")
                .content("test content")
                .build();
        postRepository.save(post);

        // when
        postService.delete(post.getId());

        // then
        Long count = postRepository.count();
        assertThat(count).isEqualTo(0L);
    }

    @Test
    @DisplayName("존재하지 않는 글 삭제로 예외 발생")
    void deleteException() {
        // expected
        assertThatThrownBy(() -> postService.delete(1_000L))
                .isInstanceOf(NotFoundPostException.class);
    }

    @Test
    @DisplayName("일치하는 비밀번호")
    void correctPassword() {
        // given
        Post post = Post.builder()
                .username("test username")
                .password(passwordEncoder.encode("test password"))
                .title("test title")
                .content("test content")
                .build();
        postRepository.save(post);

        // when
        PasswordCheck request = PasswordCheck.builder()
                .id(post.getId())
                .rawPassword("test password")
                .build();
        boolean result = postService.checkPassword(request);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("일치하지 않는 비밀번호")
    void incorrectPassword() {
        // given
        Post post = Post.builder()
                .username("test username")
                .password(passwordEncoder.encode("test password"))
                .title("test title")
                .content("test content")
                .build();
        postRepository.save(post);

        // when
        PasswordCheck request = PasswordCheck.builder()
                .id(post.getId())
                .rawPassword("incorrect password")
                .build();
        boolean result = postService.checkPassword(request);

        // then
        assertThat(result).isFalse();
    }
}