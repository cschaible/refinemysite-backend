/*
 * ************************************************************************
 *
 *  Copyright:       Robert Bosch Power Tools GmbH, 2018 - 2021
 *
 * ************************************************************************
 */

package com.bosch.pt.iot.smartsite.project.taskconstraint.boundary.dto

import com.bosch.pt.iot.smartsite.project.taskconstraint.model.TaskConstraintEnum

data class TaskConstraintDto(
    val key: TaskConstraintEnum,
    val active: Boolean,
    val name: String? = null
)
