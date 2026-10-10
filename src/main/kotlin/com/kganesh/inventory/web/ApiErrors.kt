package com.kganesh.inventory.web

import com.kganesh.inventory.service.InventoryService.DuplicateSkuException
import com.kganesh.inventory.service.InventoryService.InsufficientStockException
import com.kganesh.inventory.service.InventoryService.ProductNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiErrors {

    @ExceptionHandler(ProductNotFoundException::class)
    fun notFound(e: ProductNotFoundException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message ?: "not found")

    @ExceptionHandler(DuplicateSkuException::class)
    fun duplicate(e: DuplicateSkuException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.message ?: "conflict")

    @ExceptionHandler(InsufficientStockException::class)
    fun insufficient(e: InsufficientStockException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.message ?: "insufficient stock")

    @ExceptionHandler(IllegalArgumentException::class)
    fun badRequest(e: IllegalArgumentException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message ?: "bad request")
}
