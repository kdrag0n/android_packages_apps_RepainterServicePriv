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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.om.OverlayManager
import android.os.Parcel
import android.provider.Settings
import android.util.Log
import java.io.File

private const val TAG = "PS/SysReceiver"

private val CLIENT_PACKAGES = setOf(
    "dev.kdrag0n.dyntheme",
    "dev.kdrag0n.dyntheme.debug",
)

class PackageRemovedReceiver : BroadcastReceiver() {
    // Clean up themes on uninstall
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_PACKAGE_FULLY_REMOVED) {
            // Spoofed
            return
        }

        val pkg = intent.data!!.encodedSchemeSpecificPart
        if (pkg !in CLIENT_PACKAGES) {
            return
        }

        val actions = readUninstallActions(context) ?: return
        Log.i(TAG, "Performing uninstall actions for $pkg")

        try {
            // Less likely to fail, so do this first
            actions.secureSettings?.forEach { (k, v) ->
                Settings.Secure.putString(context.contentResolver, k, v)
            }

            actions.overlayTransaction?.let {
                val overlayManager = context.getSystemService(Context.OVERLAY_SERVICE) as OverlayManager
                overlayManager.commit(it)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to perform uninstall actions", e)
        }
    }

    private fun readUninstallActions(context: Context): UninstallActions? {
        val deContext = context.createDeviceProtectedStorageContext()
        val file = File(deContext.filesDir, UNINSTALL_ACTIONS_FILE)
        if (!file.exists()) {
            return null
        }

        val parcel = Parcel.obtain()
        val txn = try {
            val data = file.readBytes()
            parcel.unmarshall(data, 0, data.size)
            parcel.setDataPosition(0)
            parcel.readParcelable<UninstallActions?>(context.classLoader)
        } finally {
            parcel.recycle()
        }

        file.delete()
        return txn
    }
}
