package com.denchic45.kts.data.repository

import com.denchic45.kts.data.service.NetworkService

interface NetworkServiceOwner {
    val networkService: NetworkService
}