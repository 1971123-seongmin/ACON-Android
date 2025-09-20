package com.acon.feature.profile.info.composable

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import com.acon.acon.core.model.model.profile.BirthDateStatus
import com.acon.acon.core.model.model.profile.Profile
import com.acon.acon.core.model.model.profile.ProfileImageStatus
import com.acon.acon.core.model.model.profile.SavedSpot
import com.acon.acon.core.model.model.profile.SpotThumbnailStatus
import com.acon.feature.profile.TestTags
import com.acon.feature.profile.info.viewmodel.ProfileInfoUiState
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class ProfileInfoScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    val dummyProfile = Profile(
        nickname = "",
        birthDate = BirthDateStatus.NotSpecified,
        image = ProfileImageStatus.Default
    )

    @Test
    fun `로그인_사용자일_경우_로그인_사용자용_프로필_뷰가_보인다`() {
        // Given
        val mockActions = mockk<ProfileInfoScreenActions>(relaxed = true)
        composeTestRule.setContent {
            ProfileInfoScreen(
                state = ProfileInfoUiState.User(
                    profile = dummyProfile,
                    savedSpots = emptyList()
                ),
                actions = mockActions
            )
        }

        // When
        val userProfileViewNode = composeTestRule.onNodeWithTag(TestTags.USER_PROFILE_VIEW)

        // Then
        userProfileViewNode.assertIsDisplayed()
    }

    @Test
    fun `비로그인_사용자일_경우_비로그인_사용자용_프로필_뷰가_보인다`() {
        // Given
        val mockActions = mockk<ProfileInfoScreenActions>(relaxed = true)
        composeTestRule.setContent {
            ProfileInfoScreen(
                state = ProfileInfoUiState.Guest,
                actions = mockActions
            )
        }

        // When
        val guestProfileViewNode = composeTestRule.onNodeWithTag(TestTags.GUEST_PROFILE_VIEW)

        // Then
        guestProfileViewNode.assertIsDisplayed()
    }

    @Test
    fun `설정_아이콘_클릭_시_설정_아이콘_클릭_람다를_실행한다`() {
        // Given
        val mockActions = mockk<ProfileInfoScreenActions>(relaxed = true)
        composeTestRule.setContent {
            ProfileInfoScreen(
                state = ProfileInfoUiState.User(
                    profile = dummyProfile,
                    savedSpots = emptyList()
                ),
                actions = mockActions
            )
        }

        // When
        val settingIconNode = composeTestRule.onNodeWithTag(TestTags.SETTING_ICON)
        settingIconNode.performClick()

        // Then
        verify(exactly = 1) { mockActions.onSettingIconClick() }
    }

    @Test
    fun `프로필_수정_아이콘_클릭_시_프로필_수정_아이콘_클릭_람다를_실행한다`() {
        // Given
        val mockActions = mockk<ProfileInfoScreenActions>(relaxed = true)
        composeTestRule.setContent {
            ProfileInfoScreen(
                state = ProfileInfoUiState.User(
                    profile = dummyProfile,
                    savedSpots = emptyList()
                ),
                actions = mockActions
            )
        }

        // When
        val profileUpdateIconNode = composeTestRule.onNodeWithTag(TestTags.PROFILE_UPDATE_ICON)
        profileUpdateIconNode.performClick()

        // Then
        verify(exactly = 1) { mockActions.onProfileUpdateIconClick() }
    }

    @Test
    fun `저장한_장소_아이템_클릭_시_해당_장소_ID를_파라미터로_받는_람다를_실행한다`() {
        // Given
        val sampleSpotId = 123L
        val mockActions = mockk<ProfileInfoScreenActions>(relaxed = true)

        composeTestRule.setContent {
            ProfileInfoScreen(
                state = ProfileInfoUiState.User(
                    profile = dummyProfile,
                    savedSpots = listOf(
                        SavedSpot(
                            spotId = sampleSpotId,
                            spotName = "Dummy name",
                            spotThumbnail = SpotThumbnailStatus.Empty
                        )
                    )
                ),
                actions = mockActions
            )
        }

        // When
        val savedSpotItemNode = composeTestRule.onNodeWithTag(TestTags.SAVED_SPOT_ITEM + sampleSpotId)
        savedSpotItemNode.performClick()

        // Then
        verify(exactly = 1) { mockActions.onSavedSpotItemClick(sampleSpotId) }
    }

    @Test
    fun `화면을_수직_스크롤해도_바텀_바는_움직이지_않는다`() {
        // Given
        val mockActions = mockk<ProfileInfoScreenActions>(relaxed = true)
        composeTestRule.setContent {
            ProfileInfoScreen(
                state = ProfileInfoUiState.User(
                    profile = dummyProfile,
                    savedSpots = emptyList()
                ),
                actions = mockActions
            )
        }

        val bottomBarNode = composeTestRule.onNodeWithTag(TestTags.BOTTOM_BAR)
        val verticalScrollableViewNode = composeTestRule.onNodeWithTag(TestTags.VERTICAL_SCROLLABLE_VIEW)

        // When
        val expectedBottomBarTop = bottomBarNode.getBoundsInRoot().top
        verticalScrollableViewNode.performTouchInput { swipeUp() }

        val actualBottomBarTop = bottomBarNode.getBoundsInRoot().top

        // Then
        assertEquals(expectedBottomBarTop, actualBottomBarTop)
        bottomBarNode.assertIsDisplayed()
    }
}
