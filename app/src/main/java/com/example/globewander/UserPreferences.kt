package com.example.globewander

data class UserPreferences(
    val interests: List<String> = listOf(),
    val selectedCountries: List<String> = listOf(),
)
