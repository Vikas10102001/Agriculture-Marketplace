package com.example.farmi.di



import com.example.farmi.AiAssistant.data.api.ChatgptApi
import com.example.farmi.AddProduct.repository.AddProductRepository
import com.example.farmi.AiAssistant.data.api.GptDto

import com.example.farmi.AiAssistant.data.api.GptDtoImpl
import com.example.farmi.AiAssistant.repository.GptRepository
import com.example.farmi.MandiPrices.data.api.MandiApi

import com.example.farmi.authentication.repository.UserLoginRepository
import com.example.farmi.authentication.repository.UserRegisterRepository
import com.example.farmi.repositories.EditProductRepo
import com.example.farmi.repositories.GetAllProductsRepo
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.Interceptor

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth()= FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestoreDatabase()= Firebase.firestore

    @Provides
    @Singleton
    fun provideFirebaseStorage()= Firebase.storage

    @Singleton
    @Provides
    fun provideUserRepository(firebaseAuth: FirebaseAuth, firestore: FirebaseFirestore): UserRegisterRepository {
        return UserRegisterRepository(firebaseAuth, firestore)
    }
    @Singleton
    @Provides
    fun provideUserLoginRepository(firebaseAuth: FirebaseAuth): UserLoginRepository {
        return UserLoginRepository(firebaseAuth)
    }
    @Singleton
    @Provides
    fun providesAddProductRepository(firestore: FirebaseFirestore, firebaseStorage: FirebaseStorage):AddProductRepository{
        return AddProductRepository(firestore,firebaseStorage)
    }
    @Singleton
    @Provides
    fun providesGetAllProductRepo(firestore: FirebaseFirestore,firebaseAuth: FirebaseAuth):GetAllProductsRepo{
        return GetAllProductsRepo(firestore,firebaseAuth)
    }

    @Singleton
    @Provides
    fun providesEditProductRepo(firestore: FirebaseFirestore):EditProductRepo{
        return EditProductRepo(firestore)
    }
    // ai assistant

    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }


        val apiKeyInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer <your api key>")
                .build()
            chain.proceed(request)
        }

        val httpClient = OkHttpClient.Builder().apply {
            addInterceptor(httpLoggingInterceptor)
            addInterceptor(apiKeyInterceptor)
            readTimeout(60, TimeUnit.SECONDS)
        }
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        httpClient.interceptors().add(logging)
        val moshi = Moshi.Builder()
            .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(httpClient.build())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }


    @Provides
    @Singleton
    fun providesApiService( retrofit: Retrofit): ChatgptApi {
        return retrofit.create(ChatgptApi::class.java)

    }

    @Provides
    @Singleton
    fun providesGptDto(apiService: ChatgptApi) : GptDto {
        return GptDtoImpl(apiService)
    }
    @Provides
    @Singleton
    fun providesRepository(gptDto: GptDto): GptRepository {
        return GptRepository(gptDto)
    }

    // marketprice
    @Provides
    @Singleton
    fun providesMarketPriceApiService(retrofit: Retrofit):MandiApi{
        return retrofit.create(MandiApi::class.java)
    }
//    @Provides
//    @Singleton
//    fun proveFireBaseCommon(
//        firebaseAuth: FirebaseAuth,
//        firestore: FirebaseFirestore
//    )= FirebaseCommon(firestore,firebaseAuth)
}