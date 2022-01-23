/*
 * Copyright (c) 2022 Danny Lin <danny@kdrag0n.dev>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.kdrag0n.dyntheme.service.privileged.sys

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.om.OverlayManager

class SystemPrivilegedService : Service() {
    private lateinit var binder: SystemPrivilegedServiceImpl

    override fun onCreate() {
        binder = SystemPrivilegedServiceImpl(
            service = this,
            overlayManager = getSystemService("overlay") as OverlayManager,
        )
    }

    override fun onBind(intent: Intent) = binder
}
