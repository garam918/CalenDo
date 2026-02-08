package com.garam.shared.auth

import cocoapods.FirebaseAuth.FIRAuth
import cocoapods.FirebaseAuth.FIRGoogleAuthProvider
import cocoapods.FirebaseAuth.FIROAuthProvider
import cocoapods.FirebaseAuth.FIRUserInfoProtocol
import cocoapods.FirebaseFirestoreInternal.FIRFirestore
import com.garam.shared.data.source.local.LocalUserData
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.cstr
import kotlinx.cinterop.objc_retain
import kotlinx.cinterop.refTo
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AuthenticationServices.ASAuthorization
import platform.AuthenticationServices.ASAuthorizationAppleIDCredential
import platform.AuthenticationServices.ASAuthorizationAppleIDProvider
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerDelegateProtocol
import platform.AuthenticationServices.ASAuthorizationControllerPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASAuthorizationScopeEmail
import platform.AuthenticationServices.ASAuthorizationScopeFullName
import platform.CoreCrypto.CC_SHA256
import platform.CoreCrypto.CC_SHA256_DIGEST_LENGTH
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalForeignApi::class)
class AuthRepositoryImpl : AuthRepository {

    private var appleAuthDelegate: NSObject? = null

    override suspend fun isExistAccount(uid: String): Boolean {

        return suspendCancellableCoroutine { continuation ->
            FIRFirestore.firestore().collectionWithPath("users")
                .documentWithPath(uid).getDocumentWithCompletion { document, error ->

                    if (document?.exists == true) continuation.resume(true) {}
                    else continuation.resume(false) {}


                }
        }
    }


    override suspend fun signInAnonymously(): LocalUserData? {

        return suspendCancellableCoroutine { cont ->
            FIRAuth.auth().signInAnonymouslyWithCompletion { result, error ->

                if (error == null) {
                    val user = result?.user()
                    val localUserData = LocalUserData(
                        uid = user?.uid().toString(),
                        email = "Guest",
                        loginType = "anonymous"
                    )

                    cont.resume(localUserData) {}
                } else cont.resume(null) {}
            }
        }
    }

    override suspend fun signInWithGoogle(idToken: String, accessToken: String): LocalUserData? {
        return suspendCancellableCoroutine { cont ->
            val credential = FIRGoogleAuthProvider.credentialWithIDToken(idToken, accessToken)

            val currentUser = FIRAuth.auth().currentUser()

            if(currentUser != null && currentUser.isAnonymous()) {
                currentUser.linkWithCredential(credential) { result, error ->

                    if(error != null) {
                        println("link error ${error.localizedDescription}")
                        FIRAuth.auth().signInWithCredential(credential) { result, error ->


                            if (result != null) {
                                val user = result.user()
                                val email = user.email()
                                val uid = user.uid()
                                val loginType = "google"

                                println("ios uid : ${uid}")
                                println("ios email : ${email}")

                                println("ios loginType : ${(user.providerData()[0] as? FIRUserInfoProtocol)?.providerID()}")


                                val userData = LocalUserData(email = email, uid = uid, loginType = loginType)

                                cont.resume(userData) {}

                            } else cont.resume(null) {}

                        }

                    }
                    else {

                        if (result != null) {

                            val user = result.user()
                            val email = user.email()
                            val uid = user.uid()
                            val loginType = "google"
                            val userData = LocalUserData(email = email, uid = uid, loginType = loginType)

                            cont.resume(userData) {
                                println(it.message)
                            }
                        }
                    }

                }


            }
            else FIRAuth.auth().signInWithCredential(credential) { result, error ->


                if (result != null) {
                    val user = result.user()
                    val email = user.email()
                    val uid = user.uid()
                    val loginType = "google"

                    println("ios uid : ${uid}")
                    println("ios email : ${email}")

                    println("ios loginType : ${(user.providerData()[0] as? FIRUserInfoProtocol)?.providerID()}")


                    val userData = LocalUserData(email = email, uid = uid, loginType = loginType)

                    cont.resume(userData) {}

                } else cont.resume(null) {}

            }

        }
    }

