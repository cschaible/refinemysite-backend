/*
 * ************************************************************************
 *
 *  Copyright:       Robert Bosch Power Tools GmbH, 2018 - 2023
 *
 * ************************************************************************
 */

package com.bosch.pt.iot.smartsite.project.workarea.domain

import com.bosch.pt.csm.cloud.common.api.UuidIdentifiable
import com.bosch.pt.csm.cloud.common.extensions.toUUID
import com.bosch.pt.csm.cloud.common.messages.AggregateIdentifierAvro
import com.bosch.pt.csm.cloud.projectmanagement.common.ProjectmanagementAggregateTypeEnum.WORKAREALIST
import com.fasterxml.jackson.annotation.JsonValue
import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.UUID
import java.util.UUID.randomUUID

@Embeddable
data class WorkAreaListId(@get:JsonValue override val identifier: UUID = randomUUID()) :
    Serializable, UuidIdentifiable {

  constructor(identifier: String) : this(identifier.toUUID())

  override fun toString(): String = identifier.toString()

  companion object {
    private const val serialVersionUID: Long = 879917313254L
  }
}

fun UUID.asWorkAreaListId() = WorkAreaListId(this)

fun String.asWorkAreaListId() = WorkAreaListId(this)

fun WorkAreaListId.toAggregateReference(): AggregateIdentifierAvro =
    AggregateIdentifierAvro.newBuilder()
        .setType(WORKAREALIST.name)
        .setIdentifier(identifier.toString())
        .setVersion(0)
        .build()
