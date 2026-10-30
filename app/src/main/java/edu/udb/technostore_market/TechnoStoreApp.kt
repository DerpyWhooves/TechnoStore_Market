package edu.udb.technostore_market

import android.app.Application
import edu.udb.technostore_market.di.AppContainer

class TechnoStoreApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
