package com.example.spaceexplorer

import android.app.Application
import com.example.spaceexplorer.api.RetrofitClient
import com.example.spaceexplorer.data.local.SpaceDatabase
import com.example.spaceexplorer.data.remote.RemoteDataSource
import com.example.spaceexplorer.data.repository.SpaceRepository
import com.example.spaceexplorer.data.repository.SpaceRepositoryImpl
import com.example.spaceexplorer.domain.usecase.GetAPODUseCase
import com.example.spaceexplorer.domain.usecase.GetRecentAPODUseCase
import com.example.spaceexplorer.ui.viewmodels.SpaceViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class SpaceExplorerApp : Application() {

    private val dataModule = module {
        // Network
        single { RetrofitClient.createNasaApiService() }
        single { RemoteDataSource(get()) }

        // Local (Room)
        single { SpaceDatabase.getInstance(androidContext()) }
        single { get<SpaceDatabase>().astronomyPictureDao() }

        // Repository
        single<SpaceRepository> { SpaceRepositoryImpl(get(), get()) }
    }

    private val domainModule = module {
        factory { GetAPODUseCase(get()) }
        factory { GetRecentAPODUseCase(get()) }
    }

    private val presentationModule = module {
        viewModel { SpaceViewModel(get(), get()) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@SpaceExplorerApp)
            modules(dataModule, domainModule, presentationModule)
        }
    }
}
