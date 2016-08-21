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

package net.protyposis.android.spectaculum;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by maguggen on 16.06.2014.
 */
public class LibraryHelper {

    private static Context sContext;

    static Context getContext() {
        return sContext;
    }

    static void setContext(Context context) {
        sContext = context;
    }

    public static String loadTextFromAsset(String file) {
        if(sContext == null) {
            throw new RuntimeException("context has not been set");
        }

        try {
            InputStream in = sContext.getAssets().open(file);

            InputStreamReader inReader = new InputStreamReader(in);
            BufferedReader inBReader = new BufferedReader(inReader);
            String line;
            StringBuilder text = new StringBuilder();

            while (( line = inBReader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }

            return text.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean isBetween(float check, float lowerBound, float upperBound) {
        return check >= lowerBound && check <= upperBound;
    }

    public static float clamp(float check, float lowerBound, float upperBound) {
        if(check < lowerBound) {
            return lowerBound;
        } else if(check > upperBound) {
            return upperBound;
        }
        return check;
    }
}
