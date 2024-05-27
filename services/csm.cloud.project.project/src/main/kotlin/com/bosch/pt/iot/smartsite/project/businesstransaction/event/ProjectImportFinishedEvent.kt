/*
 * ************************************************************************
 *
 *  Copyright:       Robert Bosch Power Tools GmbH, 2018 - 2022
 *
 * ************************************************************************
 */

package com.bosch.pt.iot.smartsite.project.businesstransaction.event

import com.bosch.pt.csm.cloud.common.model.key.BusinessTransactionFinishedMessageKey
import com.bosch.pt.csm.cloud.projectmanagement.importer.messages.ProjectImportFinishedEventAvro
import com.bosch.pt.iot.smartsite.application.security.AuthorizationUtils.getCurrentUser
import com.bosch.pt.iot.smartsite.common.businesstransaction.BusinessTransactionContextHolder.currentBusinessTransactionId
import com.bosch.pt.iot.smartsite.project.project.ProjectId
import com.bosch.pt.iot.smartsite.user.model.User
import java.time.LocalDateTime

class ProjectImportFinishedEvent(
    projectIdentifier: ProjectId,
    val createdDate: LocalDateTime = LocalDateTime.now(),
    val createdBy: User = getCurrentUser()
) : AbstractProjectBusinessTransactionEvent(projectIdentifier) {

  override fun toAvroMessage(): ProjectImportFinishedEventAvro =
      ProjectImportFinishedEventAvro.newBuilder()
          .setAuditingInformation(buildEventAuditingInformation(createdDate, createdBy))
          .build()

  override fun toMessageKey() =
      BusinessTransactionFinishedMessageKey(
          currentBusinessTransactionId()
              ?: error(
                  "Cannot create message key because no active business transaction was found."),
          projectIdentifier.toUuid())
}
