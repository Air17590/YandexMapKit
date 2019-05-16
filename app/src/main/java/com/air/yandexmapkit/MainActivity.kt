package com.air.yandexmapkit

import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import com.air.yandexmapkit.base.BaseActivity
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.runtime.image.ImageProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_bottom.view.*
import com.yandex.mapkit.directions.driving.DrivingSession.DrivingRouteListener as DrivingRouteListener1


class MainActivity : BaseActivity() {
    private var mapObjects: MapObjectCollection? = null
    private var location: RequestPoint? = null
    private var locationObj: MapObject? = null
    private var destination: RequestPoint? = null
    private var destinationObj: MapObject? = null
    private var routes: MutableList<PolylineMapObject> = mutableListOf()

    override fun onMapTap(p0: Map, p1: Point) {
        showDialog(p1)
    }

    override fun onDrivingRoutesError(p0: com.yandex.runtime.Error) {
        print("")
    }

    override fun onDrivingRoutes(p0: MutableList<DrivingRoute>) {
        clearRoutes()
        p0.forEach {
            mapObjects?.addPolyline(it.geometry)?.let { polyObj ->
                routes.add(polyObj)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey("cce58919-fc3a-401a-ae13-aaa1d03284da")
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
        mapview.map.move(
            CameraPosition(Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0f), null
        )
        btn.setOnClickListener {
            if (location == null || destination == null) return@setOnClickListener showError()
            DirectionsFactory.initialize(this)
            val drvRouter = DirectionsFactory.getInstance().createDrivingRouter()
            val drOption = DrivingOptions()
            drOption.alternativeCount = 3
            val arr = arrayListOf(location!!, destination!!)
            val drvSession = drvRouter.requestRoutes(arr, drOption, this)
        }
        mapObjects = mapview.map.mapObjects.addCollection()
        mapview.map.addInputListener(this)
    }

    override fun onStart() {
        super.onStart()
        mapview.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        mapview.onStop()
        MapKitFactory.getInstance().onStop()
    }

    private fun clearRoutes() {
        routes.forEach { mapObjects?.remove(it) }
        routes.clear()
    }

    private fun showDialog(p: Point) {
        val mBottomSheetDialog = BottomSheetDialog(this)
        val sheetView = this.layoutInflater.inflate(R.layout.dialog_bottom, null)
        sheetView.point_from_btn.setOnClickListener {
            clearRoutes()
            locationObj?.let { mapObjects?.remove(it) }
            locationObj = mapObjects?.addPlacemark(p, ImageProvider.fromResource(this, R.mipmap.ic_location_point))
            location = RequestPoint(p, RequestPointType.WAYPOINT, "")
            mBottomSheetDialog.dismiss()
        }
        sheetView.point_to_btn.setOnClickListener {
            clearRoutes()
            destinationObj?.let { mapObjects?.remove(it) }
            destinationObj =
                mapObjects?.addPlacemark(p, ImageProvider.fromResource(this, R.mipmap.ic_destination_point))
            destination = RequestPoint(p, RequestPointType.WAYPOINT, "")
            mBottomSheetDialog.dismiss()
        }
        mBottomSheetDialog.setContentView(sheetView)
        mBottomSheetDialog.show()
    }


}
