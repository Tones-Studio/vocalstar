/*
 * Copyright 2018 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tech41.tones.vocalstar.controls

import android.os.AsyncTask


/**
 * Base class for an async task that fetches a list of media apps.
 */
abstract class FindMediaAppsTask constructor(
    private val callback: AppListUpdatedCallback, private val sortAlphabetical: Boolean
) : AsyncTask<Void, Void, List<MediaAppDetails>>() {

    /**
     * Callback used by [FindMediaAppsTask].
     */
    interface AppListUpdatedCallback {
        fun onAppListUpdated(mediaAppEntries: List<MediaAppDetails>)
    }

  abstract val mediaApps: List<MediaAppDetails>

    override fun doInBackground(vararg params: Void): List<MediaAppDetails> {
        val mediaApps = ArrayList(mediaApps)
        if(sortAlphabetical) {
            // Sort the list by localized app name for convenience.
            mediaApps.sortWith(Comparator { left, right ->
                left.appName.compareTo(right.appName, ignoreCase = true)
            })
        }
        return mediaApps
    }

    override fun onPostExecute(mediaAppEntries: List<MediaAppDetails>) {
        callback.onAppListUpdated(mediaAppEntries)
    }
}