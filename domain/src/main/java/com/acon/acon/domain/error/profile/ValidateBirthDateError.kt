package com.acon.acon.domain.error.profile

import com.acon.acon.domain.error.RootError

open class ValidateBirthDateError : RootError() {

    class InputIsFuture : ValidateBirthDateError()
    class InputIsTooPast : ValidateBirthDateError()
    class InvalidFormat : ValidateBirthDateError() {
        override val code = -1
    }

    override fun createErrorInstances(): Array<RootError> {
        return arrayOf()
    }
}