package com.acon.acon.domain.error.profile

import com.acon.acon.domain.error.RootError
import com.acon.acon.domain.error.UNSPECIFIED_SERVER_ERROR_CODE

open class ValidateNicknameError : RootError() {

    class EmptyInput : ValidateNicknameError()
    class InputLengthExceeded : ValidateNicknameError()
    class InvalidFormat : ValidateNicknameError() {
        override val code: Int = UNSPECIFIED_SERVER_ERROR_CODE
    }
    class AlreadyExist : ValidateNicknameError() {
        override val code: Int = UNSPECIFIED_SERVER_ERROR_CODE
    }

    final override fun createErrorInstances(): Array<RootError> {
        return arrayOf(
            InvalidFormat(),
            AlreadyExist()
        )
    }
}