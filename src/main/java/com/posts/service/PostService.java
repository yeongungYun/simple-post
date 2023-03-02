package com.posts.service;

import com.posts.domain.Post;
import com.posts.exception.NotFoundPostException;
import com.posts.repository.PostRepository;
import com.posts.request.PostRequest;
import com.posts.response.PostDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;

    private final PasswordEncoder passwordEncoder;

    /**
     * @param request 글 작성 요청 DTO
     * @return 저장된 게시글 id 리턴
     */
    public Long write(PostRequest request) {
        Post post = Post.builder()
                        .username(request.getUsername())
                        .password(passwordEncoder.encode(request.getRawPassword()))
                        .title(request.getTitle())
                        .content(request.getContent())
                        .build();
        postRepository.save(post);
        return post.getId();
    }

    /**
     *
     * @param id 조회할 게시글의 id
     * @return 조회할 게시글 응답 DTO 리턴
     * @exception NotFoundPostException 해당 id의 게시글이 없을경우 예외 발생
     */
    public PostDetailResponse get(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(NotFoundPostException::new);

        return PostDetailResponse.builder()
                .id(post.getId())
                .username(post.getUsername())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }
}
