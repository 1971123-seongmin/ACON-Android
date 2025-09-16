package com.acon.acon.domain.error.profile

import com.acon.acon.domain.error.RootError
import com.acon.acon.domain.error.UNSPECIFIED_SERVER_ERROR_CODE

open class UpdateProfileError : RootError() {

    class AlreadyExistNickname : UpdateProfileError() {
        override val code: Int = UNSPECIFIED_SERVER_ERROR_CODE
    }
    class InvalidNicknameFormat : UpdateProfileError() {
        override val code: Int = UNSPECIFIED_SERVER_ERROR_CODE
    }
    class InvalidBirthDateFormat : UpdateProfileError() {
        override val code: Int = UNSPECIFIED_SERVER_ERROR_CODE
    }

    class InvalidImageType: UpdateProfileError() {
        override val code = 40045
    }

    class InternalServerError: UpdateProfileError() {
        override val code = 50005
    }

    final override fun createErrorInstances(): Array<RootError> {
        return arrayOf(
            AlreadyExistNickname(),
            InvalidNicknameFormat(),
            InvalidBirthDateFormat(),
            InvalidImageType(),
            InternalServerError()
        )
    }
}