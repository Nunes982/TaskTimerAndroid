package andersonnunes.exemplo.tasktimer

import andersonnunes.exemplo.tasktimer.databinding.SettingsDialogBinding
import android.os.Bundle
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatDialogFragment
import java.util.GregorianCalendar
import java.util.Locale

private const val TAG = "SettingsDialog"

private lateinit var binding: SettingsDialogBinding

const val SETTINGS_FIRST_DAY_OF_WEEK = "FirstDay"
const val SETTINGS_IGNORE_LESS_THAN = "IgnoreLessThan"
const val SETTINGS_DEFAULT_IGNORE_LESS_THAN = 0

//                              0  1  2   3   4   5   6   7   8   9   10  11  12  13   14   15   16   17   18   19   20   21   22   23    24
private val deltas = intArrayOf(0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 120, 180, 240, 300, 360, 420, 480, 540, 600, 900, 1800, 2700)

class SettingsDialog: AppCompatDialogFragment() {

    private val defaultFirstDayOfWeek = GregorianCalendar(Locale.getDefault()).firstDayOfWeek
    private var firstDay = defaultFirstDayOfWeek
    private var ignoreLessThan = SETTINGS_DEFAULT_IGNORE_LESS_THAN

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: called")
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.SettingsDialogStyle)
        retainInstance = true
    }

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

        dialog?.setTitle(R.string.action_settings)

        binding.okButton.setOnClickListener {
            saveValues()
            dismiss()
        }

        binding.ignoreSeconds.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress <12) {
                    binding.ignoreSecondsTitle.text = getString(R.string.settingsIgnoreSecondsTitle,
                    deltas[progress],
                    resources.getQuantityString(R.plurals.settingsLittleUnits, deltas[progress]))
                } else {
                    val minutes = deltas[progress] / 60
                    binding.ignoreSecondsTitle.text = getString(R.string.settingsIgnoreSecondsTitle,
                        minutes,
                        resources.getQuantityString(R.plurals.settingsBigUnits, minutes))
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            // We don't need this
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            // nor this
            }
        })

        binding.cancelButton.setOnClickListener { dismiss() }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        Log.d(TAG,"onViewStateRestored: called")

        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState == null) {
            readValues()

            binding.firstDaySpinner.setSelection(firstDay - GregorianCalendar.SUNDAY)  // spinner values are zero

            //convert seconds into an index into the time values array
            val seekBarValue = deltas.binarySearch(ignoreLessThan)
            if (seekBarValue < 0) {
                // this shouldn't happen, the programmer's made a mistake
                throw IndexOutOfBoundsException("Value $seekBarValue not found in deltas array")
            }

            binding.ignoreSeconds.max = deltas.size - 1
            Log.d(TAG, "onViewStateRestored: setting slidr to $seekBarValue")
            binding.ignoreSeconds.progress = seekBarValue

            if (ignoreLessThan < 60) {
                binding.ignoreSecondsTitle.text = getString(
                    R.string.settingsIgnoreSecondsTitle,
                    ignoreLessThan,
                    resources.getQuantityString(R.plurals.settingsBigUnits, ignoreLessThan)
                )
            } else {
                val minutes = ignoreLessThan / 60
                binding.ignoreSecondsTitle.text = getString(
                    R.string.settingsIgnoreSecondsTitle,
                    minutes,
                    resources.getQuantityString(R.plurals.settingsBigUnits, minutes)
                )
            }
        }    }

    private fun readValues() {
        with(getDefaultSharedPreferences(context)) {
            firstDay = getInt(SETTINGS_FIRST_DAY_OF_WEEK, defaultFirstDayOfWeek)
            ignoreLessThan = getInt(
                SETTINGS_IGNORE_LESS_THAN,
                SETTINGS_DEFAULT_IGNORE_LESS_THAN
            )
        }
        Log.d(TAG, "Retrieving first day = $firstDay, ignoreLessThan = $ignoreLessThan")
    }

    private fun saveValues() {
        val newFirstDay = binding.firstDaySpinner.selectedItemPosition + GregorianCalendar.SUNDAY
        val newIgnoreLessThan = deltas[binding.ignoreSeconds.progress]

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

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: called")
        super.onDestroy()
    }
}