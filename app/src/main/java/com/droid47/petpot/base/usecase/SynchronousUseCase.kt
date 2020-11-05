package com.droid47.petpot.base.usecase

interface SynchronousUseCase<out Results, in Params> {
    fun execute(params: Params): Results
}
