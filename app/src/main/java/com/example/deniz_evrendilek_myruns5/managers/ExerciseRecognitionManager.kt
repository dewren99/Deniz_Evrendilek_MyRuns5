package com.example.deniz_evrendilek_myruns5.managers

import android.hardware.SensorEvent
import com.example.deniz_evrendilek_myruns5.generated.WekaClassifier
import com.example.deniz_evrendilek_myruns5.utils.FFT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.util.concurrent.ArrayBlockingQueue
import kotlin.math.pow
import kotlin.math.sqrt

object ExerciseRecognitionManager {
    private const val ACCELEROMETER_BLOCK_CAPACITY = 64
    private const val FEATURES_LEN = ACCELEROMETER_BLOCK_CAPACITY + 1
    private var instance = DoubleArray(FEATURES_LEN)
    private var sensorEventBuffer = ArrayBlockingQueue<Double>(ACCELEROMETER_BLOCK_CAPACITY)

    private fun addToBuffer(double: Double) {
        try {
            sensorEventBuffer.add(double)
        } catch (e: Exception) {
            val newBuffer = ArrayBlockingQueue<Double>(sensorEventBuffer.size * 2)
            sensorEventBuffer.drainTo(newBuffer)
            sensorEventBuffer = newBuffer
            sensorEventBuffer.add(double)
        }
    }

    fun addSensorEventToBuffer(event: SensorEvent) {
        val magnitude = getMagnitude(event)
        addToBuffer(magnitude)
    }

    private fun clearInstance() {
        instance = DoubleArray(FEATURES_LEN)
    }

    private fun getMagnitude(event: SensorEvent): Double {
        val v0 = event.values[0].toDouble()
        val v1 = event.values[1].toDouble()
        val v2 = event.values[2].toDouble()
        val temp = v0.pow(2) + v1.pow(2) + v2.pow(2)
        return sqrt(temp)
    }

    /**
     * Taken from https://canvas.sfu.ca/courses/80625/pages/service-implementation
     */
    private fun generateFeatureVector(re: DoubleArray) {
        clearInstance()
        val max = re.maxOrNull() ?: 0.0
        val im = DoubleArray(ACCELEROMETER_BLOCK_CAPACITY)
        FFT(ACCELEROMETER_BLOCK_CAPACITY).fft(re, im)
        println()
        for (i in re.indices) {
            val mag = Math.sqrt(re[i] * re[i] + im[i] * im[i])
            // Adding the computed FFT coefficient to the feature vector
            instance[i] = mag
            // Clear the field
            im[i] = .0
        }
        instance[ACCELEROMETER_BLOCK_CAPACITY] = max
    }

    suspend fun process() {
        return withContext(Dispatchers.IO) {
            val events = DoubleArray(ACCELEROMETER_BLOCK_CAPACITY)
            var eventIndex = 0
            while (isActive) {
                events[eventIndex++] = sensorEventBuffer.take()
                if (events.size != ACCELEROMETER_BLOCK_CAPACITY) {
                    continue
                }
                eventIndex = 0
                generateFeatureVector(events)
                val res = WekaClassifier.classify(instance.toTypedArray())
                println("Classified process: $res")
            }
        }
    }

}