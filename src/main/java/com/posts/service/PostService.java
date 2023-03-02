package com.posts.service;

import com.posts.domain.Post;
import com.posts.exception.NotFoundPostException;
import com.posts.repository.PostRepository;
import com.posts.request.PasswordCheckRequest;
import com.posts.request.PostEdit;
import com.posts.request.PostWrite;
import com.posts.response.PostDetail;
import jakarta.transaction.Transactional;
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
     * @param request 글 작성 요청 dto
     * @return 저장된 게시글 id 리턴
     */
    public Long write(PostWrite request) {
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
     * @return 조회할 게시글 응답 dto 리턴
     */
    public PostDetail get(Long id) {
        Post post = findEntity(id);

        return PostDetail.builder()
                .id(post.getId())
                .username(post.getUsername())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }

    /**
     * @param checkRequest 비밀번호 확인 dto
     * @return 비밀번호가 일치하면 true, 일치하지 않으면 false 리턴
     */
    public boolean checkPassword(PasswordCheckRequest checkRequest) {
        Long postId = checkRequest.getPostId();
        Post post = findEntity(postId);
        return passwordEncoder.matches(checkRequest.getRawPassword(), post.getPassword());
    }

    /**
     *
     * @param id
     * @return 조회한 엔티티 리턴
     * @exception NotFoundPostException 해당 id의 게시글이 없으면 예외 발생
     */
    private Post findEntity(Long id) throws NotFoundPostException {
        return postRepository.findById(id)
                .orElseThrow(NotFoundPostException::new);
    }

    public Long edit(PostEdit request) {
        Post post = findEntity(request.getId());
        post.updateTitle(request.getTitle());
        post.updateContent(request.getContent());

        return post.getId();
    }
}
