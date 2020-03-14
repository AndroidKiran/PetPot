package com.droid47.petgoogle.base.usecase

interface SynchronousUseCase<out Results, in Params> {
    fun execute(params: Params? = null): Results
}
