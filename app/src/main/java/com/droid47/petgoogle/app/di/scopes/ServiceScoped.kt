package com.droid47.petgoogle.app.di.scopes

import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
annotation class ServiceScoped
