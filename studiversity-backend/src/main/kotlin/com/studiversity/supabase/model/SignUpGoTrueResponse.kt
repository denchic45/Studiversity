package com.studiversity.supabase.model

import com.stuiversity.util.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class SignUpGoTrueResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("refresh_token") val refreshToken: String,
    val user: UserGoTrue
)

@Serializable
data class AppMetadata(

    val provider: String? = null,
    val providers: ArrayList<String> = arrayListOf()

)

@Serializable
data class IdentityData(
    val email: String? = null,
    val sub: String? = null
)

@Serializable
data class Identities(
    val id: String? = null,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("identity_data") val identityData: IdentityData? = IdentityData(),
    val provider: String? = null,
    @SerialName("last_sign_in_at") val lastSignInAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null

)


@Serializable
data class UserGoTrue(
    @SerialName("id") @Serializable(with = UUIDSerializer::class) val id: UUID,
    @SerialName("aud") val aud: String,
    @SerialName("role") val role: String,
    @SerialName("email") val email: String? = null,
    @SerialName("email_confirmed_at") val emailConfirmedAt: String? = null,
    @SerialName("phone") val phone: String? = null,
    @SerialName("last_sign_in_at") val lastSignInAt: String? = null,
    @SerialName("app_metadata") val appMetadata: AppMetadata? = AppMetadata(),
    @SerialName("user_metadata") val userMetadata: UserMetadata? = UserMetadata(),
    @SerialName("identities") val identities: ArrayList<Identities> = arrayListOf(),
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
class UserMetadata