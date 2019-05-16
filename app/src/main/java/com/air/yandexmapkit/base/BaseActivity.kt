package com.air.yandexmapkit.base

import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.air.yandexmapkit.R
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map

abstract class BaseActivity : AppCompatActivity(),  DrivingSession.DrivingRouteListener, InputListener {

    override fun onMapLongTap(p0: Map, p1: Point) {}

    protected fun showError() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.route_error))
            .setNeutralButton(
                getString(R.string.ok)
            ) { dialog, _ -> dialog.cancel() }
        builder.create().show()
    }
}