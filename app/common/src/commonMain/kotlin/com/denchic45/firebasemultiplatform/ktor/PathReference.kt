package com.denchic45.firebasemultiplatform.ktor



class PathReference : CollectionReference, DocumentReference, OperationReference {
    private val firebaseProjectId:String = TODO()
    private val segments: MutableList<String> =
        mutableListOf("projects/${firebaseProjectId}/databases/(default)/documents")

    override fun collection(name: String): CollectionReference = apply {
        append("/$name")
    }

    override val path: String
        get() = segments.joinToString(separator = "")

    override fun document(name: String): DocumentReference = apply {
        append("/$name")
    }

    fun operation(operationName: String): OperationReference = apply {
        append(":$operationName")
    }

    private fun append(segment: String) {
        segments.add(segment)
    }

    override val url: String
        get() = "https://firestore.googleapis.com/v1/$path"

    override fun toString(): String = path
}

interface HasUrl {
    val url: String
}

interface Reference : HasUrl {
    val path: String
}

interface OperationReference : HasUrl

interface CollectionReference : Reference {
    fun document(name: String): DocumentReference
}

interface DocumentReference : Reference {
    fun collection(name: String): CollectionReference
}