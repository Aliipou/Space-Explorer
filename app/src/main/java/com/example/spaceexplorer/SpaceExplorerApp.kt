package com.example.spaceexplorer

import android.app.Application
import com.example.spaceexplorer.api.RetrofitClient
import com.example.spaceexplorer.data.auth.AuthApiService
import com.example.spaceexplorer.data.auth.AuthRepository
import com.example.spaceexplorer.data.auth.TokenStore
import com.example.spaceexplorer.data.local.SpaceDatabase
import com.example.spaceexplorer.data.remote.RemoteDataSource
import com.example.spaceexplorer.data.repository.SpaceRepository
import com.example.spaceexplorer.data.repository.SpaceRepositoryImpl
import com.example.spaceexplorer.domain.usecase.GetAPODUseCase
import com.example.spaceexplorer.domain.usecase.GetRecentAPODUseCase
import com.example.spaceexplorer.ui.auth.AuthViewModel
import com.example.spaceexplorer.ui.viewmodels.SpaceViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BACKEND_BASE_URL = "http://10.0.2.2:8000/"  // emulator → localhost; swap for prod URL

class SpaceExplorerApp : Application() {

    private val dataModule = module {
        // NASA (direct)
        single { RetrofitClient.createNasaApiService() }
        single { RemoteDataSource(get()) }

        // Room
        single { SpaceDatabase.getInstance(androidContext()) }
        single { get<SpaceDatabase>().astronomyPictureDao() }

        // Space repository
        single<SpaceRepository> { SpaceRepositoryImpl(get(), get()) }

        // Auth
        single { TokenStore(androidContext()) }
        single {
            Retrofit.Builder()
                .baseUrl(BACKEND_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuthApiService::class.java)
        }
        single { AuthRepository(get(), get()) }
    }

    private val domainModule = module {
        factory { GetAPODUseCase(get()) }
        factory { GetRecentAPODUseCase(get()) }
    }

    private val presentationModule = module {
        viewModel { SpaceViewModel(get(), get()) }
        viewModel { AuthViewModel(get()) }
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
