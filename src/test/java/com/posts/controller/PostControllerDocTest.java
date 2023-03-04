package com.posts.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.posts.domain.Post;
import com.posts.repository.PostRepository;
import com.posts.request.PostEdit;
import com.posts.request.PostWrite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.IntStream;

import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
public class PostControllerDocTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                                      .apply(documentationConfiguration(restDocumentation))
                                      .build();
    }

    @Test
    void getPostList() throws Exception {
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
        mockMvc.perform(get("/posts/{page}", 1)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("post-list",
                        responseFields(
                                fieldWithPath("[].id").description("게시글 id"),
                                fieldWithPath("[].username").description("작성자"),
                                fieldWithPath("[].title").description("제목"))
                        )
                );
    }

    @Test
    @DisplayName("기본 페이지")
    void getInitialPostList() throws Exception {
        // given
        Post post = Post.builder()
                        .username("username")
                        .password("encodedPassword")
                        .title("title")
                        .content("content")
                        .build();
        postRepository.save(post);

        // expected
        mockMvc.perform(get("/posts/")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("post-list-initial",
                               responseFields(
                                       fieldWithPath("[].id").description("게시글 id"),
                                       fieldWithPath("[].username").description("작성자"),
                                       fieldWithPath("[].title").description("제목"))
                        )
                );
    }

    @Test
    @DisplayName("글 단건 조회")
    void getPost() throws Exception {
        // given
        Post post = Post.builder()
                .username("username")
                .password("encodedPassword")
                .title("title")
                .content("content")
                .build();
        postRepository.save(post);

        // expected
        mockMvc.perform(get("/posts/post/{id}", post.getId())
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("post-inquiry",
                        pathParameters(
                                parameterWithName("id").description("게시글 id")),
                                responseFields(
                                        fieldWithPath("id").description("게시글 id"),
                                        fieldWithPath("username").description("작성자"),
                                        fieldWithPath("title").description("제목"),
                                        fieldWithPath("content").description("내용"))
                        )
                );
    }

    @Test
    @DisplayName("글 단건 조회 예외")
    void getPostException() throws Exception {
        // expected
        mockMvc.perform(get("/posts/post/{id}", 1000)
               .contentType(APPLICATION_JSON)
               .accept(APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andDo(document("post-inquiry-exception"));
    }

    @Test
    @DisplayName("글 작성")
    void writePost() throws Exception {
        PostWrite request = PostWrite.builder()
                .username("username")
                .rawPassword("password")
                .title("title")
                .content("content")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/posts/post")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andDo(document("post-write",
                        requestFields(
                                fieldWithPath("username").description("작성자"),
                                fieldWithPath("rawPassword").description("입력한 비밀번호"),
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("content").description("내용"))
                        )
                );
    }

    @Test
    @DisplayName("글 수정")
    void updatePost() throws Exception {
        // given
        Post post = Post.builder()
                        .username("username")
                        .password("encodedPassword")
                        .title("title")
                        .content("content")
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
               .accept(APPLICATION_JSON)
               .content(json))
               .andExpect(status().isOk())
               .andDo(document("post-update",
                       pathParameters(
                               parameterWithName("id").description("수정할 글의 id")
                       ),
                       requestFields(
                               fieldWithPath("title").description("수정할 제목"),
                               fieldWithPath("content").description("수정할 내용"))
                       )
               );
    }

    @Test
    @DisplayName("글 수정 예외")
    void updatePostException() throws Exception {
        // given
        PostEdit request = PostEdit.builder()
                .title("update title")
                .content("update content")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(patch("/posts/post/{id}", 1_000L)
               .contentType(APPLICATION_JSON)
               .accept(APPLICATION_JSON)
               .content(json))
               .andExpect(status().isNotFound())
               .andDo(document("post-update-exception"));
    }

    @Test
    @DisplayName("글 삭제")
    void deletePost() throws Exception {
        // given
        Post post = Post.builder()
                        .username("username")
                        .password("encodedPassword")
                        .title("title")
                        .content("content")
                        .build();
        postRepository.save(post);
        long id = post.getId();

        // expected
        mockMvc.perform(delete("/posts/post/{id}", id)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("post-delete",
                        pathParameters(
                                parameterWithName("id").description("삭제할 글의 id"))
                        )
                );
    }

    @Test
    @DisplayName("글 삭제 예외")
    void deletePostException() throws Exception {
        // expected
        mockMvc.perform(delete("/posts/post/{id}", 1_000L)
               .contentType(APPLICATION_JSON)
               .accept(APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andDo(document("post-delete-exception"));
    }

    @Test
    @DisplayName("비밀번호 확인")
    void checkPassword() throws Exception {
        // given
        Post post = Post.builder()
                .username("username")
                .password(passwordEncoder.encode("password"))
                .title("title")
                .content("content")
                .build();
        postRepository.save(post);

        Long id = post.getId();
        String rawPassword = "password";

        // expected
        mockMvc.perform(post("/posts/post/check/{id}", id)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(rawPassword))
                .andExpect(status().isOk())
                .andDo(document("post-checkPassword",
                        pathParameters(
                                parameterWithName("id").description("비밀번호를 확인할 글의 id"))
                        )
                );
    }

    @Test
    @DisplayName("비밀번호 확인 - 해당 id의 글이 없는 경우")
    void checkPasswordException1() throws Exception {
        // given
        Long id = 1_000L;
        String rawPassword = "password";

        // expected
        mockMvc.perform(post("/posts/post/check/{id}", id)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(rawPassword))
                .andExpect(status().isNotFound())
                .andDo(document("post-checkPassword-notFoundPostException"));
    }

    @Test
    @DisplayName("비밀번호 확인 - 비밀번호가 일치하지 않는 경우")
    void checkPasswordException2() throws Exception {
        // given
        Post post = Post.builder()
                        .username("username")
                        .password(passwordEncoder.encode("password"))
                        .title("title")
                        .content("content")
                        .build();
        postRepository.save(post);

        Long id = post.getId();
        String rawPassword = "incorrect";

        // expected
        mockMvc.perform(post("/posts/post/check/{id}", id)
                       .contentType(APPLICATION_JSON)
                       .accept(APPLICATION_JSON)
                       .content(rawPassword))
               .andExpect(status().isUnauthorized())
                .andDo(document("post-checkPassword-incorrectPasswordException"));
    }
}
