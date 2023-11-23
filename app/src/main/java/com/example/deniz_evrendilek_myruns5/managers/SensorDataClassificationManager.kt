package com.example.deniz_evrendilek_myruns5.managers

import android.content.Context
import weka.classifiers.bayes.NaiveBayes
import weka.core.Instance
import weka.core.Instances
import java.io.BufferedReader

class SensorDataClassificationManager(context: Context) {
    private val classifier = NaiveBayes()
    private lateinit var instances: Instances

    init {
        readFeaturesFile(context, ::setupClassifier)
    }

    private fun readFeaturesFile(context: Context, onReadCallback: (BufferedReader) -> Unit) {
        val inputStream = context.assets.open("features.arff")
        val bufferedReader = inputStream.bufferedReader()
        onReadCallback(bufferedReader)
        bufferedReader.close()
    }

    private fun setupClassifier(bufferedReader: BufferedReader) {
        instances = Instances(bufferedReader)
        instances.setClassIndex(instances.numAttributes() - 1)
        classifier.buildClassifier(instances)
    }

    fun classify(instance: Instance): Double {
        instance.setDataset(instances)
        val i = classifier.classifyInstance(instance)
        println("classify $i")
        return i
    }
}