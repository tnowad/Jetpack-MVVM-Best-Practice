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
import com.kunminx.puremusic.data.bean.LibraryInfo;
import com.kunminx.puremusic.data.config.Const;
import com.kunminx.puremusic.domain.request.InfoRequester;
import com.kunminx.puremusic.ui.page.adapter.DrawerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KunMinX at 19/10/29
 */
public class DrawerFragment extends BaseFragment {

    //TODO tip 1: Based on the "Single Responsibility Principle," ViewModel should be divided into state-ViewModel and result-ViewModel.
    // The state-ViewModel should only be responsible for managing, saving, and restoring the page's state, with its scope limited to the page itself.
    // The result-ViewModel should be responsible only for "message dispatch" and handling "trusted sources" with a scope defined by "data requests" or "cross-page communication" scenarios.
    // For a deeper understanding, refer to: https://xiaozhuanlan.com/topic/8204519736

    private DrawerStates mStates;
    private InfoRequester mInfoRequester;

    @Override
    protected void initViewModel() {
        mStates = getFragmentScopeViewModel(DrawerStates.class);
        mInfoRequester = getFragmentScopeViewModel(InfoRequester.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {

        //TODO tip 2: Strict mode for DataBinding:
        // Limit the DataBinding instance to the base page, making it not exposed to subclasses by default.
        // This approach resolves the issue of ensuring null safety for View instances, achieving consistent null safety between View instances and Jetpack Compose, based on functional programming principles.
        // DataBindingConfig provides the binding items for the base page's DataBinding in this context.

        // For more details, refer to: https://xiaozhuanlan.com/topic/9816742350 and https://xiaozhuanlan.com/topic/2356748910

        return new DataBindingConfig(R.layout.fragment_drawer, BR.vm, mStates)
            .addBindingParam(BR.click, new ClickProxy())
            .addBindingParam(BR.adapter, new DrawerAdapter(getContext()));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO tip 3: Receive data pushed from PublishSubject and respond to data changes in the callback.
        // This involves using BehaviorSubject (e.g., ObservableField) to notify the UI components to re-render their properties and maintain the last known state.

        // For more details, refer to: https://xiaozhuanlan.com/topic/6741932805

        mInfoRequester.getLibraryResult().observe(getViewLifecycleOwner(), dataResult -> {
            if (!dataResult.getResponseStatus().isSuccess()) return;
            if (dataResult.getResult() != null) mStates.list.set(dataResult.getResult());
        });

        mInfoRequester.requestLibraryInfo();
    }

    public class ClickProxy {
        public void logoClick() {
            openUrlInBrowser(Const.PROJECT_LINK);
        }
    }

    //TODO tip 5: Based on the Single Responsibility Principle, extract the capability of Jetpack ViewModel to "save and restore state" into StateHolder.
    // Use a subclass of ObservableField, called State, to function as a BehaviorSubject, serving as the "trusted data source" for bound UI components.
    // This allows the UI components to re-render their properties when the result data is pushed back via PublishSubject, keeping the last state intact.

    // For more details, refer to: https://xiaozhuanlan.com/topic/6741932805

    public static class DrawerStates extends StateHolder {
        public final State<List<LibraryInfo>> list = new State<>(new ArrayList<>());
    }
}

