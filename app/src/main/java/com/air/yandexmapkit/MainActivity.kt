package com.air.yandexmapkit

import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
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
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_bottom.view.*
import com.yandex.mapkit.directions.driving.DrivingSession.DrivingRouteListener as DrivingRouteListener1


class MainActivity : BaseActivity(), UserLocationObjectListener {
    private var mapObjects: MapObjectCollection? = null
    private var drvRouter: DrivingRouter? = null
    private var routeSession = RouteSession()
    private var userLocationLayer: UserLocationLayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey("cce58919-fc3a-401a-ae13-aaa1d03284da")
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
        mapview.map.move(
            CameraPosition(Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0f), null
        )
        if (ContextCompat.checkSelfPermission(this, LOCATION_PERMISSION_NAME) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(LOCATION_PERMISSION_NAME), LOCATION_PERMISSION_REQUEST
            )
        } else {
            addUserLocationLayer()
        }
        btn.setOnClickListener {
            if (routeSession.location == null || routeSession.destination == null) return@setOnClickListener showError()
            val drOption = DrivingOptions()
            drOption.alternativeCount = 3
            val arr = arrayListOf(routeSession.location!!, routeSession.destination!!)
            val drvSession = drvRouter?.requestRoutes(arr, drOption, this)

        }
        mapObjects = mapview.map.mapObjects.addCollection()
        DirectionsFactory.initialize(this)
        mapview.map.addInputListener(this)
        drvRouter = DirectionsFactory.getInstance().createDrivingRouter()
        savedInstanceState?.getParcelable<RouteSession>(SAVE_STATE)?.let {
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
        routeSession.driveRoutes.forEach {
            drvRouter?.routeSerializer()?.load(it)?.let { route ->
                mapObjects?.addPolyline(route.geometry)?.let { polyObj ->
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
        outState?.putParcelable(SAVE_STATE, routeSession)
        super.onSaveInstanceState(outState)
    }

    private fun clearRoutes() {
        routeSession.routesObjs.forEach { mapObjects?.remove(it) }
        routeSession.clearRoutes()
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

    private fun addUserLocationLayer() {
        userLocationLayer = mapview.map.userLocationLayer
        userLocationLayer?.isEnabled = true
        userLocationLayer?.isHeadingEnabled = true
        userLocationLayer?.setObjectListener(this)
    }

    override fun onMapTap(p0: Map, p1: Point) {
        showDialog(p1)
    }

    override fun onDrivingRoutesError(p0: com.yandex.runtime.Error) {
        print("")
    }

    override fun onDrivingRoutes(p0: MutableList<DrivingRoute>) {
        clearRoutes()
        p0.forEach { route ->
            drvRouter?.routeSerializer()?.save(route)?.let {
                routeSession.driveRoutes.add(it)
            }
            mapObjects?.addPolyline(route.geometry)?.let { polyObj ->
                routeSession.routesObjs.add(polyObj)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            addUserLocationLayer()
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {}

    override fun onObjectRemoved(p0: UserLocationView) {}

    override fun onObjectAdded(p0: UserLocationView) {
        userLocationLayer?.setAnchor(
            PointF((mapview.width * 0.5).toFloat(), (mapview.height * 0.5).toFloat()),
            PointF((mapview.width * 0.5).toFloat(), (mapview.height * 0.83).toFloat())
        )

        p0.arrow.setIcon(
            ImageProvider.fromResource(
                this, R.mipmap.ic_user_location_point
            )
        )
        val pinIcon = p0.pin.useCompositeIcon()
        pinIcon.setIcon(
            "pin",
            ImageProvider.fromResource(this, R.mipmap.ic_user_location_point),
            IconStyle().setAnchor(PointF(0.5f, 0.5f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(1f)
                .setScale(0.5f)
        )
        userLocationLayer?.cameraPosition()?.target?.let {
            mapview.map.move(
                CameraPosition(it, 11.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 0f), null
            )
        }
    }

    companion object {
        const val SAVE_STATE = "SAVE_STATE"
        private const val LOCATION_PERMISSION_NAME = "android.permission.ACCESS_FINE_LOCATION"
        private const val LOCATION_PERMISSION_REQUEST = 1
    }
}
