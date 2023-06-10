package com.denchic45.studiversity.firebasemultiplatform.ktor

import com.denchic45.studiversity.firebasemultiplatform.api.Commit
import com.denchic45.studiversity.firebasemultiplatform.api.DocumentMask
import com.denchic45.studiversity.firebasemultiplatform.api.DocumentRequest
import com.denchic45.studiversity.firebasemultiplatform.api.Request
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend inline fun HttpClient.runQuery(request: Request): HttpResponse = post {
    url(PathReference().operation("runQuery").url)
    contentType(ContentType.Application.Json)
    setBody(request)
}

suspend inline fun HttpClient.commit(commit: Commit): HttpResponse = post {
    url(PathReference().operation("commit").url)
    contentType(ContentType.Application.Json)
    setBody(commit)
}

suspend inline fun HttpClient.getDocument(
    documentReference: PathReference.() -> DocumentReference
): HttpResponse = get(documentReference(PathReference()).url)

suspend inline fun HttpClient.patchDocument(
    documentReference: PathReference.() -> DocumentReference,
    document: DocumentRequest,
    updateMask: DocumentMask?
): HttpResponse = patch(documentReference(PathReference()).url) {
    addParameters(updateMask)
    contentType(ContentType.Application.Json)
    setBody(document)
}

fun HttpRequestBuilder.addParameters(updateMask: DocumentMask?) {
    updateMask?.let {
        updateMask.fieldPaths.forEach { fieldPath ->
            parameter("updateMask.fieldPaths", fieldPath)
        }
    }
}