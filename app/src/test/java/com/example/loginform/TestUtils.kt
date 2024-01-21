package com.example.loginform

import com.example.loginform.Model.Customer
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import io.mockk.every
import io.mockk.mockk

inline fun <reified T> mockTask(result: T?, exception: Exception? = null): Task<T> {
    val task: Task<T> = mockk(relaxed = true)
    every { task.isComplete } returns true
    every { task.exception } returns exception
    every { task.isCanceled } returns false
    val relaxedT: T = mockk(relaxed = true)
    every { task.result } returns result
    return task
}

inline fun <T : Any?> mockQuery(clazz: Class<T>, data: List<T>) = mockk<Query> {
    every { get() } returns mockTask(mockk {
        every { isEmpty } returns data.isEmpty()
        every { toObjects(clazz) } returns data.toMutableList()
    })
}

inline fun <T : Any?> mockQuerySnapshot(clazz: Class<T>, data: List<T>) = mockk<QuerySnapshot> {
    every { toObjects(clazz) } returns data
}
