package com.ciarandg.soundbounds.common.regions

import com.ciarandg.soundbounds.server.metadata.ServerMetaState

// Can check for regions that have missing metadata or empty playlists
object RegionAuditer {
    private fun songsMissingMetaInRegion(region: RegionData): Set<String> {
        val meta = ServerMetaState.get().meta
        return region.playlist.filter { songID ->
            !meta.songs.containsKey(songID)
        }.toSet()
    }

    // Returns a set of Pair(regionID, missingSongs). Only includes regions that have >= 1 missing songs.
    fun auditMissingMeta(regions: Collection<Map.Entry<String, RegionData>>): Set<Pair<String, Set<String>>> =
        regions.map { regionEntry ->
            Pair(regionEntry.key, songsMissingMetaInRegion(regionEntry.value))
        }.filter { it.second.isNotEmpty() }.toSet()

    // Returns a set of regionIDs, corresponding to all regions with empty playlists
    fun auditEmptyPlaylists(regions: Collection<Map.Entry<String, RegionData>>): Set<String> =
        regions.filter { it.value.playlist.isEmpty() }
            .map { it.key }.toSet()
}