package com.posts.service;

import com.posts.domain.Post;
import com.posts.exception.IncorrectPasswordException;
import com.posts.exception.NotFoundPostException;
import com.posts.repository.PostRepository;
import com.posts.request.PasswordCheck;
import com.posts.request.PostEdit;
import com.posts.request.PostWrite;
import com.posts.response.PostDetail;
import com.posts.response.PostSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
@Service
public class PostService {

    private final PostRepository postRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${post.amount}")
    private int amountPerPage;

    /**
     * 글 작성
     * @param request 글 작성 요청 dto
     * @return 저장된 게시글 id
     */
    @Transactional
    public Long write(PostWrite request) {
        Post post = Post.builder()
                        .username(request.getUsername())
                        .password(passwordEncoder.encode(request.getRawPassword()))
                        .title(request.getTitle())
                        .content(request.getContent())
                        .build();
        postRepository.save(post);
        log.info("글 작성 id={}", post.getId());
        return post.getId();
    }

    /**
     * 글 단건 조회
     * @param id 조회할 게시글의 id
     * @return 조회 게시글 응답 dto
     */
    @Transactional(readOnly = true)
    public PostDetail get(Long id) {
        Post post = findPost(id);
        log.info("글 조회 id={}", post.getId());
        return PostDetail.builder()
                .id(post.getId())
                .username(post.getUsername())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }

    /**
     * 글 여러개 조회, id 내림차순으로 리턴
     * @param currentPage 조회할 페이지 번호
     * @return
     */
    @Transactional(readOnly = true)
    public List<PostSummary> getList(int currentPage) {
        Pageable pageable = PageRequest.of(currentPage - 1, amountPerPage, Sort.by("id").descending());
        return postRepository.findAll(pageable)
                .stream()
                .map(post -> {
                    return PostSummary.builder()
                            .id(post.getId())
                            .username(post.getUsername())
                            .title(post.getTitle())
                            .build();
                }).toList();
    }

    /**
     * 글 수정
     * @param request 글 수정 dto
     * @return 수정한 글의 id
     */
    @Transactional
    public Long edit(PostEdit request) {
        Post post = findPost(request.getId());
        post.updateTitle(request.getTitle());
        post.updateContent(request.getContent());

        return post.getId();
    }

    /**
     * 글 삭제
     * @param id 삭제할 글의 id
     */
    @Transactional
    public void delete(Long id) {
        Post post = findPost(id);
        postRepository.delete(post);
    }

    /**
     * 비밀번호 확인
     * @param passwordCheck 비밀번호 확인 dto
     * @throws IncorrectPasswordException 비밀번호가 맞지 않을 시 예외 발생
     */
    public void checkPassword(PasswordCheck passwordCheck) throws IncorrectPasswordException {
        Post post = findPost(passwordCheck.getId());
        if (!passwordEncoder.matches(passwordCheck.getRawPassword(), post.getPassword())) {
            throw new IncorrectPasswordException();
        }
    }

    /**
     * @param id
     * @return 조회한 엔티티 리턴
     * @exception NotFoundPostException 해당 id의 게시글이 없으면 예외 발생
     */
    private Post findPost(Long id) throws NotFoundPostException {
        return postRepository.findById(id)
                    .orElseThrow(NotFoundPostException::new);

    }
}
