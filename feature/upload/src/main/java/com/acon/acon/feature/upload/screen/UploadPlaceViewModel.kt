package com.acon.acon.feature.upload.screen

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.acon.acon.core.model.model.upload.Feature
import com.acon.acon.core.model.model.upload.SearchedSpotByMap
import com.acon.acon.core.model.type.CafeFeatureType
import com.acon.acon.core.model.type.CategoryType
import com.acon.acon.core.model.type.ImageType
import com.acon.acon.core.model.type.PriceFeatureType
import com.acon.acon.core.model.type.RestaurantFeatureType
import com.acon.acon.core.model.type.SpotType
import com.acon.acon.domain.repository.AconAppRepository
import com.acon.acon.domain.repository.MapSearchRepository
import com.acon.acon.domain.repository.UploadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class UploadPlaceViewModel @Inject constructor(
    private val mapSearchRepository: MapSearchRepository,
    private val aconAppRepository: AconAppRepository,
    private val uploadRepository: UploadRepository,
    application: Application
) : AndroidViewModel(application), ContainerHost<UploadPlaceUiState, UploadPlaceSideEffect> {

    override val container =
        container<UploadPlaceUiState, UploadPlaceSideEffect>(UploadPlaceUiState()) {
            viewModelScope.launch {
                queryFlow
                    .debounce(100)
                    .distinctUntilChanged()
                    .collect { query ->
                        if (query.isBlank()) {
                            reduce {
                                state.copy(
                                    recommendMenu = "",
                                )
                            }
                        } else {
                            reduce {
                                state.copy(
                                    recommendMenu = query,
                                )
                            }
                        }
                    }
            }

            viewModelScope.launch {
                searchPlaceQueryFlow
                    .debounce(100)
                    .distinctUntilChanged()
                    .collect { query ->
                        if (query.isBlank()) {
                            reduce {
                                state.copy(
                                    searchedSpotsByMap = emptyList(),
                                    showSearchedSpotsByMap = false,
                                    isNextBtnEnabled = false
                                )
                            }
                        } else {
                            mapSearchRepository.fetchMapSearch(query).onSuccess {
                                reduce {
                                    state.copy(
                                        searchedSpotsByMap = it
                                    )
                                }
                            }.onFailure {
                                if (it.message?.contains("HTTP 429") == true) {
                                    reduce {
                                        state.copy(
                                            showUploadPlaceLimitDialog = true
                                        )
                                    }
                                }
                            }
                        }
                    }
            }
        }

    private val queryFlow = MutableStateFlow("")
    private val searchPlaceQueryFlow = MutableStateFlow("")

    fun onSearchQueryChanged(query: String) = intent {
        queryFlow.value = query
    }

    fun onSearchQueryOrSelectionChanged(query: String, isSelection: Boolean) = intent {
        reduce {
            if (isSelection)
                state.copy(
                    showSearchedSpotsByMap = false
                )
            else
                state.copy(
                    selectedSpotByMap = state.selectedSpotByMap?.takeIf { it.title == query },
                    showSearchedSpotsByMap = query.isNotBlank()
                )
        }
        searchPlaceQueryFlow.value = query
    }

    fun onSearchSpotByMapClicked(searchedSpotByMap: SearchedSpotByMap, onUpdateTextField: () -> Unit) = intent {
        onUpdateTextField()
        reduce {
            state.copy(
                selectedSpotByMap = searchedSpotByMap,
                showSearchedSpotsByMap = false,
                isNextBtnEnabled = true
            )
        }
    }

    fun onPreviousBtnDisabled() = intent {
        reduce { state.copy(isPreviousBtnEnabled = false)}
    }

    fun onPreviousBtnEnabled() = intent {
        reduce { state.copy(isPreviousBtnEnabled = true)}
    }

    fun updateNextBtnEnabled(isEnabled: Boolean) = intent {
        reduce { state.copy(isNextBtnEnabled = isEnabled)}
    }

    fun updateSpotType(spotType: SpotType) = intent {
        reduce { state.copy(selectedSpotType = spotType) }
    }

    fun updateCafeOptionType(cafeOption: CafeFeatureType.CafeType) = intent {
        reduce { state.copy(selectedFeature = SelectedFeature.Cafe(cafeOption)) }
    }

    fun updatePriceOptionType(priceOption: PriceFeatureType.PriceOptionType) = intent {
        reduce { state.copy(selectedPriceOption = priceOption) }
    }

    fun updateRestaurantType(type: RestaurantFeatureType.RestaurantType) = intent {
        reduce {
            val currentSelectedTypes = (state.selectedFeature as? SelectedFeature.Restaurant)?.types?.toMutableList() ?: mutableListOf()

            if (currentSelectedTypes.contains(type)) {
                currentSelectedTypes.remove(type)
            } else {
                currentSelectedTypes.add(type)
            }
            state.copy(selectedFeature = SelectedFeature.Restaurant(currentSelectedTypes))
        }
    }

    fun onAddImageUris(uris: List<Uri>) = intent {
        reduce {
            val currentUris = state.selectedImageUris ?: emptyList()
            val canAddCount = state.maxImageCount - currentUris.size

            if (canAddCount <= 0) {
                state
            } else {
                val toAdd = uris.take(canAddCount)
                state.copy(selectedImageUris = currentUris.plus(toAdd))
            }
        }
    }

    fun onRemoveImageUri(uri: Uri) = intent {
        val currentUris = state.selectedImageUris?.toMutableList()
        val removedSuccessfully = currentUris?.remove(uri)

        if (removedSuccessfully == true) {
            reduce {
                state.copy(selectedImageUris = currentUris)
            }
        }
    }

    fun onRequestRemoveUploadPlaceImageDialog(uri: Uri) = intent {
        reduce {
            state.copy(
                showRemoveUploadPlaceImageDialog = true,
                selectedUriToRemove = uri
            )
        }
    }

    fun onDismissRemoveUploadPlaceImageDialog() = intent {
        reduce {
            state.copy(
                showRemoveUploadPlaceImageDialog = false,
                selectedUriToRemove = null
            )
        }
    }

    fun goToNextStep(lastStepIndex: Int) = intent {
        if (state.currentStep < lastStepIndex) {
            reduce {
                state.copy(currentStep = state.currentStep + 1)
            }
        }
    }

    fun goToPreviousStep() = intent {
        if (state.currentStep > 0) {
            reduce {
                state.copy(currentStep = state.currentStep - 1)
            }
        }
    }

    fun onRequestExitUploadPlaceDialog() = intent {
        reduce {
            state.copy(showExitUploadPlaceDialog = true)
        }
    }

    fun onDismissExitUploadPlaceDialog() = intent {
        reduce {
            state.copy(showExitUploadPlaceDialog = false)
        }
    }

    fun onHideSearchedPlaceList() = intent {
        reduce {
            state.copy(
                showSearchedSpotsByMap = false
            )
        }
    }

    fun onRequestUploadPlaceLimitPouUp() = intent {
        reduce {
            state.copy(
                showUploadPlaceLimitPouUp = true
            )
        }

        viewModelScope.launch {
            delay(3500)
            intent {
                reduce {
                    state.copy(showUploadPlaceLimitPouUp = false)
                }
            }
        }
    }

    fun onSlideAnimationEnd(route: String) = intent {
        reduce {
            val updatedMap = state.hasAnimated.toMutableMap().apply { this[route] = true }
            state.copy(hasAnimated = updatedMap)
        }
    }

    fun onNavigateToBack() = intent {
        postSideEffect(UploadPlaceSideEffect.OnNavigateToBack)
    }

    fun onClickReportPlace() = intent {
        postSideEffect(UploadPlaceSideEffect.OnMoveToReportPlace)
    }

    private fun createFeatureList() = intent {
        val featureRequests = mutableListOf<Feature>()

        when (val feature = state.selectedFeature) {
            is SelectedFeature.Cafe -> {
                if (feature.option == CafeFeatureType.CafeType.WORK_FRIENDLY) {
                    featureRequests.add(
                        Feature(
                            category = CategoryType.CAFE_FEATURE,
                            optionList = listOf(feature.option)
                        )
                    )
                }
            }
            is SelectedFeature.Restaurant -> {
                featureRequests.add(
                    Feature(
                        category = CategoryType.RESTAURANT_FEATURE,
                        optionList = feature.types
                    )
                )
            }

            null -> TODO()
        }
        state.selectedPriceOption?.let { priceOption ->
            featureRequests.add(
                Feature(
                    category = CategoryType.PRICE,
                    optionList = listOf(priceOption)
                )
            )
        }

        reduce {
            state.copy(
                featureList = featureRequests.toImmutableList()
            )
        }
    }

    fun onSubmitUploadPlace(onSuccess:() -> Unit) = intent {
        createFeatureList()
        reduce { state.copy(isNextBtnEnabled = false) }
        when (state.selectedImageUris?.isEmpty()) {
            true -> {
                submitUploadPlace(onSuccess)
            }

            false -> {
                uploadAllImagesAndSubmit(onSuccess)
            }

            null -> {}
        }
    }

    private fun submitUploadPlace(
        onSuccess:() -> Unit,
        imageList: List<String> = emptyList()
    ) = intent {
        uploadRepository.submitUploadPlace(
            spotName = state.selectedSpotByMap?.title ?: "",
            address = state.selectedSpotByMap?.address ?: "",
            spotType = state.selectedSpotType ?: SpotType.CAFE,
            featureList = state.featureList ?: emptyList(),
            recommendedMenu = state.recommendMenu ?: "",
            imageList = imageList
        ).onSuccess {
            reduce { state.copy(isNextBtnEnabled = true) }
            onSuccess()
        }.onFailure {
            reduce { state.copy(isNextBtnEnabled = true) }
            postSideEffect(UploadPlaceSideEffect.ShowToastUploadFailed)
        }
    }

    private fun uploadAllImagesAndSubmit(onSuccess: () -> Unit) = intent {
        val uris = state.selectedImageUris ?: emptyList()

        if (uris.isEmpty()) {
            submitUploadPlace(onSuccess = onSuccess, imageList = emptyList())
            return@intent
        }

        val presignedResults = runCatching {
            coroutineScope {
                uris.map { uri ->
                    async(Dispatchers.IO) {
                        aconAppRepository.uploadImage(ImageType.SPOT, uri.toString()).getOrThrow()
                    }
                }.awaitAll()
            }
        }.onFailure {
            postSideEffect(UploadPlaceSideEffect.ShowToastUploadImageFailed)
            return@intent
        }.getOrThrow()

        val bucketUrls = presignedResults.map { it }
        submitUploadPlace(onSuccess = onSuccess, imageList = bucketUrls)
    }

    companion object {
        const val TAG = "UploadPlaceViewModel"
    }
}

