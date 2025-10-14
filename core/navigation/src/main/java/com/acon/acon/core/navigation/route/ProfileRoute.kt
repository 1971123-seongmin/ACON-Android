package com.acon.acon.core.navigation.route

import kotlinx.serialization.Serializable

interface ProfileRoute {

    @Serializable
    data object Graph : ProfileRoute

    @Serializable
    data object ProfileInfo : ProfileRoute

    @Serializable
    data object ProfileUpdate : ProfileRoute

    @Serializable
    data object Bookmark : ProfileRoute
}