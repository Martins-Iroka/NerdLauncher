package com.martdev.android.nerdlauncher

import androidx.fragment.app.Fragment

class NerdLauncherActivity : SingleFragmentActivity() {

    override fun createFragment(): Fragment {
        return NerdLauncherFragment.newInstance()
    }
}