@Immutable
data class UploadPlaceUiState(
    val hasAnimated: Map<String, Boolean> = emptyMap(),
    val isPreviousBtnEnabled: Boolean = false,
    val isNextBtnEnabled: Boolean = false,
    val showExitUploadPlaceDialog: Boolean = false,
    val showRemoveUploadPlaceImageDialog: Boolean = false,
    val showUploadPlaceLimitDialog: Boolean = false,
    val showUploadPlaceLimitPouUp: Boolean = false,
    val showSearchedSpotsByMap: Boolean = false,
    val searchedSpotsByMap: List<SearchedSpotByMap> = listOf(),

    val featureList :List<Feature>? = emptyList(),
    val selectedSpotByMap: SearchedSpotByMap? = null,
    val selectedSpotType: SpotType? = null,
    val selectedPriceOption: PriceFeatureType.PriceOptionType? = null,
    val selectedFeature: SelectedFeature? = null,
    val recommendMenu: String? = "",
    val selectedImageUris: List<Uri>? = emptyList(),
    val uploadFileName: String = "",

    val selectedUriToRemove: Uri? = null,
    val maxImageCount: Int = 10,
    val currentStep: Int = 0
)

sealed class SelectedFeature {
    data class Cafe(val option: CafeFeatureType.CafeType) : SelectedFeature()
    data class Restaurant(val types: List<RestaurantFeatureType.RestaurantType>) : SelectedFeature()
}

sealed interface UploadPlaceSideEffect {
    data object ShowToastUploadFailed : UploadPlaceSideEffect
    data object ShowToastUploadImageFailed : UploadPlaceSideEffect
    data object OnNavigateToBack : UploadPlaceSideEffect
    data object OnMoveToReportPlace : UploadPlaceSideEffect
}
