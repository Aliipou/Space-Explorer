package com.example.spaceexplorer

import android.app.Application
import com.example.spaceexplorer.api.RetrofitClient
import com.example.spaceexplorer.ui.viewmodels.SpaceViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * Application class for initializing app-wide components
 */
class SpaceExplorerApp : Application() {

    // Define Koin module for dependency injection
    private val appModule = module {
        // API Service
        single { RetrofitClient.createNasaApiService() }
        
        // ViewModels
        viewModel { SpaceViewModel(get()) }
    }
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin for dependency injection
        startKoin {
            androidLogger()
            androidContext(this@SpaceExplorerApp)
            modules(appModule)
        }
    }
}