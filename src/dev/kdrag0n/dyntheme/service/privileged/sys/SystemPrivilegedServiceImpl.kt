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
import android.content.om.OverlayIdentifier
import android.content.om.OverlayInfo
import android.content.om.OverlayManager
import android.content.om.OverlayManagerTransaction
import android.os.Parcel
import android.os.SystemProperties
import android.os.UserHandle
import android.provider.Settings
import android.util.Log
import dev.kdrag0n.dyntheme.service.privileged.IPrivilegedService
import java.io.File
import java.lang.IllegalStateException

const val PRIV_SERVICE_VERSION = 1

private const val TAG = "PS/SystemPriv"
const val UNINSTALL_ACTIONS_FILE = "uninstall_actions.bin"

class SystemPrivilegedServiceImpl(
    private val service: Service,
    private val overlayManager: OverlayManager,
) : IPrivilegedService.Stub() {
    // For cleaning up themes on uninstall
    private var uninstallActions: UninstallActions? = null

    override fun getServiceVersion() = PRIV_SERVICE_VERSION

    override fun getOverlayInfo(
        identifier: OverlayIdentifier,
        user: UserHandle,
    ): OverlayInfo? = wrap("getOverlayInfo") {
        overlayManager.getOverlayInfo(identifier, user)
    }

    override fun getOverlayInfosForTarget(
        targetPackageName: String,
        user: UserHandle,
    ): List<OverlayInfo> = wrap("getOverlayInfosForTarget") {
        overlayManager.getOverlayInfosForTarget(targetPackageName, user)
    }

    override fun setOverlayEnabled(
        packageName: String,
        enable: Boolean,
        user: UserHandle,
    ) = wrap("setOverlayEnabled") {
        overlayManager.setEnabled(packageName, enable, user)
    }

    override fun setOverlayEnabledExclusiveInCategory(
        packageName: String,
        user: UserHandle,
    ) = wrap("setOverlayEnabledExclusiveInCategory") {
        overlayManager.setEnabledExclusiveInCategory(packageName, user)
    }

    override fun commitOverlayTransaction(
        txn: OverlayManagerTransaction,
    ) = wrap("commitOverlayTransaction") {
        overlayManager.commit(txn)
    }

    override fun setUninstallActions(actions: UninstallActions?) {
        uninstallActions = actions
        persistUninstallActions()
    }

    override fun setBootanimProp(key: String, value: String) = wrap("setBootanimProp") {
        SystemProperties.set("persist.bootanim.$key", value)
    }

    override fun setSecureSetting(key: String, value: String?) = wrap("setSecureSetting") {
        Settings.Secure.putString(service.contentResolver, key, value)
    }

    override fun destroy() {
        // System service is managed differently, so this isn't normally called
        destroyNow()
    }

    override fun destroyNow() {
        service.stopSelf()
    }

    // Never throw an unsupported exception that causes this process to crash
    private inline fun <T> wrap(method: String, crossinline block: () -> T) = try {
        block()
    } catch (e: Exception) {
        Log.e(TAG, "$method failed", e)
        throw IllegalStateException("$method failed: ${e.stackTraceToString()}", e)
    }

    private fun persistUninstallActions() {
        val parcel = Parcel.obtain()
        try {
            parcel.writeParcelable(uninstallActions, 0)
            File(service.createDeviceProtectedStorageContext().filesDir, UNINSTALL_ACTIONS_FILE)
                .writeBytes(parcel.marshall())
        } finally {
            parcel.recycle()
        }
    }
}
