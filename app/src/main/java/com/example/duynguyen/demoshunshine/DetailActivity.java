/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.example.duynguyen.demoshunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frament_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(DetailActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.layout_frament_detail_activity, container, false);
            intent = getActivity().getIntent();
            if ((intent != null) && intent.hasExtra(Intent.EXTRA_TEXT)) {
                mForecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
                TextView textView = (TextView) rootView.findViewById(R.id.detailTextView);
                textView.setText(mForecastStr);
            }
            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.dtailfragment, menu);

            MenuItem item = menu.findItem(R.id.action_share);

            ShareActionProvider provider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

            if (provider != null) {
                provider.setShareIntent(createShareForecastIntent());
            } else {
                Log.e(LOG_TAG, "Share Action Provider  is null?");
            }
        }

        //        @Override
//        public void onActivityResult(int requestCode, int resultCode, Intent data) {
//            super.onActivityResult(requestCode, resultCode, data);
//            this.intent = data;
//        }

        private static final String LOG_TAG = DetailActivity.class.getSimpleName();
        private static final String FORECAST_SHARE_HAGHTAG = " #SunshineApp";

        private String mForecastStr;

        private Intent intent;

        private Intent createShareForecastIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HAGHTAG);
            return shareIntent;
        }
    }

    private ShareActionProvider provider;
}