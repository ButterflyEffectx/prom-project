package com.hadirahimi.passwordchecker

import android.app.Application
import android.content.Context

class PasswordCheckerApp : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"))
    }
}