    override suspend fun reAuthenticate(
        idToken: String,
        accessToken: String
    ): Boolean {
        return suspendCancellableCoroutine { cont ->
            val credential = FIRGoogleAuthProvider.credentialWithIDToken(idToken, accessToken)

            FIRAuth.auth().signInWithCredential(credential) { result, error ->


                if (result != null) cont.resume(true) {}
            }
        }
    }

    private var strongAuthDelegate: ASAuthorizationControllerDelegateProtocol? = null
    private var globalAppleDelegate: Any? = null

    override suspend fun linkInWithApple(): LocalUserData? =
        suspendCancellableCoroutine { continuation ->

            val rawNonce = randomNonceString()
            val hashedNonce = sha256(rawNonce)

//            val provider = ASAuthorizationAppleIDProvider()
//            val request = provider.createRequest().apply {
//                requestedScopes = listOf(ASAuthorizationScopeEmail, ASAuthorizationScopeFullName)
//                // 2. 요청에 해싱된 Nonce 설정
//                nonce = hashedNonce
//            }

            val provider = ASAuthorizationAppleIDProvider()
            val request = provider.createRequest().apply {
                requestedScopes = listOf(ASAuthorizationScopeEmail, ASAuthorizationScopeFullName)
            }


            val controller = ASAuthorizationController(listOf(request))
//            val delegate
//            strongAuthDelegate
            appleAuthDelegate = object : NSObject(),
                ASAuthorizationControllerDelegateProtocol,
                ASAuthorizationControllerPresentationContextProvidingProtocol {

                override fun presentationAnchorForAuthorizationController(controller: ASAuthorizationController): UIWindow {
//                    return UIApplication.sharedApplication.keyWindow
//                        ?: UIApplication.sharedApplication.windows.first() as UIWindow
                    return UIApplication.sharedApplication.windows.first() as UIWindow
                }
//                = platform.UIKit.UIApplication.sharedApplication.keyWindow!!

                override fun authorizationController(
                    controller: ASAuthorizationController,
                    didCompleteWithAuthorization: ASAuthorization
                ) {
                    val credential =
                        didCompleteWithAuthorization.credential as? ASAuthorizationAppleIDCredential
                    val idTokenData: NSData? = credential?.identityToken
                    val idToken =
                        idTokenData?.let { NSString.create(it, NSUTF8StringEncoding) }

                    println("idToken : ${idToken.toString()}")
                    val firebaseCredential =
                        FIROAuthProvider.appleCredentialWithIDToken(
                            idToken.toString(),
                            rawNonce,
                            null
                        )

//                    val firebaseCredential = FIROAuthProvider.credentialWithProviderID("apple.com",idToken.toString(), null)

                    val currentUser = FIRAuth.auth().currentUser()

                    if(currentUser != null && currentUser.isAnonymous()) {

                        currentUser.linkWithCredential(firebaseCredential) { result, error ->

                            if(error != null) {

                                continuation.resume(null) {
                                    println(it.message)
                                }
                            }
                            else {

                                if (result != null) {

                                    val user = result.user()
                                    val email = user.email()
                                    val uid = user.uid()
                                    val loginType = "apple"
                                    val userData =
                                        LocalUserData(email = email, uid = uid, loginType = loginType)

                                    continuation.resume(userData) {
                                        println(it.message)
                                    }
                                }
                            }


                        }
                    }
                    else FIRAuth.auth().signInWithCredential(firebaseCredential) { result, error ->

                        if (result != null) {
                            val user = result.user()
                            val email = user.email()
                            val uid = user.uid()
                            val loginType = "apple"

                            println("ios uid : ${uid}")
                            println("ios email : ${email}")

                            println("ios loginType : ${(user.providerData()[0] as? FIRUserInfoProtocol)?.providerID()}")


                            val userData =
                                LocalUserData(email = email, uid = uid, loginType = loginType)

                            continuation.resume(userData) {
                                println(it.message)
                            }

                        } else {
                            println(error?.localizedDescription)
                            continuation.resumeWithException(Exception(error?.localizedDescription))
                        }
                    }


                }

                override fun authorizationController(
                    controller: ASAuthorizationController,
                    didCompleteWithError: NSError
                ) {
                    println("Apple login error: ${didCompleteWithError.localizedDescription}")
                    continuation.resumeWithException(Exception(didCompleteWithError.localizedDescription))
                }
            }

            globalAppleDelegate = appleAuthDelegate

            controller.delegate = appleAuthDelegate as ASAuthorizationControllerDelegateProtocol
            controller.presentationContextProvider =
                appleAuthDelegate as ASAuthorizationControllerPresentationContextProvidingProtocol

            continuation.invokeOnCancellation {
                globalAppleDelegate = null
            }

            dispatch_async(dispatch_get_main_queue()) {
                controller.performRequests()
            }
        }

