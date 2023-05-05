package andersonnunes.exemplo.tasktimer

import andersonnunes.exemplo.tasktimer.databinding.SettingsDialogBinding
import android.os.Bundle
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import java.util.GregorianCalendar
import java.util.Locale

private const val TAG = "SettingsDialog"

private lateinit var binding: SettingsDialogBinding

const val SETTINGS_FIRST_DAY_OF_WEEK = "FirstDay"
const val SETTINGS_IGNORE_LESS_THAN = "IgnoreLessThan"
const val SETTINGS_DEFAULT_IGNORE_LESS_THAN = 0

class SettingsDialog: AppCompatDialogFragment() {

    private val defaultFirstDayOfWeek = GregorianCalendar(Locale.getDefault()).firstDayOfWeek
    private var firstDay = defaultFirstDayOfWeek
    private var ignoreLessThan = SETTINGS_DEFAULT_IGNORE_LESS_THAN

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: called")
        binding = SettingsDialogBinding.inflate(layoutInflater)
        return binding.root
//        return  inflater.inflate(R.layout.settings_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: called")

        super.onViewCreated(view, savedInstanceState)
        binding.okButton.setOnClickListener {
            saveValues()
            dismiss()
        }

        binding.cancelButton.setOnClickListener { dismiss() }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        Log.d(TAG,"onViewStateRestored: called")

        super.onViewStateRestored(savedInstanceState)
        readValues()

        binding.firstDaySpinner.setSelection(firstDay - GregorianCalendar.SUNDAY)  // spinner values are zero

        binding.ignoreSeconds.progress = ignoreLessThan
    }

    private fun readValues() {
        with(getDefaultSharedPreferences(context)) {
            firstDay = getInt(andersonnunes.exemplo.tasktimer.SETTINGS_FIRST_DAY_OF_WEEK, defaultFirstDayOfWeek)
            ignoreLessThan = getInt(andersonnunes.exemplo.tasktimer.SETTINGS_IGNORE_LESS_THAN,
                andersonnunes.exemplo.tasktimer.SETTINGS_DEFAULT_IGNORE_LESS_THAN)
        }
        Log.d(TAG, "Retrieving first day = $firstDay, ignoreLessThan = $ignoreLessThan")
    }

    private fun saveValues() {
        val newFirstDay = binding.firstDaySpinner.selectedItemPosition + GregorianCalendar.SUNDAY
        val newIgnoreLessThan = binding.ignoreSeconds.progress

        Log.d(TAG, "Saving first day = $newFirstDay, ignore seconds = $newIgnoreLessThan")

        with(getDefaultSharedPreferences(context).edit()) {
            if (newFirstDay != firstDay) {
                putInt(SETTINGS_FIRST_DAY_OF_WEEK, newFirstDay)
            }
            if (newIgnoreLessThan != ignoreLessThan) {
                putInt(SETTINGS_IGNORE_LESS_THAN, newIgnoreLessThan)
            }
            apply()
        }
    }
}