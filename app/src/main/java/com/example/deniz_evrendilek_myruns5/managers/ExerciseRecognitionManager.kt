package com.example.deniz_evrendilek_myruns5.managers

import android.hardware.SensorEvent
import com.example.deniz_evrendilek_myruns5.generated.WekaClassifier
import com.example.deniz_evrendilek_myruns5.utils.Complex
import com.example.deniz_evrendilek_myruns5.utils.FFTKotlin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.util.concurrent.ArrayBlockingQueue
import kotlin.math.pow
import kotlin.math.sqrt

object ExerciseRecognitionManager {
    private const val ACCELEROMETER_BLOCK_CAPACITY = 64
    private const val FEATURES_LEN = ACCELEROMETER_BLOCK_CAPACITY + 1
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

    /**
     * https://mathinsight.org/definition/magnitude_vector
     */
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
    private fun generateFeatureVector(events: DoubleArray): DoubleArray {
        val instance = DoubleArray(FEATURES_LEN)
        val max = events.maxOrNull() ?: 0.0
        val complex = events.map { Complex(it, 0.0) }.toTypedArray()
        val fftResult = FFTKotlin.fft(complex)
        val im = fftResult.map { it.im }.toTypedArray()
        val re = fftResult.map { it.re }.toTypedArray()
        for (i in re.indices) {
            val mag = Math.sqrt(re[i] * re[i] + im[i] * im[i])
            // Adding the computed FFT coefficient to the feature vector
            instance[i] = mag
            // Clear the field
            im[i] = .0
        }
        instance[ACCELEROMETER_BLOCK_CAPACITY] = max
        return instance
    }

    /**
     * Continuously reads sensor events and classifies them
     * in 64 event chunks in the IO thread.
     */
    suspend fun process(onProcessed: (Double) -> Unit) {
        return withContext(Dispatchers.IO) {
            val events = DoubleArray(ACCELEROMETER_BLOCK_CAPACITY)
            var eventIndex = 0

            while (isActive) {
                events[eventIndex++] = try {
                    sensorEventBuffer.take()
                } catch (e: InterruptedException) {
                    eventIndex = 0
                    continue
                }
                if (events.size != ACCELEROMETER_BLOCK_CAPACITY) {
                    continue
                }
                eventIndex = 0
                val instance = generateFeatureVector(events)
                val res = try {
                    WekaClassifier.classify(instance.toTypedArray())
                } catch (e: Exception) {
                    Double.NaN
                }
                if (res.isNaN()) {
                    // throw IllegalStateException("Classifier returned NaN.")
                    // Silently fail, and try to recover by resetting the loop
                    println("Something went wrong, resetting everything")
                    eventIndex = 0
                    continue
                }
                println("Classified process: $res")
                onProcessed(res)
            }
        }
    }

}