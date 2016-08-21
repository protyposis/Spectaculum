/*
 * Copyright 2014 Mario Guggenberger <mg@protyposis.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.protyposis.android.spectaculum.gles;

import android.os.SystemClock;
import android.util.Log;

/**
 * Created by maguggen on 10.07.2014.
 */
public class FrameRateCalculator {

    private static final String TAG = FrameRateCalculator.class.getSimpleName();

    private long[] mDurations;
    private long mDurationSum;
    private int mIndex;

    private long mLastFrameTime;

    public FrameRateCalculator(int movingAverageWindowSize) {
        mDurations = new long[movingAverageWindowSize];
        mLastFrameTime = SystemClock.elapsedRealtime();
    }

    public void frame() {
        long currentTime = SystemClock.elapsedRealtime();
        long duration = currentTime - mLastFrameTime;

        // go to next slot
        mIndex = (mIndex + 1) % mDurations.length;

        // the slot now contains the oldest duration which we subtract from the moving sum
        mDurationSum -= mDurations[mIndex];
        // then we add the current duration to the moving sum, and insert the duration also for later removal reference
        mDurationSum += duration;
        mDurations[mIndex] = duration;

        double avgFrameRate = 1000d / ((double) mDurationSum / mDurations.length);
        double currentFrameRate = 1000d / duration;

        Log.d(TAG, String.format("avg fps %.2f current fps %.2f", avgFrameRate, currentFrameRate));

        mLastFrameTime = currentTime;
    }
}
