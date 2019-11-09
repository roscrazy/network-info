# network-info
An Android library provide a simple implementation to provide the network connection status by rxjava stream

## Usage

### Add as dependency
This library is not yet released in Maven Central, until then you can add as a library module or use JitPack.io

add remote maven url

```groovy

    repositories {
        maven {
            url "https://jitpack.io"
        }
    }
```

then add a library dependency. **Remember** to check for latest release [here](https://github.com/roscrazy/network-info/releases)

```groovy
    dependencies {
        implementation 'com.github.roscrazy:network-info:1.0.0'
    }
```

### Add permission
Requires internet permission (obviously...)
```xml
  <uses-permission android:name="android.permission.INTERNET" />
  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```