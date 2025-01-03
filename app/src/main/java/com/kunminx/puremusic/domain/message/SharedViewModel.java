/*
 * Copyright 2018-present KunMinX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kunminx.puremusic.domain.message;

import androidx.lifecycle.ViewModel;

import com.kunminx.architecture.domain.message.MutableResult;
import com.kunminx.architecture.domain.message.Result;

/**
 * TODO Tip: This class is used for cross-page communication,
 * This class has been replaced by the PageMessenger class. For details, refer to the PageMessenger class description.
 * <p>
 * Created by KunMinX on 19/10/16
 */
@Deprecated
public class SharedViewModel extends ViewModel {

    //TODO Tip 2: This demonstrates how UnPeekLiveData combined with SharedViewModel can achieve "lifecycle-safe, reliable, consistent" message distribution.

    //TODO Tip 3: To make it easier to understand, the original UnPeekLiveData has been renamed to MutableResult;
    // ProtectedUnPeekLiveData has been renamed to Result;

    private final MutableResult<Boolean> toCloseSlidePanelIfExpanded = new MutableResult<>();

    private final MutableResult<Boolean> toCloseActivityIfAllowed = new MutableResult<>();

    private final MutableResult<Boolean> toOpenOrCloseDrawer = new MutableResult<>();

    //TODO Tip 4: MutableResult can be configured via constructor.

    private final MutableResult<Boolean> toAddSlideListener =
        new MutableResult.Builder<Boolean>().setAllowNullValue(false).create();

    public Result<Boolean> isToAddSlideListener() {
        return toAddSlideListener;
    }

    public Result<Boolean> isToCloseSlidePanelIfExpanded() {
        return toCloseSlidePanelIfExpanded;
    }

    public Result<Boolean> isToCloseActivityIfAllowed() {
        return toCloseActivityIfAllowed;
    }

    public Result<Boolean> isToOpenOrCloseDrawer() {
        return toOpenOrCloseDrawer;
    }

    public void requestToCloseActivityIfAllowed(boolean allow) {
        toCloseActivityIfAllowed.setValue(allow);
    }

    public void requestToOpenOrCloseDrawer(boolean open) {
        toOpenOrCloseDrawer.setValue(open);
    }

    public void requestToCloseSlidePanelIfExpanded(boolean close) {
        toCloseSlidePanelIfExpanded.setValue(close);
    }

    public void requestToAddSlideListener(boolean add) {
        toAddSlideListener.setValue(add);
    }
}

