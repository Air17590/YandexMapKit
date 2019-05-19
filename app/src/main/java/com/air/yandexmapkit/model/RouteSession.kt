package com.air.yandexmapkit.model

import android.os.Parcel
import android.os.Parcelable
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.PolylineMapObject

class RouteSession() : Parcelable {
    var location: RequestPoint? = null
    var destination: RequestPoint? = null
    var locationObj: MapObject? = null
    var destinationObj: MapObject? = null
    var routesObjs: MutableList<PolylineMapObject> = mutableListOf()
    var driveRoutes: MutableList<ByteArray> = mutableListOf()

    constructor(parcel: Parcel) : this() {
        val isLocationExists = parcel.readInt() == 1
        val isDestinationExists = parcel.readInt() == 1
        if (isLocationExists) {
            val locLat = parcel.readDouble()
            val locLong = parcel.readDouble()
            location = RequestPoint(Point(locLat, locLong), RequestPointType.WAYPOINT, null)
        }
        if (isDestinationExists) {
            val destLat = parcel.readDouble()
            val destLong = parcel.readDouble()
            location = RequestPoint(Point(destLat, destLong), RequestPointType.WAYPOINT, null)
        }
        var routeCount = parcel.readInt()
        while (routeCount > 0) {
            val arr = byteArrayOf()
            parcel.readByteArray(arr)
            driveRoutes.add(arr)
            routeCount--
        }
    }


    fun clearRoutes() {
        routesObjs.clear()
        driveRoutes.clear()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        val isLocationExists = location?.point != null
        val isDestinationExists = destination?.point != null
        parcel.writeInt(if (isLocationExists) 1 else 0)
        parcel.writeInt(if (isDestinationExists) 1 else 0)
        if (isLocationExists) {
            parcel.writeDouble(location!!.point.latitude)
            parcel.writeDouble(location!!.point.longitude)
        }
        if (isDestinationExists) {
            parcel.writeDouble(destination!!.point.latitude)
            parcel.writeDouble(destination!!.point.longitude)
        }
        parcel.writeInt(driveRoutes.size)
        driveRoutes.forEach {
            parcel.writeByteArray(it)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RouteSession> {
        override fun createFromParcel(parcel: Parcel): RouteSession {
            return RouteSession(parcel)
        }

        override fun newArray(size: Int): Array<RouteSession?> {
            return arrayOfNulls(size)
        }
    }
}