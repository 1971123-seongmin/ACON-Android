package com.acon.acon.navigation.nested

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.acon.acon.core.designsystem.effect.screenDefault
import com.acon.acon.core.model.model.spot.SpotNavigationParameter
import com.acon.acon.core.navigation.route.ProfileRoute
import com.acon.acon.core.navigation.route.SettingsRoute
import com.acon.acon.core.navigation.route.SpotRoute
import com.acon.acon.core.navigation.route.UploadRoute
import com.acon.feature.profile.savedspot.composable.BookmarkScreenContainer
import com.acon.feature.profile.info.composable.ProfileInfoScreenContainer
import com.acon.feature.profile.update.composable.ProfileUpdateScreenContainer

internal fun NavGraphBuilder.profileNavigation(
    navController: NavHostController,
) {
    navigation<ProfileRoute.Graph>(
        startDestination = ProfileRoute.ProfileInfo,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable<ProfileRoute.ProfileInfo> {
            ProfileInfoScreenContainer(
                modifier = Modifier
                    .screenDefault()
                    .statusBarsPadding(),
                onNavigateToProfileUpdate = {
                    navController.navigate(ProfileRoute.ProfileUpdate)
                },
                onNavigateToSpotDetail = {
                    navController.navigate(SpotRoute.SpotDetail(it))
                },
                onNavigateToSavedSpots = {
                    navController.navigate(ProfileRoute.Bookmark)
                },
                onNavigateToSetting = {
                    navController.navigate(SettingsRoute.Settings)
                },
                onNavigateToSpotList = {
                    navController.popBackStack(
                        route = SpotRoute.SpotList,
                        inclusive = false
                    )
                },
                onNavigateToUpload = {
                    navController.navigate(UploadRoute.Graph)
                }
            )
        }

        composable<ProfileRoute.ProfileUpdate> { backStackEntry ->
            ProfileUpdateScreenContainer(
                onNavigateBack = navController::navigateUp,
                modifier = Modifier.screenDefault().systemBarsPadding()
            )
        }

        composable<ProfileRoute.Bookmark> {
            BookmarkScreenContainer(
                modifier = Modifier.fillMaxSize(),
                onNavigateToBack = {
                    navController.popBackStack()
                },
                onNavigateToSpotDetailScreen = {
                    navController.navigate(
                        SpotRoute.SpotDetail(
                            SpotNavigationParameter(
                                it,
                                emptyList(),
                                null,
                                null,
                                null,
                                true
                            )
                        )
                    )
                },
            )
        }
    }
}