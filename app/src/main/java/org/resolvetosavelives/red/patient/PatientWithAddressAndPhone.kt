package org.resolvetosavelives.red.patient

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Query
import android.arch.persistence.room.Relation
import android.arch.persistence.room.Transaction
import io.reactivex.Flowable
import org.intellij.lang.annotations.Language
import org.resolvetosavelives.red.patient.sync.PatientAddressPayload
import org.resolvetosavelives.red.patient.sync.PatientPayload
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import java.util.UUID

data class PatientWithAddressAndPhone(

    val uuid: UUID,

    val fullName: String,

    val gender: Gender,

    val dateOfBirth: LocalDate?,

    @Embedded(prefix = "age_")
    val age: Age?,

    val status: PatientStatus,

    val createdAt: Instant,

    val updatedAt: Instant,

    val syncStatus: SyncStatus,

    @Embedded(prefix = "address_")
    val address: PatientAddress
) {

  @Relation(parentColumn = "uuid", entityColumn = "patientUuid")
  var phoneNumbers: List<PatientPhoneNumber>? = null

  fun toPayload(): PatientPayload {
    // TODO: Add phone numbers to this payload
    return PatientPayload(
        uuid = uuid,
        fullName = fullName,
        gender = gender,
        dateOfBirth = dateOfBirth,
        age = age?.value,
        ageUpdatedAt = age?.updatedAt,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,
        address = with(address) {
          PatientAddressPayload(
              uuid = uuid,
              colonyOrVillage = colonyOrVillage,
              district = district,
              state = state,
              country = country,
              createdAt = createdAt,
              updatedAt = updatedAt
          )
        }
    )
  }

  @Dao
  interface RoomDao {

    companion object {
      @Language("RoomSql")
      const val joinQuery = """
          SELECT P.uuid, P.fullName, P.gender, P.dateOfBirth, P.age_value, P.age_updatedAt, P.status, P.createdAt, P.updatedAt, P.syncStatus,
          PA.uuid address_uuid, PA.colonyOrVillage address_colonyOrVillage, PA.district address_district, PA.state address_state,
          PA.country address_country, PA.createdAt address_createdAt, PA.updatedAt address_updatedAt
          FROM patient P
          INNER JOIN PatientAddress PA on PA.uuid = P.addressUuid
          """
    }

    @Transaction
    @Query("$joinQuery WHERE P.fullName LIKE '%' || :query || '%'")
    fun search(query: String): Flowable<List<PatientWithAddressAndPhone>>

    @Transaction
    @Query(joinQuery)
    fun allRecords(): Flowable<List<PatientWithAddressAndPhone>>

    @Transaction
    @Query("$joinQuery WHERE P.syncStatus == :status")
    fun withSyncStatus(status: SyncStatus): Flowable<List<PatientWithAddressAndPhone>>
  }
}