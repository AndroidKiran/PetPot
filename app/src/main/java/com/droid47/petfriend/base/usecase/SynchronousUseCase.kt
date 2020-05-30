package com.droid47.petfriend.base.usecase

interface SynchronousUseCase<out Results, in Params> {
    fun execute(params: Params): Results
}
