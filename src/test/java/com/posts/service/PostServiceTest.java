package com.posts.service;


import com.posts.domain.Post;
import com.posts.exception.IncorrectPasswordException;
import com.posts.exception.NotFoundPostException;
import com.posts.repository.PostRepository;
import com.posts.request.PostEdit;
import com.posts.request.PostWrite;
import com.posts.response.PostDetail;
import com.posts.response.PostSummary;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;

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
                .password("test password")
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
    @DisplayName("글 여러개 조회")
    void getList() {
        // given
        for (int i = 1; i <= 15; ++i) {
            Post post = Post.builder()
                    .username("test username " + i)
                    .password("password" + i)
                    .title("test title " + i)
                    .content("test content " + i)
                    .build();
            postRepository.save(post);
        }
        // when
        List<PostSummary> list1 = postService.getList(1);
        List<PostSummary> list2 = postService.getList(2);

        // then
        assertThat(list1.size()).isEqualTo(10);
        assertThat(list2.size()).isEqualTo(5);

        log.info("list1[0]={}", list1.get(0));
        log.info("list2[0]={}", list2.get(0));
    }

    @Test
    @DisplayName("작성된 글의 제목과 내용 수정")
    void edit() {
        // given
        Post post = Post.builder()
                .username("test username")
                .password("test password")
                .title("test title")
                .content("test content")
                .build();
        postRepository.save(post);

        long id = post.getId();
        PostEdit postEdit = PostEdit.builder()
                .title("edited title")
                .content("edited content")
                .build();

        // when
        Long postId = postService.edit(id, postEdit);

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
                .title("edited title")
                .content("edited content")
                .build();

        // expected
        assertThatThrownBy(() -> postService.edit(1_000L, postEdit))
                .isInstanceOf(NotFoundPostException.class);
    }

    @Test
    @DisplayName("작성한 글 삭제")
    void delete() {
        // given
        Post post = Post.builder()
                .username("test username")
                .password("test password")
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

        // expected
        Long id = post.getId();
        String rawPassword = "test password";
        postService.checkPassword(id, rawPassword);
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
        Long id = post.getId();
        String rawPassword = "incorrect password";

        // then
        assertThatThrownBy(() -> postService.checkPassword(id, rawPassword))
                .isInstanceOf(IncorrectPasswordException.class);
    }

    @Test
    @DisplayName("id를 Map형식으로 변환")
    void convertId() {
        // given
        Long id = 1_000L;

        // when
        Map<String, Long> result = postService.idConvertToJson(id);

        // then
        assertThat(result.get("id")).isEqualTo(id);

    }
}