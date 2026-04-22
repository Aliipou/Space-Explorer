package com.example.spaceexplorer

import com.example.spaceexplorer.domain.model.SpaceResult
import com.example.spaceexplorer.domain.model.onError
import com.example.spaceexplorer.domain.model.onLoading
import com.example.spaceexplorer.domain.model.onSuccess
import org.junit.Assert.*
import org.junit.Test

class SpaceResultTest {

    @Test
    fun `Loading state is SpaceResult_Loading`() {
        val result: SpaceResult<String> = SpaceResult.Loading
        assertTrue(result is SpaceResult.Loading)
    }

    @Test
    fun `Success holds data and fromCache flag`() {
        val result = SpaceResult.Success("payload", fromCache = true)
        assertEquals("payload", result.data)
        assertTrue(result.fromCache)
    }

    @Test
    fun `Success fromCache defaults to false`() {
        val result = SpaceResult.Success("x")
        assertFalse(result.fromCache)
    }

    @Test
    fun `Error holds message and optional cause`() {
        val cause = RuntimeException("boom")
        val result = SpaceResult.Error("Something went wrong", cause)
        assertEquals("Something went wrong", result.message)
        assertEquals(cause, result.cause)
    }

    @Test
    fun `onSuccess callback fires for Success`() {
        var called = false
        SpaceResult.Success("data").onSuccess { called = true }
        assertTrue(called)
    }

    @Test
    fun `onSuccess callback does not fire for Error`() {
        var called = false
        SpaceResult.Error("fail").onSuccess { called = true }
        assertFalse(called)
    }

    @Test
    fun `onError callback fires for Error`() {
        var msg = ""
        SpaceResult.Error("oops").onError { msg = it }
        assertEquals("oops", msg)
    }

    @Test
    fun `onLoading callback fires for Loading`() {
        var called = false
        SpaceResult.Loading.onLoading { called = true }
        assertTrue(called)
    }

    @Test
    fun `chaining returns the same result`() {
        val result = SpaceResult.Success("chained")
        val same = result.onSuccess { }.onError { }
        assertSame(result, same)
    }
}
