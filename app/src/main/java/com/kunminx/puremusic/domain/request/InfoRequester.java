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
import com.kunminx.puremusic.data.bean.LibraryInfo;
import com.kunminx.puremusic.data.repository.DataRepository;

import java.util.List;

/**
 * Information List Request
 * <p>
 * TODO Tip 1: Separate UI from business logic, ensuring that data always flows from producer to consumer.
 * <p>
 * The key difference between UI logic and business logic is that the former is the consumer of data, while the latter is the producer.
 * The "domain layer component" serves as the data producer, and its responsibility should be limited to "request scheduling and result distribution".
 * <p>
 * In other words, the "domain layer component" should only focus on data generation, not on data usage.
 * Logic that modifies the UI state should only be written in the presentation layer, in the Observer callback responding to data changes.
 * When upgrading to Jetpack Compose, this principle remains the same.
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
 * TODO Tip 2: Requesters are usually divided by business logic.
 * There can be multiple Requester classes in a project, and each page can hold multiple different Requester instances based on business needs.
 * Through PublishSubject, one-time messages are pushed back and branched in the presentation layer Observer.
 * For Events, execute them directly; for States, use BehaviorSubject to notify the view and carry the state.
 * <p>
 * Activity {
 * onCreate(){
 * request.observe {result ->
 * is Event ? -> execute one time
 * is State ? -> BehaviorSubject setValue and notify
 * }
 * }
 * <p>
 * For a deeper understanding, refer to the article on "Jetpack MVVM Layered Architecture":
 * https://xiaozhuanlan.com/topic/6741932805
 * <p>
 * Created by KunMinX on 19/11/2
 */
public class InfoRequester extends Requester {

    private final MutableResult<DataResult<List<LibraryInfo>>> mLibraryResult = new MutableResult<>();

    //TODO Tip 4: Follow "reactive programming" and implement "one-way data flow" development.
    // MutableResult should only be used internally in the "authentication center" and expose only immutable Result to the UI layer.
    // Implement one-way data flow from the "domain layer" to the "presentation layer" through "read-write separation".

    // For a deeper understanding, refer to the article on "Mastering LiveData Essence, Enjoying Reliable Message Authentication":
    // https://xiaozhuanlan.com/topic/6017825943

    public Result<DataResult<List<LibraryInfo>>> getLibraryResult() {
        return mLibraryResult;
    }

    //TODO Tip 5: Requester, as the data producer, should only focus on "request scheduling and result distribution".
    //
    // In other words, it should focus solely on data generation and pushback, not on data usage.
    // Logic for changing the UI state should only be written in the presentation layer, such as with Jetpack Compose.

    @SuppressLint("CheckResult")
    public void requestLibraryInfo() {
        if (mLibraryResult.getValue() == null)
            DataRepository.getInstance().getLibraryInfo().subscribe(mLibraryResult::setValue);
    }
}

