package com.ciarandegroot.soundbounds.server.ui

class ServerWorldController(private val model: WorldModel = WorldModel()) {
    fun createRegion(regionName: String, priority: Int) {}
    fun destroyRegion(regionName: String) {}
    fun getRegionDataCopy(regionName: String): RegionData = RegionData()
    fun setRegionData(regionName: String, data: RegionData) {}
}

data class RegionData(val placeholder: Int = 0) // TODO

data class WorldModel(val regions: HashMap<String, RegionData> = HashMap())