    @OptIn(BetaInteropApi::class)
    override suspend fun signInWithApple(): LocalUserData? =
        suspendCancellableCoroutine { continuation ->

            val rawNonce = randomNonceString()
            val hashedNonce = sha256(rawNonce)

//            val provider = ASAuthorizationAppleIDProvider()
//            val request = provider.createRequest().apply {
//                requestedScopes = listOf(ASAuthorizationScopeEmail, ASAuthorizationScopeFullName)
//                // 2. 요청에 해싱된 Nonce 설정
//                nonce = hashedNonce
//            }

            val provider = ASAuthorizationAppleIDProvider()
            val request = provider.createRequest().apply {
                requestedScopes = listOf(ASAuthorizationScopeEmail, ASAuthorizationScopeFullName)
            }


            val controller = ASAuthorizationController(listOf(request))
//            val delegate
//            strongAuthDelegate
            appleAuthDelegate = object : NSObject(),
                ASAuthorizationControllerDelegateProtocol,
                ASAuthorizationControllerPresentationContextProvidingProtocol {

                override fun presentationAnchorForAuthorizationController(controller: ASAuthorizationController): UIWindow {
//                    return UIApplication.sharedApplication.keyWindow
//                        ?: UIApplication.sharedApplication.windows.first() as UIWindow
                    return UIApplication.sharedApplication.windows.first() as UIWindow
                }
//                = platform.UIKit.UIApplication.sharedApplication.keyWindow!!

                override fun authorizationController(
                    controller: ASAuthorizationController,
                    didCompleteWithAuthorization: ASAuthorization
                ) {
                    val credential =
                        didCompleteWithAuthorization.credential as? ASAuthorizationAppleIDCredential
                    val idTokenData: NSData? = credential?.identityToken
                    val idToken =
                        idTokenData?.let { NSString.create(it, NSUTF8StringEncoding) }

                    println("idToken : ${idToken.toString()}")
                    val firebaseCredential =
                        FIROAuthProvider.appleCredentialWithIDToken(
                            idToken.toString(),
                            rawNonce,
                            null
                        )

//                    val firebaseCredential = FIROAuthProvider.credentialWithProviderID("apple.com",idToken.toString(), null)

//                     val currentUser = FIRAuth.auth().currentUser()

//                    if(currentUser != null && currentUser.isAnonymous()) {
//
//                        currentUser.linkWithCredential(firebaseCredential) { result, error ->
//
//                            if(error != null) {
//
//                                continuation.resume(null) {
//                                    println(it.message)
//                                }
//                            }
//                            else {
//
//                                if (result != null) {
//
//                                    val user = result.user()
//                                    val email = user.email()
//                                    val uid = user.uid()
//                                    val loginType = "apple"
//                                    val userData =
//                                        LocalUserData(email = email, uid = uid, loginType = loginType)
//
//                                    continuation.resume(userData) {
//                                        println(it.message)
//                                    }
//                                }
//                            }
//
//
//                        }
//                    }
//                    else
                        FIRAuth.auth().signInWithCredential(firebaseCredential) { result, error ->

                        if (result != null) {
                            val user = result.user()
                            val email = user.email()
                            val uid = user.uid()
                            val loginType = "apple"

                            println("ios uid : ${uid}")
                            println("ios email : ${email}")

                            println("ios loginType : ${(user.providerData()[0] as? FIRUserInfoProtocol)?.providerID()}")


                            val userData =
                                LocalUserData(email = email, uid = uid, loginType = loginType)

                            continuation.resume(userData) {
                                println(it.message)
                            }

                        } else {
                            println(error?.localizedDescription)
                            continuation.resumeWithException(Exception(error?.localizedDescription))
                        }
                    }


                }

                override fun authorizationController(
                    controller: ASAuthorizationController,
                    didCompleteWithError: NSError
                ) {
                    println("Apple login error: ${didCompleteWithError.localizedDescription}")
                    continuation.resumeWithException(Exception(didCompleteWithError.localizedDescription))
                }
            }

            globalAppleDelegate = appleAuthDelegate

            controller.delegate = appleAuthDelegate as ASAuthorizationControllerDelegateProtocol
            controller.presentationContextProvider =
                appleAuthDelegate as ASAuthorizationControllerPresentationContextProvidingProtocol

            continuation.invokeOnCancellation {
                globalAppleDelegate = null
            }

            dispatch_async(dispatch_get_main_queue()) {
                controller.performRequests()
            }
        }


