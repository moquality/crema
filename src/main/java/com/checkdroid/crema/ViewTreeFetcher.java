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

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.core.deps.guava.base.Function;
import android.support.test.espresso.core.deps.guava.base.Joiner;
import android.support.test.espresso.core.deps.guava.base.Strings;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.espresso.util.TreeIterables.ViewAndDistance;
import android.view.View;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import static android.support.test.espresso.util.TreeIterables.depthFirstViewTraversalWithDistance;

class ViewTreeFetcher implements ViewAction {

    String tree;

    @Override
    public Matcher<View> getConstraints() {
        return Matchers.allOf(ViewMatchers.isRoot());
    }

    @Override
    public String getDescription() {
        return "fetch view tree";
    }

    @Override
    public void perform(UiController uiController, View view) {
        StringBuilder sb = new StringBuilder();
        Joiner.on("\n").appendTo(sb, Iterables.transform(
                depthFirstViewTraversalWithDistance(view), new Function<ViewAndDistance, String>() {
                    @Override
                    public String apply(ViewAndDistance viewAndDistance) {
                        String formatString = "+%s%s ";
                        formatString += "\n|";

                        return String.format(formatString,
                                Strings.padStart(">", viewAndDistance.getDistanceFromRoot() + 1, '-'),
                                HumanReadables.describe(viewAndDistance.getView()));
                    }
                }));
        tree = sb.toString();
    }

    public String getViewTree() {
        return tree;
    }

}
