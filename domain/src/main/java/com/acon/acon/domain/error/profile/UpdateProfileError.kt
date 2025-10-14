package com.acon.acon.domain.error.profile

import com.acon.acon.domain.error.RootError

open class UpdateProfileError : RootError() {

    class AlreadyExistNickname : UpdateProfileError() {
        override val code: Int = 40901
    }
    class InvalidNicknameFormat : UpdateProfileError() {
        override val code: Int = 40051
    }
    class InvalidBirthDateFormat : UpdateProfileError() {
        override val code: Int = 40053
    }
    class InvalidBucketImagePath : UpdateProfileError() {
        override val code: Int = 40052
    }
    class InvalidImageType: UpdateProfileError() {
        override val code: Int = 40045
    }
    class InternalServerError: UpdateProfileError() {
        override val code: Int = 50005
    }

    final override fun createErrorInstances(): Array<RootError> {
        return arrayOf(
            AlreadyExistNickname(),
            InvalidNicknameFormat(),
            InvalidBirthDateFormat(),
            InvalidBucketImagePath(),
            InvalidImageType(),
            InternalServerError()
        )
    }
}