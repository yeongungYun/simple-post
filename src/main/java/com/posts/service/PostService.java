package com.posts.service;

import com.posts.domain.Post;
import com.posts.repository.PostRepository;
import com.posts.request.PostRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;

    /**
     * @param postRequest 게시글 작성 요청 DTO
     * @return 저장된 게시글 id 리턴
     */
    public Long write(PostRequest postRequest) {
        Post post = postRequest.toEntity();
        postRepository.save(post);
        return post.getId();
    }

}
