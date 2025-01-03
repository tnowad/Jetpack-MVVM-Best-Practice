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

package com.kunminx.puremusic.domain.usecase;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.kunminx.architecture.data.response.DataResult;
import com.kunminx.architecture.domain.usecase.UseCase;
import com.kunminx.puremusic.data.bean.DownloadState;

/**
 * UseCase Example, implements the LifeCycle interface, dedicated to services with "stop" requirements.
 * <p>
 * TODO tip:
 * Instead of writing two methods separately in the data layer for "download,"
 * I follow the Open/Closed Principle and insert a UseCase between the ViewModel and the data layer
 * to specifically handle situations where the process can be stopped.
 * Besides the Open/Closed Principle, using UseCase also helps avoid memory leaks.
 * For a detailed explanation, see the 15th comment in the discussion on https://xiaozhuanlan.com/topic/6257931840
 * and the analysis of "This is a self-driving guide to 'Architecture Patterns'"
 * https://xiaozhuanlan.com/topic/8204519736
 * <p>
 * <p>
 * Now, the process is handled within the MVI-Dispatcher. For implementation details, refer to the DownloadRequest.
 * <p>
 * <p>
 * Created by KunMinX at 19/11/25
 */
@Deprecated
public class CanBeStoppedUseCase extends UseCase<CanBeStoppedUseCase.RequestValues,
    CanBeStoppedUseCase.ResponseValue> implements DefaultLifecycleObserver {

//    private final DownloadState downloadState = new DownloadState();

    //TODO tip: Make CanBeStoppedUseCase observe the page lifecycle.
    // This allows the data layer to be notified to cancel the request in time when the page is about to exit
    // and the download request is not yet completed, avoiding resource waste and unpredictable issues.

    // For a deeper understanding of the role of Lifecycle components, refer to the "Restoring a Real Jetpack Lifecycle"
    // analysis: https://xiaozhuanlan.com/topic/3684721950

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        if (getRequestValues() != null) {
//            downloadState.isForgive = true;
//            downloadState.file = null;
//            downloadState.progress = 0;
//            getUseCaseCallback().onError();
        }
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {

        // Access data layer resources to handle business logic with stoppable characteristics

//        DataRepository.getInstance().downloadFile(downloadState, dataResult -> {
//            getUseCaseCallback().onSuccess(new ResponseValue(dataResult));
//        });
    }

    public static final class RequestValues implements UseCase.RequestValues {

    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final DataResult<DownloadState> mDataResult;

        public ResponseValue(DataResult<DownloadState> dataResult) {
            mDataResult = dataResult;
        }

        public DataResult<DownloadState> getDataResult() {
            return mDataResult;
        }
    }
}

