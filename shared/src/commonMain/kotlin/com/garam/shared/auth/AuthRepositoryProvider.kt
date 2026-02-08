package com.garam.shared.auth

expect class AuthRepositoryProvider() {
    fun get(): AuthRepository
}