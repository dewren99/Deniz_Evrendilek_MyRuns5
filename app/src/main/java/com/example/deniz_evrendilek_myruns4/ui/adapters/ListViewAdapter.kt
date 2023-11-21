package com.example.deniz_evrendilek_myruns5.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.deniz_evrendilek_myruns5.R
import com.example.deniz_evrendilek_myruns5.constants.ExerciseTypes
import com.example.deniz_evrendilek_myruns5.constants.InputTypes
import com.example.deniz_evrendilek_myruns5.data.model.ExerciseEntry
import com.example.deniz_evrendilek_myruns5.data.model.ManualExerciseEntryForm
import com.example.deniz_evrendilek_myruns5.ui.fragments.navigations.exercise.LocationStatistics


class ListViewAdapter(
    private val context: Context,
    private var exerciseEntryList: Array<ExerciseEntry>,
    @Suppress("unused") private val unitPreference: String,
    private val onHistoryItemClick: (ExerciseEntry) -> Unit,
) : ArrayAdapter<ExerciseEntry>(context, R.layout.history_item, exerciseEntryList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view =
            convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.history_item, parent, false)

        val item = exerciseEntryList[position]

        val inputType = InputTypes.getString(item.inputType)
        val exerciseType = ExerciseTypes.getString(item.activityType)
        val dateTime = ManualExerciseEntryForm.getDateTimeStr(item)
        val duration = ManualExerciseEntryForm.getDurationStr(item)

        val isManualEntry = InputTypes.isManualEntry(item.inputType)
        val distance = if (isManualEntry) {
            ManualExerciseEntryForm.getDistanceStr(unitPreference, item)
        } else {
            LocationStatistics.distance(context, item.distance)
        }

        val title = "$inputType: $exerciseType, $dateTime"
        val text = "$distance $duration"

        view.findViewById<TextView>(R.id.history_item_title).text = title
        view.findViewById<TextView>(R.id.history_item_text).text = text

        view.setOnClickListener { _ ->
            onHistoryItemClick(item)
        }
        return view
    }
}