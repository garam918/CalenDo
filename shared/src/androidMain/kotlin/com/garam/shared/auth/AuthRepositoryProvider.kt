package com.garam.shared.auth

actual class AuthRepositoryProvider {
    actual fun get(): AuthRepository = AuthRepositoryImpl()
}