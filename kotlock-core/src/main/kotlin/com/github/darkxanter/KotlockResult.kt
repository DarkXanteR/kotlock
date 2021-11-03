package com.github.darkxanter


public sealed interface KotlockResult {
    public fun success(dto: Kotlock): KotlockSuccess = KotlockSuccess(dto)
    public fun failure(throwable: Throwable): KotlockFailure = KotlockFailure(throwable)
}

@JvmInline
public value class KotlockSuccess(public val kotlock: Kotlock) : KotlockResult

@JvmInline
public value class KotlockFailure(public val throwable: Throwable? = null) : KotlockResult
