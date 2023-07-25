package com.denchic45.studiversity

import com.denchic45.studiversity.domain.resource.Forbidden
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.bindResources
import com.denchic45.studiversity.domain.resource.resourceOf
import com.denchic45.studiversity.domain.resource.success
import kotlin.test.Test

class ResourceTest {

    @Test
    fun testErrorBinding() {
        val res1: Resource<String> = resourceOf("string 1")
        val res2: Resource<String> = resourceOf(Forbidden)
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
        val bindResources = bindResources {
        }
        assert(bindResources is Resource.Success)
    }
}