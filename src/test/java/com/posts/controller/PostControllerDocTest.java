package com.posts.controller;

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

@Deprecated
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
    @DisplayName("?????? ????????? ??????")
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
                    pathParameters(
                        parameterWithName("page").description("????????? ??????")
                    ),
                    responseFields(
                        fieldWithPath("[].id").description("????????? id"),
                        fieldWithPath("[].username").description("?????????"),
                        fieldWithPath("[].title").description("??????"))
                )
            );
    }

    @Test
    @DisplayName("?????? ?????????")
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
            .andDo(document("post-initialList",
                    responseFields(
                        fieldWithPath("[].id").description("????????? id"),
                        fieldWithPath("[].username").description("?????????"),
                        fieldWithPath("[].title").description("??????"))
                )
            );
    }

    @Test
    @DisplayName("??? ?????? ??????")
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
                        parameterWithName("id").description("????????? id")),
                    responseFields(
                        fieldWithPath("id").description("????????? id"),
                        fieldWithPath("username").description("?????????"),
                        fieldWithPath("title").description("??????"),
                        fieldWithPath("content").description("??????"))
                )
            );
    }

    @Test
    @DisplayName("??? ?????? ?????? ??????")
    void getPostException() throws Exception {
        // expected
        mockMvc.perform(get("/posts/post/{id}", 1000)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andDo(document("post-inquiry-exception"));
    }

    @Test
    @DisplayName("??? ??????")
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
                        fieldWithPath("username").description("?????????"),
                        fieldWithPath("rawPassword").description("????????? ????????????"),
                        fieldWithPath("title").description("??????"),
                        fieldWithPath("content").description("??????")
                    ),
                    responseFields(
                        fieldWithPath("id").description("????????? ??? id")
                    )
                )
            );
    }

    @Test
    @DisplayName("??? ??????")
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
                        parameterWithName("id").description("????????? ?????? id")
                    ),
                    requestFields(
                        fieldWithPath("title").description("????????? ??????"),
                        fieldWithPath("content").description("????????? ??????")
                    ),
                    responseFields(
                        fieldWithPath("id").description("????????? ??? id")
                    )
                )
            );
    }

    @Test
    @DisplayName("??? ?????? ??????")
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
    @DisplayName("??? ??????")
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
                        parameterWithName("id").description("????????? ?????? id"))
                )
            );
    }

    @Test
    @DisplayName("??? ?????? ??????")
    void deletePostException() throws Exception {
        // expected
        mockMvc.perform(delete("/posts/post/{id}", 1_000L)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andDo(document("post-delete-exception"));
    }

    @Test
    @DisplayName("???????????? ??????")
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
                        parameterWithName("id").description("??????????????? ????????? ?????? id"))
                )
            );
    }

    @Test
    @DisplayName("???????????? ?????? - ?????? id??? ?????? ?????? ??????")
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
    @DisplayName("???????????? ?????? - ??????????????? ???????????? ?????? ??????")
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
