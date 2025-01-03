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

package com.kunminx.puremusic.domain.request;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.kunminx.architecture.data.response.DataResult;
import com.kunminx.architecture.data.response.ResponseStatus;
import com.kunminx.architecture.data.response.ResultSource;
import com.kunminx.architecture.domain.message.MutableResult;
import com.kunminx.architecture.domain.message.Result;
import com.kunminx.architecture.domain.request.Requester;
import com.kunminx.puremusic.data.bean.User;
import com.kunminx.puremusic.data.repository.DataRepository;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * User Account Request
 * <p>
 * TODO Tip 1: Separate UI from business logic and ensure data always flows from producer to consumer
 * <p>
 * The difference between UI logic and business logic is that the former consumes data, while the latter produces data.
 * The "domain layer component" is the producer of data, and its responsibility should be limited to "request scheduling and result distribution".
 * <p>
 * In other words, the "domain layer component" should only focus on data generation, not on data usage.
 * The logic to change UI states should be written in the presentation layer (e.g., observer callbacks).
 * When upgrading to Jetpack Compose, this remains the same.
 * <p>
 * Example usage:
 * Activity {
 * onCreate(){
 * vm.livedata.observe { result->
 * panel.visible(result.show ? VISIBLE : GONE)
 * tvTitle.setText(result.title)
 * tvContent.setText(result.content)
 * }
 * }
 * <p>
 * For a deeper understanding, refer to the article on "Jetpack MVVM Layered Architecture":
 * https://xiaozhuanlan.com/topic/6741932805
 * <p>
 * Created by KunMinX on 20/04/26
 */
public class AccountRequester extends Requester implements DefaultLifecycleObserver {

    //TODO Tip 3: Allow accountRequest to observe page lifecycle,
    // so that when the page exits and the login request has not completed due to network delay,
    // it can notify the data layer to cancel the request, avoiding unnecessary resource consumption and potential issues.

    private final MutableResult<DataResult<String>> tokenResult = new MutableResult<>();

    //TODO Tip 4: Follow "reactive programming" principles and maintain "unidirectional data flow".
    // MutableResult should only be used within the "authentication center" and only expose immutable Result to the UI layer.
    // Through "separation of read and write", data flows from "domain layer" to "presentation layer" in one direction.

    // For more details, refer to the article "Mastering LiveData for Reliable Message Authentication Mechanisms":
    // https://xiaozhuanlan.com/topic/6017825943

    public Result<DataResult<String>> getTokenResult() {
        return tokenResult;
    }

    //TODO Tip 5: Simulate a cancellable login request:
    // By observing the page lifecycle in accountRequest,
    // when the page is about to exit and the login request is not yet completed due to network delay,
    // the data layer is notified to cancel the request, avoiding unnecessary resource consumption and issues.

    private Disposable mDisposable;

    //TODO Tip 6: As a producer of data, the requester's responsibility should be limited to "request scheduling and result distribution".
    // In other words, this class should only focus on data generation and feedback, not on data usage.
    // UI state changes should be handled at the presentation layer (e.g., with Jetpack Compose).

    public void requestLogin(User user) {
        DataRepository.getInstance().login(user).subscribe(new Observer<DataResult<String>>() {
            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
            }
            @Override
            public void onNext(DataResult<String> dataResult) {
                tokenResult.postValue(dataResult);
            }
            @Override
            public void onError(Throwable e) {
                tokenResult.postValue(new DataResult<>(null,
                    new ResponseStatus(e.getMessage(), false, ResultSource.NETWORK)));
            }
            @Override
            public void onComplete() {
                mDisposable = null;
            }
        });
    }

    public void cancelLogin() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    //TODO Tip 7: Allow accountRequest to observe page lifecycle,
    // so that when the page exits and the login request is still pending, it can notify the data layer to cancel the request,
    // avoiding resource waste and potential issues.

    // For more on the importance of Lifecycle components, refer to the article "Understanding Jetpack Lifecycle":
    // https://xiaozhuanlan.com/topic/3684721950

    @Override
    public void onStop(@NonNull @NotNull LifecycleOwner owner) {
        cancelLogin();
    }
}

