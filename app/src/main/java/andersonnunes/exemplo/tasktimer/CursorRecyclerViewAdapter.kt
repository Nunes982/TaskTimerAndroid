package andersonnunes.exemplo.tasktimer

import android.annotation.SuppressLint
import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer


class TaskViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {
    val tliName: TextView = itemView.findViewById(R.id.tli_name)
    val tliDescription: TextView = itemView.findViewById(R.id.tli_description)
    val tliEdit: ImageButton = itemView.findViewById(R.id.tli_edit)
    val tliDelete: ImageButton = itemView.findViewById(R.id.tli_delete)

    fun bind(task: Task, listener: CursorRecyclerViewAdapter.OnTaskClickListener) {
        tliName.text = task.name
        tliDescription.text = task.description
        tliEdit.visibility = View.VISIBLE
        tliDelete.visibility = View.VISIBLE

        tliEdit.setOnClickListener {
            listener.onEditClick(task)
        }

        tliDelete.setOnClickListener {
            listener.onDeleteClick(task)
        }

        containerView.setOnLongClickListener {
            listener.onTaskLongClick(task)
            true
        }
    }
}

private const val TAG = "CursorRecyclerViewAdapt"

class CursorRecyclerViewAdapter(private var cursor: Cursor?, private val listener: OnTaskClickListener) :
        RecyclerView.Adapter<TaskViewHolder>() {

    interface OnTaskClickListener {
        fun onEditClick(task: Task)
        fun onDeleteClick(task: Task)
        fun onTaskLongClick(task: Task)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        Log.d(TAG, "onCreateViewHolder: new view requested")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_list_items, parent, false)
        return TaskViewHolder(view)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        val cursor = cursor    // avoid problems with smart cast

        if (cursor == null || cursor.count == 0) {
            holder.tliName.setText(R.string.instructions_heading)
            holder.tliDescription.setText(R.string.instructions)
            holder.tliEdit.visibility = View.GONE
            holder.tliDelete.visibility = View.GONE
        } else {
            if(!cursor.moveToPosition(position)) {
                throw IllegalStateException("Couldn't move cursor to position $position")
            }

            // Create a Task object from the data in the cursor
            val task = Task(
                    cursor.getString(cursor.getColumnIndex(TasksContract.Columns.TASK_NAME)),
                    cursor.getString(cursor.getColumnIndex(TasksContract.Columns.TASK_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndex(TasksContract.Columns.TASK_SORT_ORDER)))
            // Remember that the id isn't set in the constructor
            task.id = cursor.getLong(cursor.getColumnIndex(TasksContract.Columns.ID))

            holder.bind(task, listener)
        }
    }

    override fun getItemCount(): Int {
        val cursor = cursor
        val count = if (cursor == null || cursor.count == 0) {
            1   // fib, because we populate a single ViewHolder with instructions
        } else {
            cursor.count
        }
        return count
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.
     * the returned old Cursor is 8not8 closed
     *
     * @param newCursor The new cursor to be used
     * @return Returns the previously set Cursor, or null if there wasn't
     * one.
     * If the given new Cursor is the same instance as the previously set
     * Cursor, null is also returned.
     */
    fun swapCursor(newCursor: Cursor?): Cursor? {
        if (newCursor === cursor) {
            return null
        }
        val numItems = itemCount
        val oldCursor = cursor
        cursor = newCursor
        if (newCursor != null) {
            //notify the observers about the new cursor
            notifyDataSetChanged()
        } else {
            //notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, numItems)
        }
        return oldCursor
    }
}