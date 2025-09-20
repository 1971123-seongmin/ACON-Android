package com.acon.acon.domain.error.profile

import com.acon.acon.domain.error.RootError

open class ValidateNicknameError : RootError() {

    class EmptyInput : ValidateNicknameError()
    class InputLengthExceeded : ValidateNicknameError()
    class InvalidFormat : ValidateNicknameError() {
        override val code: Int = 40051
    }
    class AlreadyExist : ValidateNicknameError() {
        override val code: Int = 40901
    }

    final override fun createErrorInstances(): Array<RootError> {
        return arrayOf(
            InvalidFormat(),
            AlreadyExist()
        )
    }
}