package andersonnunes.exemplo.tasktimer

import andersonnunes.exemplo.tasktimer.databinding.FragmentAddEditBinding
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

private const val TAG = "AddEditFragment"

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_TASK = "task"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AddEditFragment.OnSaveClicked] interface
 * to handle interaction events.
 * Use the [AddEditFragment.newInstance] factory method to
 * create an instance of this fragment. *
 */
class AddEditFragment : Fragment() {
    private var task: Task? = null
    private var listener: OnSaveClicked? = null

    private val viewModel by lazy { ViewModelProvider(requireActivity())[TaskTimerViewModel::class.java] }

    private var _fragmentBinding: FragmentAddEditBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _fragmentBinding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)

        task = arguments?.getParcelable(ARG_TASK)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: starts")

        // Inflate the layout for this fragment
        _fragmentBinding = FragmentAddEditBinding.inflate(inflater, container, false)
//        return inflater.inflate(R.layout.fragment_add_edit, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: called")
        if (savedInstanceState == null) {
            val task = task
            if (task != null) {
                Log.d(TAG, "onViewCreated: Task details found, editing task ${task.id}")
                binding.addeditName.setText(task.name)
                binding.addeditDescription.setText(task.description)
                binding.addeditSortorder.setText(Integer.toString(task.sortOrder))
            } else {
                // No task, so we must be adding a new task, and editing an existing one
                Log.d(TAG, "onViewCreated: No arguments, adding new record")
            }
        }
    }

    private fun taskFromUi(): Task {
        val sortOrder = if (binding.addeditSortorder.text.isNotEmpty()) {
            Integer.parseInt(binding.addeditSortorder.text.toString())
        } else {
            0
        }

        val newTask = Task(
            binding.addeditName.text.toString(),
            binding.addeditDescription.text.toString(),
            sortOrder
        )
        newTask.id = task?.id ?: 0

        return newTask
    }

    fun isDirty(): Boolean {
        val newTask = taskFromUi()
        return ((newTask != task) &&
                (newTask.name.isNotBlank()
                        || newTask.description.isNotBlank()
                        || newTask.sortOrder != 0)
                )
    }

    private fun saveTask() {
        // Create a newTask object with  the details to be saved, then
        // call the viewModel's saveTask function to save it,
        // Task is now a data class, so we can compare the new detail with the original task,
        // and only save if they are diferent.

        val newTask = taskFromUi()
        if (newTask != task) {
            Log.d(TAG, "saveTask: saving task, id is ${newTask.id}")
            task = viewModel.saveTask(newTask)
            Log.d(TAG, "saveTask: id is ${task?.id}")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated: starts")
        super.onActivityCreated(savedInstanceState)

        if (listener is AppCompatActivity) {
            val actionBar = (listener as AppCompatActivity?)?.supportActionBar
            actionBar?.setDisplayHomeAsUpEnabled(true)
        }

        val binding = view?.let { FragmentAddEditBinding.bind(it) }

        _fragmentBinding = binding
        binding?.addeditSave?.setOnClickListener {
            listener?.onSaveClicked()
            saveTask()
        }
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach: starts")
        super.onAttach(context)
        if (context is OnSaveClicked) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnSaveClicked")
        }
    }

    override fun onDetach() {
        Log.d(TAG, "onDetach: starts")
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnSaveClicked {
        fun onSaveClicked()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param task The task to be edited, or null to add a new task.
         * @return A new instance of fragment AddEditFragment.
         */
        @JvmStatic
        fun newInstance(task: Task?) =
            AddEditFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_TASK, task)
                }
            }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewStateRestored: called")
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onStart() {
        Log.d(TAG, "onStart: called")
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG, "onResume: called")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "onPause: called")
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "onSaveInstanceState: called")
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        Log.d(TAG, "onStop: called")
        super.onStop()
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView: called")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: called")
        super.onDestroy()
    }

}
