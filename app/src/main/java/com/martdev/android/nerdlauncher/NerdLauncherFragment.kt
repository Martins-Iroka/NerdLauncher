package com.martdev.android.nerdlauncher

import android.app.WallpaperManager
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.Comparator

class NerdLauncherFragment : Fragment() {

    private lateinit var mRecyclerView: RecyclerView

    companion object {
        private const val TAG = "NerdLauncherFragment"

        fun newInstance(): NerdLauncherFragment {
            return NerdLauncherFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity!!.window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_nerd_launcher, container, false)

        mRecyclerView = view.findViewById(R.id.app_recycler_view)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)

        setupAdapter()
        return view
    }

    override fun onResume() {
        super.onResume()
        val wallpaper = WallpaperManager.getInstance(activity!!).drawable
        mRecyclerView.background = wallpaper
    }

    private fun setupAdapter() {
        val startupIntent = Intent(Intent.ACTION_MAIN)
            .apply { addCategory(Intent.CATEGORY_LAUNCHER) }

        val pm = activity!!.packageManager
        val activities = pm.queryIntentActivities(startupIntent, 0)
        activities.sortWith(Comparator { a, b ->
            val manager: PackageManager = activity!!.packageManager
            String.CASE_INSENSITIVE_ORDER.compare(
                a?.loadLabel(manager).toString(),
                b?.loadLabel(manager).toString()
            )
        })

        Log.i(TAG, "Found ${activities.size} activities")
        mRecyclerView.adapter = ActivityAdapter(activities)
    }

    private inner class ActivityHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private lateinit var mResolveInfo: ResolveInfo
        private var mAppName: TextView = itemView.findViewById(R.id.app_name)
        private var mAppIcon: ImageView = itemView.findViewById(R.id.app_icon)

        init {
            mAppName.setOnClickListener(this)
        }

        fun bindActivity(resolveInfo: ResolveInfo) {
            mResolveInfo = resolveInfo
            val pm = activity!!.packageManager
            val appName = mResolveInfo.loadLabel(pm).toString()
            val appIcon = mResolveInfo.loadIcon(pm)
            mAppName.text = appName
            mAppIcon.setImageDrawable(appIcon)
        }

        override fun onClick(v: View?) {
            val activityInfo = mResolveInfo.activityInfo
            val intent = Intent(Intent.ACTION_MAIN)
                .setClassName(
                    activityInfo.applicationInfo.packageName,
                    activityInfo.name
                )
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private inner class ActivityAdapter(activities: List<ResolveInfo>) : RecyclerView.Adapter<ActivityHolder>() {

        private var mActivities: List<ResolveInfo> = activities

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityHolder {
            val layoutInflater = LayoutInflater.from(activity)
                .inflate(R.layout.nerd_launcher_item, parent, false)
            return ActivityHolder(layoutInflater)
        }

        override fun getItemCount(): Int {
            return mActivities.size
        }

        override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
            val resolveInfo = mActivities[position]
            holder.bindActivity(resolveInfo)

        }
    }
}