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

package com.kunminx.puremusic;

import android.os.Bundle;
import android.view.View;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.kunminx.architecture.ui.page.BaseActivity;
import com.kunminx.architecture.ui.page.DataBindingConfig;
import com.kunminx.architecture.ui.page.StateHolder;
import com.kunminx.architecture.ui.state.State;
import com.kunminx.puremusic.domain.event.Messages;
import com.kunminx.puremusic.domain.message.DrawerCoordinateManager;
import com.kunminx.puremusic.domain.message.PageMessenger;
import com.kunminx.puremusic.domain.proxy.PlayerManager;

/**
 * Create by KunMinX at 19/10/16
 */

public class MainActivity extends BaseActivity {

    //TODO tip 1: Based on the "Single Responsibility Principle", the ViewModel should be divided into state-ViewModel and result-ViewModel.
    // The state-ViewModel is responsible only for managing, saving, and restoring the state of the page. Its scope is limited to this page.
    // The result-ViewModel is responsible for "message dispatch" scenarios and "trusted sources" and its scope depends on the "data request" or
    // "cross-page communication" message dispatch range.

    // If this concept is unclear, refer to https://xiaozhuanlan.com/topic/8204519736

    private MainActivityStates mStates;
    private PageMessenger mMessenger;
    private boolean mIsListened = false;

    @Override
    protected void initViewModel() {
        mStates = getActivityScopeViewModel(MainActivityStates.class);
        mMessenger = getApplicationScopeViewModel(PageMessenger.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {

        //TODO tip 2: Strict DataBinding Mode:
        // Restrict the DataBinding instance to the base page and do not expose it to subclasses by default.
        // This approach thoroughly resolves the issue of View instance null safety consistency.
        // In this way, the View instance null safety will be on par with Jetpack Compose based on functional programming.
        // DataBindingConfig is used to provide binding items for the base page in this context.

        // If this concept is unclear, refer to https://xiaozhuanlan.com/topic/9816742350 and https://xiaozhuanlan.com/topic/2356748910

        return new DataBindingConfig(R.layout.activity_main, BR.vm, mStates)
            .addBindingParam(BR.listener, new ListenerHandler());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PlayerManager.getInstance().init(this);

        //TODO tip 6: Receive the push-back data from PublishSubject and respond to changes in data in the callback,
        // i.e., notify the View (using BehaviorSubject or ObservableField) to re-render its properties
        // and remember the last state.

        // If this concept is unclear, refer to https://xiaozhuanlan.com/topic/6741932805

        mMessenger.output(this, messages -> {
            switch (messages.eventId) {
                case Messages.EVENT_CLOSE_ACTIVITY_IF_ALLOWED:
                    NavController nav = Navigation.findNavController(this, R.id.main_fragment_host);
                    if (nav.getCurrentDestination() != null && nav.getCurrentDestination().getId() != R.id.mainFragment) {
                        nav.navigateUp();
                    } else if (Boolean.TRUE.equals(mStates.isDrawerOpened.get())) {

                        //TODO same as tip 3
                        mStates.openDrawer.set(false);
                    } else {
                        super.onBackPressed();
                    }
                    break;
                case Messages.EVENT_OPEN_DRAWER:

                    //TODO yes: Same as tip 2:
                    // Handle the open and close of the drawer in the drawerBindingAdapter to avoid View instance null safety issues
                    // because the horizontal layout might not have a drawerLayout.
                    // Using manual null checks here might easily cause null reference issues.

                    // Additionally, bind the "openDrawer" state to the drawerLayout using a "debounced" ObservableField subclass,
                    // primarily because ObservableField has "debounce" characteristics which are not suitable for this case.

                    mStates.openDrawer.set(true);

                    //TODO do not: (This could lead to hidden null safety issues)

                    /*if (mBinding.dl != null) {
                        if (aBoolean && !mBinding.dl.isDrawerOpen(GravityCompat.START)) {
                            mBinding.dl.openDrawer(GravityCompat.START);
                        } else {
                            mBinding.dl.closeDrawer(GravityCompat.START);
                        }
                    }*/
                    break;
            }
        });

        DrawerCoordinateManager.getInstance().isEnableSwipeDrawer().observe(this, aBoolean -> {

            //TODO yes: Same as tip 2
            mStates.allowDrawerOpen.set(aBoolean);

            //TODO do not: (This could lead to hidden null safety issues)

            /*if (mBinding.dl != null) {
                mBinding.dl.setDrawerLockMode(aBoolean
                        ? DrawerLayout.LOCK_MODE_UNLOCKED
                        : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }*/
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!mIsListened) {

            //TODO tip 3: This demonstrates sending a request to a "trusted source" to achieve reliable "lifecycle-safe and consistent message dispatch" notifications.

            // If this concept is unclear, refer to https://xiaozhuanlan.com/topic/0168753249
            // --------
            // At the same time, this emphasizes the "least knowledge principle",
            // Internal activity operations should be handled within the activity itself,
            // rather than trying to invoke or manipulate internal activity components from a fragment.
            // This is because the activity's handling might change in the future and can be applied to more fragments, not just this one.

            mMessenger.input(new Messages(Messages.EVENT_ADD_SLIDE_LISTENER));

            mIsListened = true;
        }
    }

    @Override
    public void onBackPressed() {

        //TODO same as tip 3

        mMessenger.input(new Messages(Messages.EVENT_CLOSE_SLIDE_PANEL_IF_EXPANDED));
    }

    public class ListenerHandler extends DrawerLayout.SimpleDrawerListener {
        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            mStates.isDrawerOpened.set(true);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
            mStates.isDrawerOpened.set(false);
            mStates.openDrawer.set(false);
        }
    }

    //TODO tip 5: Based on the Single Responsibility Principle, extract the state-saving and recovery capability of Jetpack ViewModel as a StateHolder,
    // and use the enhanced ObservableField subclass "State" to act as a BehaviorSubject,
    // serving as the "trusted data source" for the bound View components.
    // When receiving results from PublishSubject, it will respond to the changes and re-render the View properties, keeping the last state.

    // If this concept is unclear, refer to https://xiaozhuanlan.com/topic/6741932805

    public static class MainActivityStates extends StateHolder {

        public final State<Boolean> isDrawerOpened = new State<>(false);

        public final State<Boolean> openDrawer = new State<>(false);

        public final State<Boolean> allowDrawerOpen = new State<>(true);

    }
}

