/*
 * ************************************************************************
 *
 *  Copyright:       Robert Bosch Power Tools GmbH, 2018 - 2021
 *
 * ************************************************************************
 */
package com.bosch.pt.csm.common

import com.bosch.pt.csm.application.SmartSiteSpringBootTest
import com.bosch.pt.csm.application.config.EnableAllKafkaListeners
import com.bosch.pt.csm.cloud.common.facade.rest.ApiVersionProperties
import com.bosch.pt.csm.common.facade.AbstractIntegrationTest
import org.springframework.beans.factory.annotation.Autowired

@EnableAllKafkaListeners
@SmartSiteSpringBootTest
abstract class AbstractApiIntegrationTest : AbstractIntegrationTest() {

  @Autowired protected lateinit var apiVersionProperties: ApiVersionProperties
}
