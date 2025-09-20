package com.acon.acon.domain.usecase

import com.acon.acon.domain.error.profile.ValidateBirthDateError
import java.time.LocalDate
import javax.inject.Inject

class ValidateBirthDateUseCase @Inject constructor() {

    private val pastThreshold = LocalDate.of(1900, 1, 1)

    suspend operator fun invoke(birthDate: LocalDate) : Result<Unit> {
        return when {
            birthDate.isAfter(LocalDate.now()) -> Result.failure(ValidateBirthDateError.InputIsFuture())
            birthDate.isBefore(pastThreshold) -> Result.failure(ValidateBirthDateError.InputIsTooPast())
            else -> Result.success(Unit)
        }
    }
}