    override suspend fun signOut() {
        FIRAuth.auth().signOut(error = null)
    }

    override suspend fun deleteAccount() {

        val currentUser = FIRAuth.auth().currentUser()

        FIRFirestore.firestore().collectionWithPath("users")
            .documentWithPath(currentUser?.uid().toString()).deleteDocument()

        FIRAuth.auth().currentUser()?.deleteWithCompletion {

        }

    }

    override fun currentUser(): LocalUserData? {

        val user = FIRAuth.auth().currentUser()
        val loginType = if (user?.isAnonymous() == true) "anonymous"
        else when ((user?.providerData()[0] as? FIRUserInfoProtocol)?.providerID()) {
            "google.com" -> "google"
            "apple.com" -> "apple"
            else -> ""
        }

        return if (user == null) null else LocalUserData(
            uid = user.uid(),
            email = if (user.isAnonymous()) "" else user.email(), loginType = loginType
        )
    }

    fun randomNonceString(length: Int = 32): String {
        val charset = "0123456789ABCDEFGHIJKLMNOPQRSTUVXYZabcdefghijklmnopqrstuvwxyz-._"
        val result = StringBuilder()
        var remainingLength = length

        while (remainingLength > 0) {
            val random = (0..charset.lastIndex).random() // 간단한 랜덤 (보안성을 높이려면 SecRandomCopyBytes 권장)
            result.append(charset[random])
            remainingLength--
        }
        return result.toString()
    }

    // 2. SHA256 해싱 함수 (Apple에 보낼 때 사용)
    @OptIn(ExperimentalForeignApi::class)
    fun sha256(input: String): String {
        val data = input.encodeToByteArray()
        val hash = UByteArray(CC_SHA256_DIGEST_LENGTH)

        data.usePinned { pinned ->
            hash.usePinned { pinnedHash ->
                CC_SHA256(
                    pinned.addressOf(0),
                    data.size.toUInt() - 1u,
                    hash.refTo(0)
                ) // -1u는 null terminator 제외
            }

        }

        return hash.joinToString("") { byte ->
            // UByte를 Int로 변환
            val value = byte.toInt()

            // 1. 상위 4비트(High Nibble) 추출: 첫 번째 16진수 자릿수
            val highNibble = (value shr 4) and 0xF

            // 2. 하위 4비트(Low Nibble) 추출: 두 번째 16진수 자릿수
            val lowNibble = value and 0xF

            // HEX_CHARS에서 해당하는 문자를 찾아 문자열로 합칩니다.
            // 이는 "%02x"와 동일한 결과를 냅니다.
            "${HEX_CHARS[highNibble]}${HEX_CHARS[lowNibble]}"
        }
    }

    private val HEX_CHARS = "0123456789abcdef"
}
