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

package dev.kdrag0n.dyntheme.service.privileged;

import android.content.om.OverlayIdentifier;
import android.content.om.OverlayInfo;
import android.content.om.OverlayManagerTransaction;
import android.os.UserHandle;

import dev.kdrag0n.dyntheme.service.privileged.sys.UninstallActions;

interface IPrivilegedService {
    int getServiceVersion() = 0;

    // Overlays
    OverlayInfo getOverlayInfo(in OverlayIdentifier identifier, in UserHandle user) = 100;
    List<OverlayInfo> getOverlayInfosForTarget(String targetPackageName, in UserHandle user) = 101;
    void setOverlayEnabled(String packageName, boolean enable, in UserHandle user) = 102;
    void setOverlayEnabledExclusiveInCategory(String packageName, in UserHandle user) = 103;
    void commitOverlayTransaction(in OverlayManagerTransaction txn) = 104;

    // Management
    void setUninstallActions(in UninstallActions actions) = 200;

    // Props
    void setBootanimProp(String key, String value) = 300;

    // Settings
    boolean setSecureSetting(String key, String value) = 400;

    // Misc
    void destroy() = 16777114;
    void destroyNow() = 500;
}
