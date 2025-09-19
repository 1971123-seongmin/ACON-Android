package com.acon.acon.domain.error.profile

import com.acon.acon.domain.error.RootError
import com.acon.acon.domain.error.UNSPECIFIED_SERVER_ERROR_CODE

open class ValidateBirthDateError : RootError() {

    class InputIsFuture : ValidateBirthDateError()
    class InputIsTooPast : ValidateBirthDateError()
    class InvalidFormat : ValidateBirthDateError() {
        override val code = UNSPECIFIED_SERVER_ERROR_CODE
    }

    override fun createErrorInstances(): Array<RootError> {
        return arrayOf()
    }
}