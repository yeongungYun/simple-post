package com.posts.exception;

/**
 * 해당 id를 가진 게시글을 찾을 수 없을 때 발생하는 예외
 */
public class NotFoundPostException extends RuntimeException {

    private static final String MESSAGE = "게시글을 찾을 수 없습니다.";

    public NotFoundPostException() {
        super(MESSAGE);
    }
}
