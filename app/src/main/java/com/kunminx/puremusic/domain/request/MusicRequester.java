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

import android.annotation.SuppressLint;

import com.kunminx.architecture.data.response.DataResult;
import com.kunminx.architecture.domain.message.MutableResult;
import com.kunminx.architecture.domain.message.Result;
import com.kunminx.architecture.domain.request.Requester;
import com.kunminx.puremusic.data.bean.TestAlbum;
import com.kunminx.puremusic.data.repository.DataRepository;

/**
 * Music Resource Request
 * <p>
 * TODO tip 1: Separate UI from business logic, ensure data always flows from producer to consumer
 * <p>
 * The essential difference between UI logic and business logic is that the former is a consumer of data,
 * and the latter is a producer of data. The "domain layer components" as data producers should only focus
 * on "request dispatching and result distribution."
 * <p>
 * In other words, the "domain layer components" should only focus on generating data and not on its usage.
 * Logic to change UI state should only be written in the view layer (e.g., in observer callbacks) in response to
 * data changes. When upgrading to Jetpack Compose, this remains true.
 * <p>
 * Activity {
 * onCreate(){
 * vm.livedata.observe { result->
 * panel.visible(result.show ? VISIBLE : GONE)
 * tvTitle.setText(result.title)
 * tvContent.setText(result.content)
 * }
 * }
 * <p>
 * TODO tip 2: Requesters are usually divided by business logic
 * In a project, there are typically multiple Requester classes. Each page may hold different Requester instances
 * according to its business needs. These instances push one-time messages via PublishSubject, and the observer
 * in the view layer can further split the logic: execute events directly and use BehaviorSubject to notify views
 * for state changes.
 * <p>
 * Activity {
 * onCreate(){
 * request.observe {result ->
 * is Event ? -> execute once
 * is State ? -> set value and notify BehaviorSubject
 * }
 * }
 * <p>
 * If this explanation is unclear, please refer to the "Jetpack MVVM Layered Design" analysis:
 * https://xiaozhuanlan.com/topic/6741932805
 * <p>
 * <p>
 * Created by KunMinX at 19/10/29
 */
public class MusicRequester extends Requester {

    private final MutableResult<DataResult<TestAlbum>> mFreeMusicsResult = new MutableResult<>();

    //TODO tip 4: Follow "Reactive Programming" principles and implement "Unidirectional Data Flow"
    // MutableResult should only be used internally in the "Authentication Center," exposing immutable Result
    // to the UI layer. This enforces "read-write separation," ensuring that data flows from the domain layer
    // to the view layer in a unidirectional manner.

    // If this explanation is unclear, refer to the "Mastering LiveData and Reliable Message Authentication"
    // analysis: https://xiaozhuanlan.com/topic/6017825943

    public Result<DataResult<TestAlbum>> getFreeMusicsResult() {
        return mFreeMusicsResult;
    }

    //TODO tip 5: The requester's role is solely for "request dispatching and result distribution"
    //
    // In other words, here we focus on data generation and forwarding, not its usage.
    // Logic to change UI state should only be written in the view layer (e.g., with Jetpack Compose).

    @SuppressLint("CheckResult")
    public void requestFreeMusics() {
        DataRepository.getInstance().getFreeMusic().subscribe(mFreeMusicsResult::setValue);
    }
}

