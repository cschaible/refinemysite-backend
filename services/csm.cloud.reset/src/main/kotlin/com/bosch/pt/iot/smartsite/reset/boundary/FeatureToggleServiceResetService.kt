/*
 * ************************************************************************
 *
 *  Copyright:       Robert Bosch Power Tools GmbH, 2018 - 2022
 *
 * ************************************************************************
 */

package com.bosch.pt.iot.smartsite.reset.boundary

import com.bosch.pt.iot.smartsite.reset.Resettable
import com.bosch.pt.iot.smartsite.reset.jdbc.JdbcResetStrategy
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.stereotype.Service

@Service
class FeatureToggleServiceResetService(
    jdbcResetStrategy: JdbcResetStrategy,
    @Qualifier("featuretoggleJdbcTemplate") jdbcOperations: JdbcOperations
) :
    AbstractRelationalDatabaseResetService(
        jdbcResetStrategy, jdbcOperations, "featuretoggle-service"),
    Resettable
