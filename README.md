# Amazon Ads Cordova Plugin
Add support for Amazon Mobile Ads to your cordova based mobile apps.

## How do I install it? ##

* If you're like me and using [CLI](http://cordova.apache.org/):
```
cordova plugin add https://github.com/blakgeek/cordova-plugin-amazonmobileads
```

or

```
phonegap local plugin add https://github.com/blakgeek/cordova-plugin-amazonmobileads
```

## WARNING: iOS Cordova Registry
****Installing this plugin directly from Cordova Registry results in Xcode using a broken `AmazonAd.framework`, this is because the current publish procedure to NPM breaks symlinks [CB-6092](https://issues.apache.org/jira/browse/CB-6092). Please install the plugin through through the github url or re-add the `AmazonAd.framework` manually.****

## How do I use it? ##
TODO: Add documentation

In the meantime take a the demo project [the demo project](https://github.com/blakgeek/cordova-plugin-amazonmobileads-demo)