package com.posts.controller;

import com.posts.domain.Post;
import com.posts.repository.PostRepository;
import com.posts.request.PostEdit;
import com.posts.request.PostWrite;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.IntStream;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(RestDocumentationExtension.class)
public class PostControllerRestAssuredDocTest {


    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(restDocumentation))
            .build();
    }

    @Test
    @DisplayName("?????? ????????? ??????")
    void getPostList() {
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
        int page = 1;

        // expected
        RestAssured
            .given(spec)
            .pathParam("page", page)
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .filter(document("post-list",
                pathParameters(
                    parameterWithName("page").description("????????? ??????")),
                responseFields(
                    fieldWithPath("[].id").description("????????? id"),
                    fieldWithPath("[].username").description("?????????"),
                    fieldWithPath("[].title").description("??? ??????")
                )
            ))
            .when()
            .get("/posts/{page}")
            .then()
            .assertThat().statusCode(SC_OK);
    }
    
    @Test
    @DisplayName("?????? ?????????")
    void getInitialPage() {
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
        RestAssured
            .given(spec)
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .filter(document("post-initialList",
                responseFields(
                    fieldWithPath("[].id").description("????????? id"),
                    fieldWithPath("[].username").description("?????????"),
                    fieldWithPath("[].title").description("??? ??????")
                )
            ))
            .when()
            .get("/posts/")
            .then()
            .assertThat().statusCode(SC_OK)
            .body("size()", response -> equalTo(10));
    }

    @Test
    @DisplayName("??? ?????? ??????")
    void getPost() {
        // given
        Post post = Post.builder()
            .username("username")
            .password("password")
            .title("title")
            .content("content")
            .build();
        postRepository.save(post);

        // expected
        RestAssured
            .given(spec)
            .pathParam("id", post.getId())
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .filter(document("post-inquiry",
                pathParameters(
                    parameterWithName("id").description("????????? ??? id")),
                responseFields(
                    fieldWithPath("id").description("??? id"),
                    fieldWithPath("username").description("?????????"),
                    fieldWithPath("title").description("??? ??????"),
                    fieldWithPath("content").description("??? ??????")
                )))
            .when()
            .get("/posts/post/{id}")
            .then()
            .assertThat().statusCode(SC_OK)
            .body("id", response -> equalTo(post.getId().intValue()))
            .body("username", response -> equalTo("username"))
            .body("title", response -> equalTo("title"))
            .body("content", response -> equalTo("content"));
    }

    @Test
    @DisplayName("??? ?????? ?????? ??????")
    void getPostException() {
        // expected
        RestAssured
            .given(spec)
            .pathParam("id", 1000L)
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .filter(document("post-inquiry-exception"))
            .when()
            .get("/posts/post/{id}")
            .then()
            .assertThat().statusCode(SC_NOT_FOUND);
    }

    @Test
    @DisplayName("??? ??????")
    void writePost() {
        // given
        PostWrite postWrite = PostWrite.builder()
            .username("username")
            .rawPassword("password")
            .title("title")
            .content("content")
            .build();

        // expected
        RestAssured
            .given(spec)
            .body(postWrite)
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .filter(document("post-write",
                requestFields(
                    fieldWithPath("username").description("?????????"),
                    fieldWithPath("rawPassword").description("????????? ????????????"),
                    fieldWithPath("title").description("??? ??????"),
                    fieldWithPath("content").description("??? ??????")
                )
            ))
            .when()
            .post("/posts/post")
            .then()
            .assertThat().statusCode(SC_CREATED);
    }

    @Test
    @DisplayName("??? ??????")
    void updatePost() {
        // given
        Post post = Post.builder()
            .username("username")
            .password("encodedPassword")
            .title("title")
            .content("content")
            .build();
        postRepository.save(post);

        long id = post.getId();
        PostEdit postEdit = PostEdit.builder()
            .title("update title")
            .content("update content")
            .build();

        // expected
        RestAssured
            .given(spec)
            .pathParam("id", id)
            .body(postEdit)
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .filter(document("post-update",
                pathParameters(
                    parameterWithName("id").description("????????? ??? id")
                ),
                requestFields(
                    fieldWithPath("title").description("????????? ??????"),
                    fieldWithPath("content").description("????????? ??????")
                )
            ))
            .when()
            .patch("/posts/post/{id}")
            .then()
            .assertThat().statusCode(SC_OK);
    }

    @Test
    @DisplayName("??? ?????? ??????")
    void updatePostException() {
        // given
        PostEdit postEdit = PostEdit.builder()
            .title("update title")
            .content("update content")
            .build();

        // expected
        RestAssured
            .given(spec)
            .pathParam("id", 1_000L)
            .body(postEdit)
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .filter(document("post-update-exception",
                pathParameters(
                    parameterWithName("id").description("????????? ??? id")
                )
            ))
            .when()
            .patch("/posts/post/{id}")
            .then()
            .assertThat().statusCode(SC_NOT_FOUND);
    }

    @Test
    @DisplayName("??? ??????")
    void deletePost() {
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
        RestAssured
            .given(spec)
            .pathParam("id", id)
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .filter(document("post-delete"))
            .when()
            .delete("/posts/post/{id}")
            .then()
            .assertThat().statusCode(SC_OK);
    }

    @Test
    @DisplayName("??? ?????? ??????")
    void deletePostException() {

        // expected
        RestAssured
            .given(spec)
            .pathParam("id", 1_000L)
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .filter(document("post-delete"))
            .when()
            .delete("/posts/post/{id}")
            .then()
            .assertThat().statusCode(SC_NOT_FOUND);
    }

    @Test
    @DisplayName("???????????? ??????")
    void checkPassword() {
        // given
        String rawPassword = "password";
        Post post = Post.builder()
            .username("username")
            .password(passwordEncoder.encode(rawPassword))
            .title("title")
            .content("content")
            .build();
        postRepository.save(post);

        Long id = post.getId();

        // expected
        RestAssured
            .given(spec)
            .pathParam("id", id)
            .body(rawPassword)
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .filter(document("post-checkPassword"))
            .when()
            .post("/posts/post/check/{id}")
            .then()
            .assertThat().statusCode(SC_OK);
    }

    @Test
    @DisplayName("???????????? ?????? - ?????? id??? ?????? ?????? ??????")
    void checkPasswordException1() {
        // given
        String rawPassword = "password";

        // expected
        RestAssured
            .given(spec)
            .pathParam("id", 1_000L)
            .body(rawPassword)
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .filter(document("post-checkPassword-notFoundPostException"))
            .when()
            .post("/posts/post/check/{id}")
            .then()
            .assertThat().statusCode(SC_NOT_FOUND);
    }

    @Test
    @DisplayName("???????????? ?????? - ??????????????? ???????????? ?????? ??????")
    void checkPasswordException2() {
        // given
        String rawPassword = "password";
        Post post = Post.builder()
            .username("username")
            .password(passwordEncoder.encode(rawPassword))
            .title("title")
            .content("content")
            .build();
        postRepository.save(post);

        Long id = post.getId();
        String incorrectPassword = "incorrect password";

        // expected
        RestAssured
            .given(spec)
            .pathParam("id", id)
            .body(incorrectPassword)
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .filter(document("post-checkPassword-incorrectPasswordException"))
            .when()
            .post("/posts/post/check/{id}")
            .then()
            .assertThat().statusCode(SC_UNAUTHORIZED);
    }
}
