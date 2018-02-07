package guru.stefma.gcs.cache

import com.google.cloud.storage.*
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logging
import org.gradle.caching.*
import org.gradle.caching.configuration.AbstractBuildCache
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * A [Settings] plugin which could be used to use
 * [**Google Cloud Storage**](https://cloud.google.com/storage/) as a
 * [build cache](https://docs.gradle.org/current/userguide/build_cache.html) backend.
 */
open class GCSCachePlugin : Plugin<Settings> {

    private val logger = Logging.getLogger(GCSCachePlugin::class.java)

    override fun apply(settings: Settings) {
        val buildCache = settings.buildCache
        buildCache.registerBuildCacheService(GCSBuildCache::class.java, GCSCacheServiceFactory::class.java)
        logger.log(LogLevel.DEBUG, "Plugin '${GCSBuildCache::class.java.simpleName}' applied and " +
                "'${GCSCacheServiceFactory::class.java.simpleName}' registered as buildCacheService")
    }

}

open class GCSCacheServiceFactory : BuildCacheServiceFactory<GCSBuildCache> {

    private val logger = Logging.getLogger(GCSCacheServiceFactory::class.java)

    /**
     * Creates the [GCSCacheService] and setup all properties
     */
    override fun createBuildCacheService(buildCache: GCSBuildCache, describer: BuildCacheServiceFactory.Describer): BuildCacheService {
        logger.log(LogLevel.DEBUG, "Create buildCacheService '${GCSCacheService::class.java.simpleName}'")
        // TODO: Configure the GCS stuff
        val (storage, bucket) = createGCStorage()
        return GCSCacheService(storage, bucket)
    }

    private fun createGCStorage(): Pair<Storage, Bucket> {
        val storage = StorageOptions.getDefaultInstance().service
        val bucket = storage.get("gradle-cache") ?: storage.create(BucketInfo
                .newBuilder("gradle-cache")
                .setStorageClass(StorageClass.REGIONAL)
                .setLocation("us-east1")
                .build())
        return Pair(storage, bucket)
    }

}

class GCSCacheService(
        private val storage: Storage,
        private val bucket: Bucket
) : BuildCacheService {

    private val logger = Logging.getLogger(GCSCacheService::class.java)

    @Throws(BuildCacheException::class)
    override fun store(key: BuildCacheKey, writer: BuildCacheEntryWriter) {
        logger.log(LogLevel.DEBUG, "Try to store cache for given key: '$key'")

        try {
            val outputStream = ByteArrayOutputStream()
            writer.writeTo(outputStream)
            logger.log(LogLevel.DEBUG, "Successfully written artifact with key '$key' into '$writer")
            // TODO: Check if that needs to be async...
            bucket.create(key.hashCode, outputStream.toByteArray(), "application/x-gzip")

            logger.log(LogLevel.DEBUG, "Successfully stored artifact with key '$key' into bucket '${bucket.name}'")
        } catch (ioexe: IOException) {
            logger.log(LogLevel.ERROR, "Error while write artifact with key '$key'", ioexe)
        }
    }

    @Throws(BuildCacheException::class)
    override fun load(key: BuildCacheKey, reader: BuildCacheEntryReader): Boolean {
        logger.log(LogLevel.DEBUG, "Try to load for given key: '$key'")

        val blob = storage.get(BlobId.of(bucket.name, key.hashCode)) ?: return false.also {
            logger.log(LogLevel.DEBUG, "No cache available for key '$key'")
        }

        return try {
            val inputStream = BufferedInputStream(ByteArrayInputStream(blob.getContent()))
            reader.readFrom(inputStream)
            logger.log(LogLevel.DEBUG, "Successfully loaded '$inputStream' into '$reader' for key '$key'")
            true
        } catch (ioexe: IOException) {
            logger.log(LogLevel.ERROR, "Error while write cloud artifact with key '$key'", ioexe)
            false
        }
    }

    override fun close() {
        // Clean up things...
        logger.log(LogLevel.DEBUG, "Close all resources for the given ${GCSCacheService::class.java.simpleName}")
    }
}

// TODO: Update me for required settings
open class GCSBuildCache : AbstractBuildCache()