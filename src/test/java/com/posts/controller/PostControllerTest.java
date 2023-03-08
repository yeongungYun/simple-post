package com.posts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.posts.domain.Post;
import com.posts.repository.PostRepository;
import com.posts.request.PostEdit;
import com.posts.request.PostWrite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.IntStream;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class PostControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("/posts/{page}에 GET 요청으로 페이지 조회")
    void getList() throws Exception {
        // given
        IntStream.rangeClosed(1, 15).forEach((i) -> {
            Post post = Post.builder()
                            .username("username " + i)
                            .password("password " + i)
                            .title("title " + i)
                            .content("content " + i)
                            .build();
            postRepository.save(post);
        });

        // expected
        mockMvc.perform(get("/posts/{page}", 2)
                   .contentType(APPLICATION_JSON))
               .andExpect(jsonPath("$[0].username").value("username 5"))
               .andExpect(jsonPath("$[0].title").value("title 5"))
               .andExpect(jsonPath("$[1].username").value("username 4"))
               .andExpect(jsonPath("$[1].title").value("title 4"))
               .andExpect(jsonPath("$[2].username").value("username 3"))
               .andExpect(jsonPath("$[2].title").value("title 3"))
               .andExpect(jsonPath("$[3].username").value("username 2"))
               .andExpect(jsonPath("$[3].title").value("title 2"))
               .andExpect(jsonPath("$[4].username").value("username 1"))
               .andExpect(jsonPath("$[4].title").value("title 1"))
               .andDo(print());
    }

    @Test
    @DisplayName("/posts/ - 1페이지 호출")
    void getInitialList() throws Exception {
        // given
        IntStream.rangeClosed(1, 15).forEach((i) -> {
            Post post = Post.builder()
                            .username("username " + i)
                            .password("password " + i)
                            .title("title " + i)
                            .content("content " + i)
                            .build();
            postRepository.save(post);
        });

        mockMvc.perform(get("/posts/")
                   .contentType(APPLICATION_JSON))
               .andExpect(jsonPath("$.length()").value(10))
               .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 페이지 조회 - 내용 없음")
    void getNoList() throws Exception {
        // expected
        mockMvc.perform(get("/posts/{page}", 1000)
                   .contentType(APPLICATION_JSON))
               .andExpect(content().string("[]"))
               .andDo(print());
    }

    @Test
    @DisplayName("/posts/post/{id}에 GET 요청으로 글 조회")
    void getPost() throws Exception {
        // given
        Post post = Post.builder()
                        .username("test username")
                        .password("test password")
                        .title("test title")
                        .content("test content")
                        .build();
        postRepository.save(post);

        // expected
        mockMvc.perform(get("/posts/post/{id}", post.getId())
                   .contentType(APPLICATION_JSON))
               .andExpect(jsonPath("$.id").value(post.getId()))
               .andExpect(jsonPath("$.username").value(post.getUsername()))
               .andExpect(jsonPath("$.title").value(post.getTitle()))
               .andExpect(jsonPath("$.content").value(post.getContent()))
               .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 글 조회로 404 NOT FOUND 응답")
    void getPostException() throws Exception {
        // expected
        mockMvc.perform(get("/posts/post/{id}", 1_000L)
                   .contentType(APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andDo(print());
    }

    @Test
    @DisplayName("/posts/post에 POST 요청으로 글 작성")
    void writePost() throws Exception {
        // given
        PostWrite request = PostWrite.builder()
                                     .username("test username")
                                     .rawPassword("test password")
                                     .title("test title")
                                     .content("test content")
                                     .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/posts/post")
                   .contentType(APPLICATION_JSON)
                   .content(json))
               .andExpect(status().isCreated())
               .andDo(print());
    }

    @Test
    @DisplayName("/posts/post/{id}에 PATCH 요청으로 글 수정")
    void updatePost() throws Exception {
        // given
        Post post = Post.builder()
                        .username("test username")
                        .password("test password")
                        .title("test title")
                        .content("test content")
                        .build();
        postRepository.save(post);

        long id = post.getId();
        PostEdit request = PostEdit.builder()
                                   .title("update title")
                                   .content("update content")
                                   .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(patch("/posts/post/{id}", id)
                   .contentType(APPLICATION_JSON)
                   .content(json))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(post.getId()))
               .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 글 수정으로 404 NOT FOUND 응답")
    void updatePostException() throws Exception {
        // given
        long id = 1_000L;
        PostEdit request = PostEdit.builder()
                                   .title("update title")
                                   .content("update content")
                                   .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(patch("/posts/post/{id}", id)
                   .contentType(APPLICATION_JSON)
                   .content(json))
               .andExpect(status().isNotFound())
               .andDo(print());
    }

    @Test
    @DisplayName("/posts/post/{id}에 DELETE 요청으로 글 삭제")
    void deletePost() throws Exception {
        // given
        Post post = Post.builder()
                        .username("test username")
                        .password("test password")
                        .title("test title")
                        .content("test content")
                        .build();
        postRepository.save(post);

        mockMvc.perform(delete("/posts/post/{id}", post.getId())
                   .contentType(APPLICATION_JSON))
               .andExpect(status().isOk())
               .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 글 삭제로 404 NOT FOUND 응답")
    void deletePostException() throws Exception {
        // expected
        mockMvc.perform(delete("/posts/post/{id}", 1_000L)
                   .contentType(APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andDo(print());
    }

    @Test
    @DisplayName("비밀번호 확인")
    void checkPassword() throws Exception {
        // given
        Post post = Post.builder()
                        .username("test username")
                        .password(passwordEncoder.encode("test password"))
                        .title("test title")
                        .content("test content")
                        .build();
        postRepository.save(post);

        Long id = post.getId();
        String rawPassword = "test password";

        // when
        mockMvc.perform(post("/posts/post/check/{id}", id)
                   .contentType(APPLICATION_JSON)
                   .content(rawPassword))
               .andExpect(status().isOk())
               .andDo(print());
    }

    @Test
    @DisplayName("일치하지 않는 비밀번호로 401 UNAUTHORIZED 응답")
    void checkPasswordException() throws Exception {
        // given
        Post post = Post.builder()
                        .username("test username")
                        .password(passwordEncoder.encode("test password"))
                        .title("test title")
                        .content("test content")
                        .build();
        postRepository.save(post);

        Long id = post.getId();
        String rawPassword = "incorrect password";

        // when
        mockMvc.perform(post("/posts/post/check/{id}", id)
                   .contentType(APPLICATION_JSON)
                   .content(rawPassword))
               .andExpect(status().isUnauthorized())
               .andDo(print());
    }
}