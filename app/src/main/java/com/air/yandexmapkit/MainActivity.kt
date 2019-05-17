package com.air.yandexmapkit

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.view.ViewGroup
import com.air.yandexmapkit.base.BaseActivity
import com.air.yandexmapkit.model.RouteSession
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.runtime.image.ImageProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_bottom.view.*
import com.yandex.mapkit.directions.driving.DrivingSession.DrivingRouteListener as DrivingRouteListener1


class MainActivity : BaseActivity() {
    private var mapObjects: MapObjectCollection? = null
    private var routeSession = RouteSession()

    override fun onMapTap(p0: Map, p1: Point) {
        showDialog(p1)
    }

    override fun onDrivingRoutesError(p0: com.yandex.runtime.Error) {
        print("")
    }

    override fun onDrivingRoutes(p0: MutableList<DrivingRoute>) {
        clearRoutes()
        routeSession.driveRoutes = p0
        p0.forEach {
            mapObjects?.addPolyline(it.geometry)?.let { polyObj ->
                routeSession.routesObjs.add(polyObj)
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
            if (routeSession.location == null || routeSession.destination == null) return@setOnClickListener showError()
            val drvRouter = DirectionsFactory.getInstance().createDrivingRouter()
            val drOption = DrivingOptions()
            drOption.alternativeCount = 3
            val arr = arrayListOf(routeSession.location!!, routeSession.destination!!)
            val drvSession = drvRouter.requestRoutes(arr, drOption, this)
        }
        mapObjects = mapview.map.mapObjects.addCollection()
        DirectionsFactory.initialize(this)
        mapview.map.addInputListener(this)
        (savedInstanceState?.getSerializable(SAVE_STATE) as? RouteSession)?.let {
            routeSession = it
            initRoute()
        }
    }

    fun initRoute() {
        routeSession.location?.let {
            routeSession.locationObj =
                mapObjects?.addPlacemark(it.point, ImageProvider.fromResource(this, R.mipmap.ic_location_point))
        }
        routeSession.destination?.let {
            routeSession.destinationObj =
                mapObjects?.addPlacemark(it.point, ImageProvider.fromResource(this, R.mipmap.ic_destination_point))
        }
        if (routeSession.routesObjs.isNotEmpty()) {
            routeSession.routesObjs.clear()
            routeSession.driveRoutes.forEach {
                mapObjects?.addPolyline(it.geometry)?.let { polyObj ->
                    routeSession.routesObjs.add(polyObj)
                }
            }
        }
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

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putSerializable(SAVE_STATE, routeSession)
        super.onSaveInstanceState(outState)
    }

    private fun clearRoutes() {
        routeSession.routesObjs.forEach { mapObjects?.remove(it) }
        routeSession.routesObjs.clear()
    }

    private fun showDialog(p: Point) {
        val mBottomSheetDialog = BottomSheetDialog(this)
        val sheetView = this.layoutInflater.inflate(
            R.layout.dialog_bottom,
            null
        )
        sheetView.point_from_btn.setOnClickListener {
            clearRoutes()
            routeSession.locationObj?.let { mapObjects?.remove(it) }
            routeSession.locationObj =
                mapObjects?.addPlacemark(p, ImageProvider.fromResource(this, R.mipmap.ic_location_point))
            routeSession.location = RequestPoint(p, RequestPointType.WAYPOINT, "")
            mBottomSheetDialog.dismiss()
        }
        sheetView.point_to_btn.setOnClickListener {
            clearRoutes()
            routeSession.destinationObj?.let { mapObjects?.remove(it) }
            routeSession.destinationObj =
                mapObjects?.addPlacemark(p, ImageProvider.fromResource(this, R.mipmap.ic_destination_point))
            routeSession.destination = RequestPoint(p, RequestPointType.WAYPOINT, "")
            mBottomSheetDialog.dismiss()
        }
        mBottomSheetDialog.setContentView(sheetView)
        mBottomSheetDialog.setOnShowListener {
            val bottomSheet = mBottomSheetDialog.findViewById<ViewGroup>(R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        mBottomSheetDialog.show()
    }

    companion object {
        const val SAVE_STATE = "SAVE_STATE"
    }
}
