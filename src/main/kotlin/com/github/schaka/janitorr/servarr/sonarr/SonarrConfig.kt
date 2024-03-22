package com.github.schaka.janitorr.servarr.sonarr

import com.github.schaka.janitorr.config.ApplicationProperties
import com.github.schaka.janitorr.config.FileSystemProperties
import com.github.schaka.janitorr.servarr.ServarrService
import com.github.schaka.janitorr.servarr.data_structures.Tag
import com.github.schaka.janitorr.servarr.history.HistoryResponse
import com.github.schaka.janitorr.servarr.quality_profile.QualityProfile
import com.github.schaka.janitorr.servarr.sonarr.episodes.EpisodeResponse
import com.github.schaka.janitorr.servarr.sonarr.series.SeriesPayload
import org.slf4j.LoggerFactory
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Only required for native image
 */
@Configuration(proxyBeanMethods = false)
@RegisterReflectionForBinding(classes = [QualityProfile::class, Tag::class, SeriesPayload::class, HistoryResponse::class, EpisodeResponse::class])
class SonarrConfig(
    val sonarrNoOpService: SonarrNoOpService
) {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }

    @Bean
    @Sonarr
    fun sonarrService(
        sonarrProperties: SonarrProperties,
        sonarrClient: SonarrClient,
        filesystemProperties: FileSystemProperties,
        applicationProperties: ApplicationProperties
    ): ServarrService {

        if (sonarrProperties.enabled) {
            val service = SonarrRestService(sonarrClient, filesystemProperties, applicationProperties)
            service.postConstruct()
            return service
        }

        return sonarrNoOpService
    }
}