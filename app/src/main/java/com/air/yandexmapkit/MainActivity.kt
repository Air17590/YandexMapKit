package com.air.yandexmapkit

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.user_location.UserLocationTapListener
import kotlinx.android.synthetic.main.activity_main.*
import com.yandex.mapkit.directions.driving.DrivingSession.DrivingRouteListener as DrivingRouteListener1


class MainActivity : AppCompatActivity(),DrivingSession.DrivingRouteListener {
    private var mapObjects: MapObjectCollection? = null
    override fun onDrivingRoutesError(p0: com.yandex.runtime.Error) {
        print("")
    }

    override fun onDrivingRoutes(p0: MutableList<DrivingRoute>) {
        val d = p0[0]
        p0.forEach {
            mapObjects?.addPolyline(it.geometry)
        }
        print("")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey("Key")
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
        mapview.map.move(
            CameraPosition(Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0f), null
        )
        val personaLayer = MapKitFactory.getInstance().createPersonalizedPoiLayer(mapview.mapWindow)
        val trafficLayer = MapKitFactory.getInstance().createTrafficLayer(mapview.mapWindow)
//        val s = GeoObjectTapListener { it ->
//            textview.text = it.geoObject.name.toString()
//            true
//        }
        personaLayer.isVisible = true
        // mapview.map.addTapListener(s)

        btn.setOnClickListener {
            mapview.map.isDebugInfoEnabled = true
            DirectionsFactory.initialize(this)
            val drvRouter = DirectionsFactory.getInstance().createDrivingRouter()
            val drOption = DrivingOptions()
            drOption.alternativeCount = 3
            val repoint1 = RequestPoint(Point(55.751574, 37.573856), RequestPointType.WAYPOINT, "")
            val repoint2 = RequestPoint(Point(55.751574, 38.573856), RequestPointType.WAYPOINT, "")
            val arr = arrayListOf<RequestPoint>(repoint1, repoint2)
            val drvSession = drvRouter.requestRoutes(arr, drOption, this)
            drvSession.retry(this)
            drvRouter.resume()
        }
        mapview.map.userLocationLayer.isEnabled = true

        val ssd = UserLocationTapListener {
            textview.text = "${it.latitude} ${it.longitude}"
        }
        mapview.map.userLocationLayer.setTapListener(ssd)
        mapObjects = mapview.map.mapObjects.addCollection()

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


}
