package com.example.loginform.di

import android.content.Context
import com.example.loginform.data.PrefRepository
import com.example.loginform.data.authentication.AuthRepository
import com.example.loginform.data.authentication.AuthRepositoryImpl
import com.example.loginform.data.cartrepository.CartRepository
import com.example.loginform.data.cartrepository.CartRepositoryImpl
import com.example.loginform.data.orders.OrdersRepository
import com.example.loginform.data.orders.OrdersRepositoryImpl
import com.example.loginform.data.productsrepository.ProductRepository
import com.example.loginform.data.productsrepository.ProductsRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepositoryImp(
        prefRepository: PrefRepository,
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): AuthRepository = AuthRepositoryImpl(prefRepository, firebaseAuth, firestore)

    @Provides
    @Singleton
    fun provideProductRepoImpl(
        firestore: FirebaseFirestore,
        firebaseStorage: FirebaseStorage,
        @ApplicationContext context: Context,
        prefRepository: PrefRepository
    ): ProductRepository = ProductsRepositoryImpl(firestore, firebaseStorage.reference, context,prefRepository)

    @Provides
    @Singleton
    fun provideOrdersRepositoryImpl(
        firestore: FirebaseFirestore,
        authRepository: AuthRepository,
        productRepository: ProductRepository,
        prefRepository: PrefRepository
    ): OrdersRepository = OrdersRepositoryImpl(firestore, authRepository, productRepository,prefRepository)

    @Provides
    @Singleton
    fun provideCartRepositoryImpl(
        firestore: FirebaseFirestore,
        authRepository: AuthRepository,
    ): CartRepository = CartRepositoryImpl(firestore, authRepository)

}