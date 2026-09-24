package com.kangurusiaga.app.core.common

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Dispatcher(val dispatcher: AppDispatchers)

enum class AppDispatchers {
    IO,
    DEFAULT,
    MAIN
}
