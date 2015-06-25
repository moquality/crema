/*
 * Copyright (c) 2015. CheckDroid at Georgia Tech
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.checkdroid.crema;

import android.view.View;
import java.util.Comparator;

public class EspressoViewComparator implements Comparator<View> {

    @Override
    public int compare(View v1, View v2) {
        int[] l1 = {0,0};
        int[] l2 = {0,0};
        v1.getLocationOnScreen(l1);
        v2.getLocationOnScreen(l2);
        if (l1[0] == l2[0]){
            return (l1[1] == l2[1])? 0 : (l1[1] < l2[1])? -1 : 1;
        } else if (l1[0] < l2[0]) {
            return (l1[1] == l2[1])? -1 : (l1[1] < l2[1])? -1 : 1;
        } else {
            return (l1[1] == l2[1])? 1 : (l1[1] < l2[1])? -1 : 1;
        }
    }
}
