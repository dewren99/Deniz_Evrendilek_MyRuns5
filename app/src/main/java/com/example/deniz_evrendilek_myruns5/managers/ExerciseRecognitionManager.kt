package com.example.deniz_evrendilek_myruns5.managers

import android.hardware.SensorEvent
import com.example.deniz_evrendilek_myruns5.utils.FFT
import weka.core.Instance
import kotlin.math.pow
import kotlin.math.sqrt

object ExerciseRecognitionManager {
    private const val ACCELEROMETER_BLOCK_CAPACITY = 64
    private const val FEATURES_LEN = ACCELEROMETER_BLOCK_CAPACITY + 1

    private var instance = Instance(FEATURES_LEN)

    private fun clearInstance() {
        instance = Instance(FEATURES_LEN)
    }

    private fun onClassify(classifier: (Instance) -> Double): Double {
        val res = classifier(instance)
        clearInstance()
        return res
    }

    private fun getMagnitude(event: SensorEvent): Double {
        val v0 = event.values[0].toDouble()
        val v1 = event.values[1].toDouble()
        val v2 = event.values[2].toDouble()
        val temp = v0.pow(2) + v1.pow(2) + v2.pow(2)
        return sqrt(temp)
    }

    private fun generateFeatureVector(re: List<Double>) {
        val max = re.maxOrNull() ?: 0.0
        val im = DoubleArray(ACCELEROMETER_BLOCK_CAPACITY)
        println("List sizes: ${im.size}, ${re.size}")
        FFT(ACCELEROMETER_BLOCK_CAPACITY).fft(re.toDoubleArray(), im)
        println("real AVG: ${re.average()}")
        println("imaginary non-zero: ${im.filter { it != 0.0 }.size}")
        println()
        for (i in re.indices) {
            val mag = Math.sqrt(re[i] * re[i] + im[i] * im[i])
            // Adding the computed FFT coefficient to the feature vector
            instance.setValue(i, mag)
            // Clear the field
            im[i] = .0;
        }
        instance.setValue(ACCELEROMETER_BLOCK_CAPACITY, max);
    }

    fun process(events: List<SensorEvent>, classifier: (Instance) -> Double): Double {
        val magnitudes: List<Double> = events.map {
            getMagnitude(it)
        }
        generateFeatureVector(magnitudes)
        return onClassify(classifier)
    }


}