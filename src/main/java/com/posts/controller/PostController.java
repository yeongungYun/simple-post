package com.posts.controller;

import com.posts.request.PostEdit;
import com.posts.request.PostWrite;
import com.posts.response.PostDetail;
import com.posts.response.PostSummary;
import com.posts.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/posts")
@RestController
public class PostController {

    private final PostService postService;

    /**
     * 글 조회
     *
     * @param id 글 id
     * @return PostDetail 응답 dto
     */
    @GetMapping("/post/{id}")
    public PostDetail get(@PathVariable(name = "id") Long id) {
        return postService.get(id);
    }

    /**
     * url에 페이지 번호를 넣지 않는 메인 화면
     *
     * @return 1페이지 리턴
     */
    @GetMapping("/")
    public List<PostSummary> getInitialPage() {
        return postService.getList(1);
    }

    /**
     * 해당 페이지 글들 조회
     *
     * @param page 페이지 번호
     * @return 해당 페이지의 글들 리턴
     */
    @GetMapping("/{page}")
    public List<PostSummary> getPage(@PathVariable(name = "page", required = false) Integer page) {
        return postService.getList(page);
    }

    /**
     * 글 작성
     * @param postWrite 글 작성 dto
     * @return Key: "id", Value: [작성한 글 id]
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/post")
    public Map<String, Long> write(@RequestBody PostWrite postWrite) {
        Long postId = postService.write(postWrite);
        return postService.idConvertToJson(postId);
    }

    /**
     * 글 수정
     *
     * @param postEdit 글 수정 dto
     * @return Key: "id", Value: [수정한 글 id]
     */
    @PatchMapping("/post/{id}")
    public Map<String, Long> edit(@PathVariable(name = "id") Long id, @RequestBody PostEdit postEdit) {
        Long postId = postService.edit(id, postEdit);
        return postService.idConvertToJson(postId);
    }

    /**
     * 글 삭제
     *
     * @param id 삭제할 글 id
     */
    @DeleteMapping("/post/{id}")
    public void delete(@PathVariable(name = "id") Long id) {
        postService.delete(id);
    }


    /**
     * 비밀번호 확인
     * @param id          확인할 글 id
     * @param rawPassword 입력한 비밀번호
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/post/check/{id}")
    public void check(@PathVariable(name = "id") Long id, @RequestBody String rawPassword) {
        postService.checkPassword(id, rawPassword);
    }
}
