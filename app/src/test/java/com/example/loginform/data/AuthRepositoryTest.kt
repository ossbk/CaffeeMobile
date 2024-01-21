package com.example.loginform.data

import androidx.test.core.app.ApplicationProvider
import com.example.loginform.Model.Customer
import com.example.loginform.data.authentication.AuthRepository
import com.example.loginform.data.authentication.AuthRepositoryImpl
import com.example.loginform.mockQuery
import com.example.loginform.mockTask
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AuthRepositoryTest {
    private val prefRepository: PrefRepository =
        spyk(PrefRepository(ApplicationProvider.getApplicationContext()))
    private val firebaseAuth: FirebaseAuth = mockk()
    private val firebaseFirestore: FirebaseFirestore = mockk()
    private val firebaseCustomerCollection: CollectionReference = mockk()

    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        coEvery { firebaseAuth.signInWithEmailAndPassword(any(), any()) } returns mockTask(null)
        coEvery { firebaseFirestore.collection("Users") } returns firebaseCustomerCollection

        authRepository = AuthRepositoryImpl(prefRepository, firebaseAuth, firebaseFirestore)
    }

    @Test
    fun `login should return success with customer when email and password is correct`() = runTest {
        coEvery {
            firebaseCustomerCollection.whereEqualTo(
                "cusEmail",
                "test@auth.com"
            )
        } returns mockQuery(Customer::class.java, listOf(Customer(cusEmail = "test@auth.com")))

        val result = authRepository.login("test@auth.com", "123")

        assertTrue(result is Resource.Success)

        val data = (result as Resource.Success).data
        assertEquals("test@auth.com", data.cusEmail)

        verify { firebaseAuth.signInWithEmailAndPassword("test@auth.com", "123") }
        verify { prefRepository.currentUser = Gson().toJson(data) }
    }

    @Test
    fun `login should return failure when email and password is invalid`() = runTest {
        coEvery {
            firebaseCustomerCollection.whereEqualTo(
                "cusEmail",
                any()
            )
        } returns mockQuery(Customer::class.java, listOf())

        val result = authRepository.login("wrongid@auth.com", "123")

        assertTrue(result is Resource.Failure)
    }

    @Test
    fun `signUp should return success`() = runTest {
        coEvery {
            firebaseAuth.createUserWithEmailAndPassword(
                "test@auth.com",
                "123"
            )
        } returns mockTask(null)
        coEvery {
            firebaseCustomerCollection.document()
        } returns mockk {
            every { id } returns "t-id"
        }
        coEvery {
            firebaseCustomerCollection.document("t-id")
        } returns mockk {
            every { set(any()) } returns mockTask(null)
        }

        val result = authRepository.signUp(
            Customer(
                cusFullName = "John Doe",
                cusEmail = "test@auth.com",
                cusPassword = "123"
            )
        )

        assertTrue(result is Resource.Success)

        val data = (result as Resource.Success).data
        assertTrue(data)

        verify { firebaseAuth.createUserWithEmailAndPassword("test@auth.com", "123") }
    }

    @Test
    fun `signUp should return failure when create user fails`() = runTest {
        coEvery {
            firebaseAuth.createUserWithEmailAndPassword(
                "test@auth.com",
                "123"
            )
        } throws IllegalArgumentException()

        val result = authRepository.signUp(
            Customer(
                cusFullName = "John Doe",
                cusEmail = "test@auth.com",
                cusPassword = "123"
            )
        )

        assertTrue(result is Resource.Failure)

        verify { firebaseAuth.createUserWithEmailAndPassword("test@auth.com", "123") }
    }
}