# Crema

Set of utilities to extend the espresso testing framework

Word origin: [Crema](https://www.seattlecoffeegear.com/learn/coffee-101/articles/what-is-crema) is the foam that makes [espresso](https://en.wikipedia.org/?title=Espresso) delicious.

# How to get Crema

As of now, crema is not on jcenter (but it will soon be!). So, you need to add a Maven URL into your <b>project's build.gradle</b>
```
allprojects {
    repositories {
        jcenter()
        maven {
            url  "http://dl.bintray.com/checkdroid/com.checkdroid"
        }
    }
}
```

Next, you need to add a test compile dependency to your <b>app's build.gradle</b>
```
dependencies {
    // Testing-only dependencies
    androidTestCompile 'com.android.support.test:runner:0.3'
    androidTestCompile 'com.android.support.test:rules:0.3'
    androidTestCompile 'com.android.support.test.espresso:espresso-core:2.2'
    androidTestCompile 'com.checkdroid:crema:0.1'
}
```

Finally, add EspressoPlus.* matchers in your test.
```
import static com.checkdroid.crema.EspressoPlus.*;
```


# What does Crema get you

With Crema, we want to bring a library of new matchers and assertions to espresso.

As of now, crema has the following:
* <b>XPath Matcher: </b> `withXPath(String xPath)` and `withXPath2(String xPath)` are two implementations of xPath Matchers provided by crema.
* More coming <i>soon</i>..

# Support
* Got any issues/problems with crema?
* Any other matchers/assertious you would like?

Please create [new issues](https://github.com/checkdroid/crema/issues/new) on github to reach out to us!
