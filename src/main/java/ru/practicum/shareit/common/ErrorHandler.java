package ru.practicum.shareit.common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.common.dto.ErrorResult;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResult handleNotFound(NotFoundException e, HttpServletRequest req) {
        log.error("404 NOT_FOUND {} {} - {}", req.getMethod(), req.getRequestURI(), e.getMessage(), e);
        return ErrorResult.of(HttpStatus.NOT_FOUND, e.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResult handleBadRequest(BadRequestException e, HttpServletRequest req) {
        log.error("400 BAD_REQUEST {} {} - {}", req.getMethod(), req.getRequestURI(), e.getMessage(), e);
        return ErrorResult.of(HttpStatus.BAD_REQUEST, e.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResult handleConflict(ConflictException e, HttpServletRequest req) {
        log.error("409 CONFLICT {} {} - {}", req.getMethod(), req.getRequestURI(), e.getMessage(), e);
        return ErrorResult.of(HttpStatus.CONFLICT, e.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResult handleValidation(MethodArgumentNotValidException e, HttpServletRequest req) {
        String msg = e.getBindingResult().getFieldErrors().stream().map(fe -> fe.getField() + ": " + fe.getDefaultMessage()).findFirst().orElse(e.getMessage());
        log.error("400 VALIDATION {} {} - {}", req.getMethod(), req.getRequestURI(), msg, e);
        return ErrorResult.of(HttpStatus.BAD_REQUEST, msg, req.getRequestURI());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResult handleMissingHeader(MissingRequestHeaderException e, HttpServletRequest req) {
        log.error("400 MISSING_HEADER {} {} - {}", req.getMethod(), req.getRequestURI(), e.getMessage(), e);
        return ErrorResult.of(HttpStatus.BAD_REQUEST, e.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResult handleAny(Throwable e, HttpServletRequest req) {
        log.error("500 INTERNAL_ERROR {} {} - {}", req.getMethod(), req.getRequestURI(), e.getMessage(), e);
        return ErrorResult.of(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), req.getRequestURI());
    }
}
