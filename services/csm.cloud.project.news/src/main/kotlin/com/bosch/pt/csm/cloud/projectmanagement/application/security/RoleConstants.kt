/*
 * ************************************************************************
 *
 *  Copyright:       Robert Bosch Power Tools GmbH, 2018 - 2022
 *
 * ************************************************************************
 */
package com.bosch.pt.csm.cloud.projectmanagement.application.security

enum class RoleConstants {
  ADMIN,
  USER;

  fun roleName(): String = "ROLE_$name"
}
