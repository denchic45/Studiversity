package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.data.service.NetworkService

interface NetworkServiceOwner {
    val networkService: NetworkService
}