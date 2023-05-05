package andersonnunes.exemplo.tasktimer

import andersonnunes.exemplo.tasktimer.databinding.ActivityMainBinding
import andersonnunes.exemplo.tasktimer.databinding.ContentMainBinding
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

private const val TAG = "MainActivity"
private const val DIALOG_ID_CANCEL_EDIT = 1

class MainActivity : AppCompatActivity(),
    AddEditFragment.OnSaveClicked,
    MainActivityFragment.OnTaskEdit,
    AppDialog.DialogEvents {

    private lateinit var mainActivityBinding: ActivityMainBinding
    private lateinit var mainContentMainBinding: ContentMainBinding

    // Whether the activity is in 2-pane mode
    //i.e. running in landscape, or on a tablet
    private var mTwoPane = false

    // module scope because we need to dismiss it in onStop (e.g. when orientation changes) to avoid memory leaks.
    private var aboutDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        mainContentMainBinding = ContentMainBinding.inflate(layoutInflater)

        setContentView(mainActivityBinding.root)
        setSupportActionBar(mainActivityBinding.toolbar)

        mTwoPane = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        Log.d(TAG, "onCreate: twoPane is $mTwoPane")

        val fragment = findFragmentById(R.id.task_details_container)
        if(fragment != null) {
            // There was an existing fragment to edit a task, make sure the panes are set correctly
            showEditPane()
        } else {
            mainContentMainBinding.taskDetailsContainer.visibility =
                if (mTwoPane) View.INVISIBLE else View.GONE
            mainActivityBinding.mainContent.mainFragment.visibility = View.VISIBLE
        }
        Log.d(TAG, "onCreate: finished")
    }

    private fun showEditPane() {
        mainActivityBinding.mainContent.taskDetailsContainer.visibility = View.VISIBLE
        // hide the left hand pane, if in single pane view
        mainActivityBinding.mainContent.mainFragment.visibility =
            if (mTwoPane) View.VISIBLE else View.GONE
    }

    private fun removeEditPane(fragment: Fragment? = null) {
        Log.d(TAG, "removeEditPane called")
        if(fragment != null) {
//            supportFragmentManager.beginTransaction()
//                    .remove(fragment)
//                    .commit()
            removeFragment(fragment)
        }

        // Set the visibility of the right hand pane
        mainActivityBinding.mainContent.taskDetailsContainer.visibility =
            if (mTwoPane) View.INVISIBLE else View.GONE
        // and show the left hand pane
        mainActivityBinding.mainContent.mainFragment.visibility = View.VISIBLE

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onSaveClicked() {
        Log.d(TAG, "onSaveClicked: called")
        removeEditPane(findFragmentById(R.id.task_details_container))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.menumain_addTask -> taskEditRequest(null)
            R.id.menumain_settings -> {
                val dialog = SettingsDialog()
                dialog.show(supportFragmentManager, null)
            }
            R.id.menumain_showAbout -> showAboutDialog()
            android.R.id.home -> {
                Log.d(TAG, "onOptionsItemSelected: home button pressed")
                val fragment = findFragmentById(R.id.task_details_container)
//                removeEditPane(fragment)
                if ((fragment is AddEditFragment) && fragment.isDirty()) {
                    showConfirmationDialog(
                        DIALOG_ID_CANCEL_EDIT,
                        getString(R.string.cancelEditDiag_message),
                        R.string.cancelEditDiag_positive_caption,
                        R.string.cancelEditDiag_negative_caption
                    )
                } else {
                    removeEditPane(fragment)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

//    private fun showAboutDialog() {
//        val messageView = layoutInflater.inflate(R.layout.about, null, false)
//        val builder = AlertDialog.Builder(this)
//
//        builder.setTitle(R.string.app_name)     // This will work
//        builder.setIcon(R.mipmap.ic_launcher)
//
//        aboutDialog = builder.setView(messageView).create()
//        aboutDialog?.setCanceledOnTouchOutside(true)
//
//        messageView.setOnLongClickListener {
//            Log.d(TAG, "Entering messageView.onClick")
//            if (aboutDialog != null && aboutDialog?.isShowing == true) {
//                aboutDialog?.dismiss()
//            }
//        }
//
//        val aboutVersion = messageView.findViewById(R.id.about_version) as TextView
//        aboutVersion.text = BuildConfig.VERSION_NAME
//
//        aboutDialog?.show()
//    }

    private fun showAboutDialog() {
        val messageView = layoutInflater.inflate(R.layout.about, null, false)
        val builder = AlertDialog.Builder(this)

        builder.setTitle(R.string.app_name)     // This will work
        builder.setIcon(R.mipmap.ic_launcher)

        builder.setPositiveButton(R.string.ok) { _, _ ->
            Log.d(TAG, "onClick: Entering messageView.onClick")
            if (aboutDialog != null && aboutDialog?.isShowing == true) {
                aboutDialog?.dismiss()
            }
        }

        aboutDialog = builder.setView(messageView).create()
        aboutDialog?.setCanceledOnTouchOutside(true)

        val aboutVersion = messageView.findViewById(R.id.about_version) as TextView
        aboutVersion.text = BuildConfig.VERSION_NAME

        // Use a nullable type: the TextView won't exist on API 21 and higher
        val aboutUrl: TextView? = messageView.findViewById(R.id.about_url)
        aboutUrl?.setOnClickListener { v ->
            val intent = Intent(Intent.ACTION_VIEW)
            val s = (v as TextView).text.toString()
            intent.data = Uri.parse(s)
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    this@MainActivity,
                    R.string.about_url_error,
                    Toast.LENGTH_LONG
                ).show()
            }

        }
        aboutDialog?.show()
    }

    override fun onTaskEdit(task: Task) {
        taskEditRequest(task)
    }

    private fun taskEditRequest(task: Task?) {
        Log.d(TAG, "taskEditRequest: starts")

        // Create a new fragment to edit the task
//        val newFragment = AddEditFragment.newInstance(task)
//        supportFragmentManager.beginTransaction()
//                .replace(R.id.task_details_container, newFragment)
//                .commit()

        showEditPane()
        replaceFragment(AddEditFragment.newInstance(task), R.id.task_details_container)
        Log.d(TAG, "Exiting taskEditRequest")
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val fragment = findFragmentById(R.id.task_details_container)
        if (fragment == null || mTwoPane) {
            super.onBackPressed()
        } else {
//            removeEditPane(fragment)
            if ((fragment is AddEditFragment) && fragment.isDirty()) {
                showConfirmationDialog(
                    DIALOG_ID_CANCEL_EDIT,
                    getString(R.string.cancelEditDiag_message),
                    R.string.cancelEditDiag_positive_caption,
                    R.string.cancelEditDiag_negative_caption
                )
            } else {
                removeEditPane(fragment)
            }
        }
    }

    override fun onPositiveDialogResult(dialogId: Int, arg: Bundle) {
        Log.d(TAG, "onPositiveDialogResult: called with dialogId $dialogId")
        if (dialogId == DIALOG_ID_CANCEL_EDIT) {
            val fragment = findFragmentById(R.id.task_details_container)
            removeEditPane()
        }
    }

    override fun onStart() {
        Log.d(TAG, "onStart: called")
        super.onStart()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.d(TAG, "onRestoreInstanceState: called")
        super.onRestoreInstanceState(savedInstanceState)
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
        if (aboutDialog?.isShowing == true) {
            aboutDialog?.dismiss()
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: called")
        super.onDestroy()
    }
}
