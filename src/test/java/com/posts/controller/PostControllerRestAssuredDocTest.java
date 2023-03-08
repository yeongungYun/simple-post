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
    @DisplayName("해당 페이지 조회")
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
                    parameterWithName("page").description("페이지 번호")),
                responseFields(
                    fieldWithPath("[].id").description("게시글 id"),
                    fieldWithPath("[].username").description("작성자"),
                    fieldWithPath("[].title").description("글 제목")
                )
            ))
            .when()
            .get("/posts/{page}")
            .then()
            .assertThat().statusCode(SC_OK);
    }
    
    @Test
    @DisplayName("기본 페이지")
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
                    fieldWithPath("[].id").description("게시글 id"),
                    fieldWithPath("[].username").description("작성자"),
                    fieldWithPath("[].title").description("글 제목")
                )
            ))
            .when()
            .get("/posts/")
            .then()
            .assertThat().statusCode(SC_OK)
            .body("size()", response -> equalTo(10));
    }

    @Test
    @DisplayName("글 단건 조회")
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
                    parameterWithName("id").description("조회할 글 id")),
                responseFields(
                    fieldWithPath("id").description("글 id"),
                    fieldWithPath("username").description("작성자"),
                    fieldWithPath("title").description("글 제목"),
                    fieldWithPath("content").description("글 내용")
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
    @DisplayName("글 단건 조회 예외")
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
    @DisplayName("글 작성")
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
                    fieldWithPath("username").description("작성자"),
                    fieldWithPath("rawPassword").description("입력한 비밀번호"),
                    fieldWithPath("title").description("글 제목"),
                    fieldWithPath("content").description("글 내용")
                )
            ))
            .when()
            .post("/posts/post")
            .then()
            .assertThat().statusCode(SC_CREATED);
    }

    @Test
    @DisplayName("글 수정")
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
                    parameterWithName("id").description("수정할 글 id")
                ),
                requestFields(
                    fieldWithPath("title").description("수정할 제목"),
                    fieldWithPath("content").description("수정할 내용")
                )
            ))
            .when()
            .patch("/posts/post/{id}")
            .then()
            .assertThat().statusCode(SC_OK);
    }

    @Test
    @DisplayName("글 수정 예외")
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
                    parameterWithName("id").description("수정할 글 id")
                )
            ))
            .when()
            .patch("/posts/post/{id}")
            .then()
            .assertThat().statusCode(SC_NOT_FOUND);
    }

    @Test
    @DisplayName("글 삭제")
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
    @DisplayName("글 삭제 예외")
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
    @DisplayName("비밀번호 확인")
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
    @DisplayName("비밀번호 확인 - 해당 id의 글이 없는 경우")
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
    @DisplayName("비밀번호 확인 - 비밀번호가 일치하지 않는 경우")
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
