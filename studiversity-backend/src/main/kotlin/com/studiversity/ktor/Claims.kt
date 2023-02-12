package com.studiversity.ktor

import com.auth0.jwt.interfaces.Payload
import com.studiversity.util.toUUID
import java.util.*

val Payload.claimId: UUID
    get() = getClaim("sub").asString().toUUID()
