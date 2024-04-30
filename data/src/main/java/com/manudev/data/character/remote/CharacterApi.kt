package com.manudev.data.character.remote

import com.manudev.data.character.remote.model.CharacterDataContainer
import com.manudev.data.character.remote.model.CharacterDataWrapper
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CharacterApi {

    @GET("/v1/public/characters")
    suspend fun getAllCharacters(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Response<CharacterDataWrapper>
    @GET("/v1/public/characters/{characterId}")
    suspend fun getCharacterById(
        @Path("characterId") characterId: Int,
    ): Response<CharacterDataWrapper>

    @GET("/v1/public/characters")
    suspend fun getCharacterByStartName(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("nameStartsWith") nameStartsWith: String,
    ): Response<CharacterDataWrapper>
}