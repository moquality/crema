# Crema

Set of utilities to extend the <a href="https://developer.android.com/training/testing/ui-testing/espresso-testing.html">espresso testing framework</a>

Word origin: [Crema](https://www.seattlecoffeegear.com/learn/coffee-101/articles/what-is-crema) is the foam that makes [espresso](https://en.wikipedia.org/?title=Espresso) delicious.

# How to get Crema

<b>STEP 1:</b> Crema is on jcenter. If you are already using it (Android default), then skip this step.  
If you are not using jcenter, add the following maven dependency to your <b>project's build.gradle</b>
```
allprojects {
    repositories {
        maven {
            url  "http://dl.bintray.com/checkdroid/com.checkdroid"
        }
    }
}
```

<b>STEP 2:</b> Next, you need to add a test compile dependency to your <b>app's build.gradle</b>
```
dependencies {
    // Testing-only dependencies
    androidTestCompile 'com.android.support.test:runner:0.3'
    androidTestCompile 'com.android.support.test:rules:0.3'
    androidTestCompile 'com.android.support.test.espresso:espresso-core:2.2'
    androidTestCompile 'com.checkdroid:crema:0.1'
}
```

<b>STEP 3:</b> Finally, add EspressoPlus.* matchers in your test.
```
import static com.checkdroid.crema.EspressoPlus.*;
```


# What does Crema get you

With Crema, we want to bring a library of new matchers and assertions to espresso.

As of now, crema has the following:
* <b>[XPath Matchers](https://github.com/checkdroid/crema/wiki/XPath-Matching): </b> Crema has 2 implementations of xPath Matchers
 * `withXPath(String xPath)`
 * `withXPath2(String xPath)`
* <b>Taking screenshots in tests: </b> Saves screenshots as `/sdcard/{filename}_{timestamp}.png`
 * `takeScreenshot()` - default file name is package name
 * `takeScreenshot(String filename)`
 * `takeScreenshot(boolean timestamp)` - timestamp is not added if false
 * `takeScreenshot(String file, boolean addTimestamp)`
* <i>More coming soon</i>..

# Support
* Got any issues/problems with crema?
* Any other matchers/assertious you would like?

Please create [new issues](https://github.com/checkdroid/crema/issues/new) on github to reach out to us!

Signup for updates here: http://checkdroid.com/updates
