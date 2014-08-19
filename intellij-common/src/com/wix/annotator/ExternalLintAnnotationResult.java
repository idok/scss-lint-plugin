package com.wix.annotator;

public class ExternalLintAnnotationResult<T> {
    public ExternalLintAnnotationResult(ExternalLintAnnotationInput input, T result) {
        this.input = input;
        this.result = result;
    }

    public final ExternalLintAnnotationInput input;
    public final T result;
}