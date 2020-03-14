package com.droid47.petgoogle.search.data.datasource

import com.droid47.petgoogle.search.data.models.PetResponseEntity
import com.droid47.petgoogle.search.data.models.search.SearchResponseEntity
import com.droid47.petgoogle.search.data.models.type.BreedResponseEntity
import com.droid47.petgoogle.search.data.models.type.PetTypeResponseEntity
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface SearchNetworkSource {

    @GET("/v2/animals")
    fun getPets(@QueryMap(encoded = true) options: Map<String, @JvmSuppressWildcards Any>): Single<SearchResponseEntity>

    @GET("/v2/types")
    fun getPetTypes(): Single<PetTypeResponseEntity>

    @GET("/v2/types/{type}/breeds")
    fun getBreeds(@Path("type") petType: String): Single<BreedResponseEntity>

    @GET("/v2/animals/{id}")
    fun getPetDetails(@Path("id") petId: Int): Single<PetResponseEntity>
}