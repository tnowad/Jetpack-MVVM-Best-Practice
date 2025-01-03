/*
 *
 *  * Copyright 2018-present KunMinX
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.kunminx.puremusic.domain.message;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.kunminx.architecture.domain.message.MutableResult;
import com.kunminx.architecture.domain.message.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO Tip 1: Achieve consistency for "whether the drawer side slide is enabled" through Lifecycle,
 * <p>
 * Each "view controller that needs to register and observe the lifecycle" does not need to manually write unbinding operations inside themselves.
 * If this is unclear, refer to "Restoring a Real Jetpack Lifecycle" here:
 * https://xiaozhuanlan.com/topic/3684721950
 * <p>
 * TODO Tip 2: Meanwhile, as a singleton used for "cross-page communication", this class also takes on the role of a "trusted source",
 * all requests related to Drawer state coordination are handled by this singleton and uniformly distributed to all subscriber pages.
 * <p>
 * If this is unclear, refer to "Mastering the Essence of LiveData and Enjoying a Reliable Message Authentication Mechanism" analysis here:
 * https://xiaozhuanlan.com/topic/6017825943
 * <p>
 * <p>
 * Created by KunMinX on 19/11/3
 */
public class DrawerCoordinateManager implements DefaultLifecycleObserver {

    private static final DrawerCoordinateManager S_HELPER = new DrawerCoordinateManager();

    private DrawerCoordinateManager() {
    }

    public static DrawerCoordinateManager getInstance() {
        return S_HELPER;
    }

    private final List<String> tagOfSecondaryPages = new ArrayList<>();

    private boolean isNoneSecondaryPage() {
        return tagOfSecondaryPages.size() == 0;
    }

    private final MutableResult<Boolean> enableSwipeDrawer = new MutableResult<>();

    public Result<Boolean> isEnableSwipeDrawer() {
        return enableSwipeDrawer;
    }

    public void requestToUpdateDrawerMode(boolean pageOpened, String pageName) {
        if (pageOpened) {
            tagOfSecondaryPages.add(pageName);
        } else {
            tagOfSecondaryPages.remove(pageName);
        }
        enableSwipeDrawer.setValue(isNoneSecondaryPage());
    }

    //TODO Tip 3: Make NetworkStateManager observe the page lifecycle,
    // so that when entering or leaving the target page, it automatically registers and handles disabling and enabling the drawer,
    // avoiding a series of unpredictable issues.

    // For a detailed explanation of the significance of the Lifecycle component, refer to "Restoring a Real Jetpack Lifecycle" analysis
    // https://xiaozhuanlan.com/topic/3684721950

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {

        tagOfSecondaryPages.add(owner.getClass().getSimpleName());

        enableSwipeDrawer.setValue(isNoneSecondaryPage());

    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {

        tagOfSecondaryPages.remove(owner.getClass().getSimpleName());

        enableSwipeDrawer.setValue(isNoneSecondaryPage());

    }

}
