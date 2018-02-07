## A Tester project for the Gradle GSCache Plugin

The project contains a `TestTask` which can be cached. 
The Task writes a given text (can be set with a *property* called `prop`) to a file (`build/outputfile.txt`).
The `tester` task is a registered instance of the `TestTask`.

The project also includes a `clean` task which will remove the `build/outputfile.txt` file.

By default the `tester` task depends on `clean`. Which means it will **never be up-to-date**.
But since we have enabled the *build-cache* inside the `gradle.properties` it will always try to use the cached artifact.

### Testing
The task can be executed like the following:
```
./gradlew tester // Will use a Random Integer and save it in the file

./gradlew tester -Pprop="Prop" // Will save "Prop" in the file
``` 

For you can use `--console=plain` on the task execution to see which tasks get executed.

To see the debug output from the build-cache you can use `-d | grep "guru"`.
A full testing task should look like:
```
./gradlew tester --console=plain -Pprop="hi" -d | grep guru
```

The output will look like:
```
Stefans-MacBook-Pro-3:consumer stefan$ ./gradlew tester --console=plain -Pprop="hi" -d | grep guru
...
21:13:42.374 [DEBUG] [guru.stefma.gcs.cache.GCSCachePlugin] Plugin 'GCSBuildCache' applied and 'GCSCacheServiceFactory' registered as buildCacheService
...
21:13:42.375 [DEBUG] [guru.stefma.gcs.cache.GCSCacheServiceFactory] Create build cache service 'GCSCacheService'
21:13:42.375 [DEBUG] [guru.stefma.gcs.cache.GCSCacheService] buildCacheService 'GCSCacheService' created
...
21:13:42.420 [DEBUG] [guru.stefma.gcs.cache.GCSCacheService] Try to load for given key: '8abef90fe288aa382d48e84e704d6cf1'
21:13:42.425 [DEBUG] [guru.stefma.gcs.cache.GCSCacheService] Close all resources for the given GCSCacheService
``` 