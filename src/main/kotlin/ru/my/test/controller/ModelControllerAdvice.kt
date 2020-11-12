package ru.my.test.controller

import ApiError
import ApiValidationError
import Violation
import javassist.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class ModelControllerAdvice {
    @ExceptionHandler(value = [NotFoundException::class])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun notFoundException(exception: NotFoundException): ApiError {
        return ApiError(
                type = ErrorType.PROCESS_ERROR,
                status = HttpStatus.NOT_FOUND.value(),
                title = "Ресурс не найден",
                detail = exception.message
        )
    }

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun methodArgumentNotValidException(exception: MethodArgumentNotValidException): ApiError {
        val violations = fromBindingResult(exception.bindingResult)
        return ApiValidationError(
            detail = exception.javaClass.simpleName,
            violations = violations
        )
    }

    @ExceptionHandler(value = [HttpMessageNotReadableException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun httpMessageNotReadableException(exception: HttpMessageNotReadableException): ApiError {
        return ApiError(
            type = ErrorType.PROCESS_ERROR,
            status = HttpStatus.NOT_FOUND.value(),
            title = "Неправильно сформирован JSON",
            detail = exception.message
        )
    }

    private fun fromBindingResult(bindingResult: BindingResult): List<Violation> {
        val errors = bindingResult.allErrors
        return errors
            .mapNotNull { error ->
                when (error) {
                    is FieldError -> Violation(error.field, error.defaultMessage ?: "")
                    is ObjectError -> Violation(error.objectName, error.defaultMessage ?: "")
                    else -> null
                }
            }
            .toList()
    }
}
