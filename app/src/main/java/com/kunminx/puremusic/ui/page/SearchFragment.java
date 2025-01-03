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

package com.kunminx.puremusic.ui.page;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kunminx.architecture.ui.page.BaseFragment;
import com.kunminx.architecture.ui.page.DataBindingConfig;
import com.kunminx.architecture.ui.page.StateHolder;
import com.kunminx.architecture.ui.state.State;
import com.kunminx.puremusic.BR;
import com.kunminx.puremusic.R;
import com.kunminx.puremusic.data.bean.DownloadState;
import com.kunminx.puremusic.data.config.Const;
import com.kunminx.puremusic.domain.event.DownloadEvent;
import com.kunminx.puremusic.domain.message.DrawerCoordinateManager;
import com.kunminx.puremusic.domain.request.DownloadRequester;

/**
 * Created by KunMinX on 19/10/29
 */
public class SearchFragment extends BaseFragment {

    // TODO tip 1: According to the "Single Responsibility Principle," the ViewModel should be divided into state-ViewModel and result-ViewModel.
    // The state-ViewModel is only responsible for managing, saving, and restoring the state of this page, its scope is limited to this page.
    // The result-ViewModel is only responsible for "message dispatching" and its scope depends on "data requests" or "cross-page communication" message distribution.

    // For more understanding, refer to https://xiaozhuanlan.com/topic/8204519736

    private SearchStates mStates;
    private DownloadRequester mDownloadRequester;
    private DownloadRequester mGlobalDownloadRequester;

    @Override
    protected void initViewModel() {
        mStates = getFragmentScopeViewModel(SearchStates.class);
        mDownloadRequester = getFragmentScopeViewModel(DownloadRequester.class);
        mGlobalDownloadRequester = getActivityScopeViewModel(DownloadRequester.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        // TODO tip 2: Strict DataBinding mode:
        // Limit the DataBinding instance to the base page and do not expose it to subclasses by default.
        // This way, the View instance null safety issue is solved completely.
        // Thus, the null safety of View instances will be as reliable as Jetpack Compose, which follows functional programming principles.

        // For more understanding, refer to https://xiaozhuanlan.com/topic/9816742350 and https://xiaozhuanlan.com/topic/2356748910

        return new DataBindingConfig(R.layout.fragment_search, BR.vm, mStates)
            .addBindingParam(BR.click, new ClickProxy());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLifecycle().addObserver(DrawerCoordinateManager.getInstance());

        // TODO tip 3: Bind business logic that can be paused independently in the lifecycle to UseCase and observe it.
        getLifecycle().addObserver(mDownloadRequester);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO tip 8: Here, the MVI-Dispatcher input-output interface is used for data request responses.

        // For more understanding, refer to "Domain Layer Design" breakdown at https://juejin.cn/post/7117498113983512589

        mDownloadRequester.output(this, downloadEvent -> {
            if (downloadEvent.eventId == DownloadEvent.EVENT_DOWNLOAD) {
                DownloadState state = downloadEvent.downloadState;
                mStates.progress_cancelable.set(state.progress);
                mStates.enableDownload.set(state.progress == 100 || state.progress == 0);
            }
        });

        // TODO tip 9: This demonstrates "the same Result-ViewModel class instantiated in different scopes leading to different results."

        mGlobalDownloadRequester.output(this, downloadEvent -> {
            if (downloadEvent.eventId == DownloadEvent.EVENT_DOWNLOAD_GLOBAL) {
                DownloadState state = downloadEvent.downloadState;
                mStates.progress.set(state.progress);
                mStates.enableGlobalDownload.set(state.progress == 100 || state.progress == 0);
            }
        });
    }

    // TODO tip 4: Using DataBinding here avoids the null safety consistency issues of setOnClickListener when the View instance is null.

    // That is, bind the view only when the view exists; if it doesn't, there's no binding, so the View instance will never cause null safety issues.
    // For more understanding, refer to https://xiaozhuanlan.com/topic/9816742350

    public class ClickProxy {

        public void back() {
            nav().navigateUp();
        }

        public void testNav() {
            openUrlInBrowser(Const.COLUMN_LINK);
        }

        public void subscribe() {
            openUrlInBrowser(Const.COLUMN_LINK);
        }

        // TODO tip: Same as tip 8

        public void testDownload() {
            mGlobalDownloadRequester.input(new DownloadEvent(DownloadEvent.EVENT_DOWNLOAD_GLOBAL));
        }

        // TODO tip 5: Execute downloadable tasks that follow the lifecycle's end in the UseCase.

        public void testLifecycleDownload() {
            mDownloadRequester.input(new DownloadEvent(DownloadEvent.EVENT_DOWNLOAD));
        }
    }

    // TODO tip 6: Based on the Single Responsibility Principle, extract the ability to "save and restore state" of Jetpack ViewModel into a StateHolder.
    // Use a subclass of State, an improved version of ObservableField, to function as the BehaviorSubject.
    // It serves as the "trusted data source" for the bound controls, triggering re-rendering when data changes, and ensuring that the last state is maintained.

    // For more understanding, refer to https://xiaozhuanlan.com/topic/6741932805

    public static class SearchStates extends StateHolder {

        public final State<Integer> progress = new State<>(1);

        public final State<Integer> progress_cancelable = new State<>(1);

        public final State<Boolean> enableDownload = new State<>(true);

        public final State<Boolean> enableGlobalDownload = new State<>(true);
    }
}

