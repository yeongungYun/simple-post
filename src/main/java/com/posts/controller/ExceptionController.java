package com.posts.controller;

import com.posts.exception.IncorrectPasswordException;
import com.posts.exception.NotFoundPostException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionController {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundPostException.class)
    public void notFoundPostException() {
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(IncorrectPasswordException.class)
    public void incorrectPasswordException() {
    }
}
