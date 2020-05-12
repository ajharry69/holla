package com.xently.holla.providers

import android.content.SearchRecentSuggestionsProvider
import com.xently.holla.BuildConfig

class SearchSuggestionsProvider : SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        const val AUTHORITY =
            BuildConfig.APPLICATION_ID.plus(".providers.SearchSuggestionsProvider")
        const val MODE: Int = DATABASE_MODE_QUERIES
    }
}