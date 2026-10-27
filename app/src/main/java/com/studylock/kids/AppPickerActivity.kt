package com.studylock.kids

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.studylock.kids.data.Prefs

data class AppItem(val pkg: String, val label: String, val icon: Drawable)

/** List of installed apps with checkboxes. */
class AppPickerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_picker)
        title = getString(R.string.choose_apps)

        val selected = Prefs.getLockedApps(this).toMutableSet()
        val pm = packageManager
        val apps = pm.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0
        ).map {
            AppItem(it.activityInfo.packageName, it.loadLabel(pm).toString(), it.loadIcon(pm))
        }.filter { it.pkg != packageName }
            .distinctBy { it.pkg }
            .sortedBy { it.label.lowercase() }

        findViewById<RecyclerView>(R.id.rvApps).apply {
            layoutManager = LinearLayoutManager(this@AppPickerActivity)
            adapter = AppAdapter(apps, selected)
        }
        findViewById<Button>(R.id.btnSaveApps).setOnClickListener {
            Prefs.setLockedApps(this, selected)
            finish()
        }
    }
}

class AppAdapter(
    private val items: List<AppItem>,
    private val selected: MutableSet<String>
) : RecyclerView.Adapter<AppAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val icon: ImageView = v.findViewById(R.id.ivIcon)
        val name: TextView = v.findViewById(R.id.tvName)
        val check: CheckBox = v.findViewById(R.id.cbLock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false))

    override fun onBindViewHolder(h: VH, position: Int) {
        val a = items[position]
        h.icon.setImageDrawable(a.icon)
        h.name.text = a.label
        h.check.setOnCheckedChangeListener(null)
        h.check.isChecked = a.pkg in selected
        h.check.setOnCheckedChangeListener { _, on ->
            if (on) selected.add(a.pkg) else selected.remove(a.pkg)
        }
        h.itemView.setOnClickListener { h.check.toggle() }
    }

    override fun getItemCount() = items.size
}
