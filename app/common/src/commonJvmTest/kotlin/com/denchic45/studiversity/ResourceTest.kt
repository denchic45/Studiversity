package com.denchic45.studiversity


import com.denchic45.studiversity.api.GeneralErrors
import com.denchic45.studiversity.domain.resource.*
import com.denchic45.studiversity.util.ErrorInfo
import com.denchic45.studiversity.util.ErrorResponse
import io.ktor.http.*
import kotlin.test.Test

class ResourceTest {

    @Test
    fun testErrorBinding() {
        val res1: Resource<String> = resourceOf("string 1")
        val res2: Resource<String> =
            resourceOf(
                ClientError(
                    ErrorResponse(
                        HttpStatusCode.Forbidden.value,
                        ErrorInfo(GeneralErrors.PERMISSION_DENIED)
                    )
                )
            )
        val bindResources = bindResources {
            res1.bind() + res2.bind()
        }
        assert(bindResources is Resource.Error)
    }

    @Test
    fun testLoadingBinding() {
        val res1: Resource<String> = resourceOf("string 1")
        val res2: Resource<String> = resourceOf()
        val bindResources = bindResources {
            res1.bind() + res2.bind()
        }
        assert(bindResources is Resource.Loading)
    }

    @Test
    fun testSuccessBinding() {
        val res1: Resource<String> = resourceOf("string 1")
        val res2: Resource<String> = resourceOf("string 2")
        val bindResources = bindResources {
            res1.bind() + res2.bind()
        }
        assert(bindResources.success().value == "string 1string 2")
    }

    @Test
    fun testEmptyBinding() {
        val bindResources = bindResources {}
        assert(bindResources is Resource.Success)
    }
}