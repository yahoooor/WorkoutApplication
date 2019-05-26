package com.example.workoutnotebook

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_step_counter.*

class StepCounterActivity : AppCompatActivity(), SensorEventListener, StepListener {
    private var simpleStepDetector: StepDetector? = null
    private var sensorManager: SensorManager? = null
    private val TEXT_NUM_STEPS = "Number of Steps: "
    private var numSteps: Int = 0

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector!!.updateAccelerometer(event.timestamp, event.values[0], event.values[1], event.values[2])
        }
    }

    override fun step(timeNs: Long) {
        numSteps++
        tvSteps.text = TEXT_NUM_STEPS.plus(numSteps)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_counter)

        // Get an instance of the SensorManager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        simpleStepDetector = StepDetector()
        simpleStepDetector!!.registerListener(this)

        btnStart.setOnClickListener {
            numSteps = 0
            sensorManager!!.registerListener(this, sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST)
        }

        btnStop.setOnClickListener {
            sensorManager!!.unregisterListener(this)
        }
    }
}