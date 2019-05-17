package com.air.yandexmapkit.model

import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.PolylineMapObject

class RouteSession : java.io.Serializable {
    var location: RequestPoint? = null
    var locationObj: MapObject? = null
    var destination: RequestPoint? = null
    var destinationObj: MapObject? = null
    var routesObjs: MutableList<PolylineMapObject> = mutableListOf()
    var driveRoutes: MutableList<DrivingRoute> = mutableListOf()
}