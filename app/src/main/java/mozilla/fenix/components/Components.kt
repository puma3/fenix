/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.fenix.components

import android.content.Context
import kotlinx.coroutines.experimental.async
import mozilla.components.browser.engine.gecko.GeckoEngine
import mozilla.components.browser.search.SearchEngineManager
import mozilla.components.browser.session.Session
import mozilla.components.concept.engine.Engine
import mozilla.components.feature.search.SearchUseCases
import mozilla.components.feature.session.DefaultSessionStorage
import mozilla.components.feature.session.SessionProvider
import mozilla.components.feature.session.SessionUseCases
import org.mozilla.geckoview.GeckoRuntime

/**
 * Helper class for lazily instantiating components needed by the application.
 */
class Components(private val applicationContext: Context) {

    // Engine
    private val geckoRuntime by lazy {
        GeckoRuntime.getDefault(applicationContext)
    }
    val engine : Engine by lazy { GeckoEngine(geckoRuntime) }

    // Session
    val sessionProvider : SessionProvider by lazy {
        SessionProvider(Session("https://www.mozilla.org"), DefaultSessionStorage(applicationContext))
    }
    val sessionUseCases = SessionUseCases(sessionProvider, engine)

    // Search
    private val searchEngineManager by lazy {
        SearchEngineManager().apply {
            async { load(applicationContext) }
        }
    }
    private val searchUseCases = SearchUseCases(applicationContext, searchEngineManager, sessionProvider)
    val defaultSearchUseCase = { searchTerms: String -> searchUseCases.defaultSearch.invoke(searchTerms) }
}