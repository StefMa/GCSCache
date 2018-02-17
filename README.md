[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
<!-- (TODO: Add badge for plugin-portal -> https://img.shields.io/badge/plugin%20portal-v1.2-blue.svg) -->

# GCSCache
A custom Gradle Build Cache implementation which uses Google Cloud Storage to store the cacheable artifacts.

## Description
Haven't heard about [Gradle's Build Cache](https://docs.gradle.org/current/userguide/build_cache.html) yet?

In a nutshell the generated output of a [Task](https://guides.gradle.org/writing-gradle-tasks/) will cached (either local or remote) 
and can be reused the next time you build your software. Event on `clean` builds. 

The GCSCache will exactly do that for you. It will store the generated artifacts in your [Google Cloud Storage](https://cloud.google.com/storage/) 
which can be reused the next time you do a build.

## How to use it

### Apply the plugin
To apply the plugin just put the following to the **top** of your **`settings.gradle`**:
```gradle
buildscript {
  repositories {
    maven { url "https://plugins.gradle.org/m2/" } // TODO: Check if gradlePluginPortal() is possible
    // or
    jcenter()
  }
  dependencies {
    classpath "" // TODO: Define classpath
  }
}
```

### Configuration
Just go to your `settings.gradle` and use the following code:
```gradle
apply plugin: guru.stefma.gcs.cache.GCSCachePlugin // TODO: Use plugin in

buildCache {
    remote(guru.stefma.gcs.cache.GCSBuildCache) {
        push = true 
    }
}
```

This will enable the GCSCache and enable the push (so that it will upload the artifacts as well).

Other configuration options are:

| Property | Description | Mandatory | Default Value |
| ----------------- | ----------- | --------- | ------------- |
| `enabled` | If the remote Build Cache is enabled | ❌ | `true`
| `push` | If artifacts should be pushed/uploaded to the cloud | ❌ | `false`

### Authentication
Since the plugin needs to authenticate you with your Google Cloud you have to follow [these steps](https://cloud.google.com/storage/docs/reference/libraries#setting_up_authentication) to authenticate.
<!-- TODO Add better description for that -->

<!-- 
For more see also the Google Cloud section below 👇

## Google Cloud
-->

<!--
Other TODOS:
Read: https://discuss.gradle.org/t/feature-request-gradle-3-5-build-cache-and-amazon-s3/22065/4
See/READ: https://github.com/myniva/gradle-s3-build-cache
Do: Explain that it uses the free trier currently etc. pp (https://cloud.google.com/free/)
Do: Upload to gradle plugin portal
Do: Create a github release
-->
