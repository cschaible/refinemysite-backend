/*
 * ************************************************************************
 *
 *  Copyright:       Robert Bosch Power Tools GmbH, 2018 - 2022
 *
 * ************************************************************************
 */

package com.bosch.pt.iot.smartsite.project.participant.mail.template

import com.bosch.pt.csm.cloud.common.model.IsoCountryCodeEnum
import com.bosch.pt.csm.cloud.common.test.event.EventStreamGeneratorStaticExtensions.Companion.getIdentifier
import com.bosch.pt.csm.cloud.companymanagement.company.event.submitCompanyWithBothAddresses
import com.bosch.pt.csm.cloud.companymanagement.company.event.submitCompanyWithPostboxAddress
import com.bosch.pt.csm.cloud.projectmanagement.project.event.submitParticipantG3
import com.bosch.pt.csm.cloud.usermanagement.user.event.submitUser
import com.bosch.pt.iot.smartsite.application.config.EnableAllKafkaListeners
import com.bosch.pt.iot.smartsite.application.config.MailProperties
import com.bosch.pt.iot.smartsite.application.config.MailjetPort
import com.bosch.pt.iot.smartsite.common.event.setupDatasetTestData
import com.bosch.pt.iot.smartsite.common.facade.rest.AbstractIntegrationTestV2
import com.bosch.pt.iot.smartsite.project.participant.command.sideeffects.ParticipantMailService
import com.bosch.pt.iot.smartsite.project.participant.facade.job.InvitationExpirationJob.Companion.EXPIRE_AFTER_DAYS
import com.bosch.pt.iot.smartsite.project.participant.mail.template.ParticipantInvitedTemplate.Companion.EXPIRATION_DATE
import com.bosch.pt.iot.smartsite.project.participant.mail.template.ParticipantInvitedTemplate.Companion.ORIGINATOR_NAME
import com.bosch.pt.iot.smartsite.project.participant.mail.template.ParticipantInvitedTemplate.Companion.PROJECT_NAME
import com.bosch.pt.iot.smartsite.project.participant.mail.template.ParticipantInvitedTemplate.Companion.REGISTER_URL
import com.bosch.pt.iot.smartsite.project.participant.mail.template.ParticipantInvitedTemplate.Companion.TEMPLATE_NAME
import com.bosch.pt.iot.smartsite.project.project.asProjectId
import com.bosch.pt.iot.smartsite.util.bcc
import com.bosch.pt.iot.smartsite.util.mailjetSuccess
import com.bosch.pt.iot.smartsite.util.recipient
import com.bosch.pt.iot.smartsite.util.respondWithSuccess
import com.bosch.pt.iot.smartsite.util.templateId
import com.bosch.pt.iot.smartsite.util.variables
import java.time.LocalDateTime.now
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("Verify participant invitation mail B1 (user invited to register)")
@EnableAllKafkaListeners
open class ParticipantMailServiceIntegrationParticipantInvitedTest : AbstractIntegrationTestV2() {

  @Autowired private lateinit var mailjetPort: MailjetPort
  @Autowired private lateinit var cut: ParticipantMailService
  @Autowired private lateinit var mailProperties: MailProperties

  private val templateInvitedProperties by lazy { mailProperties.templates[TEMPLATE_NAME]!! }

  private val expirationDate = now().plusDays(EXPIRE_AFTER_DAYS)

  private val mockServer by lazy { MockWebServer().apply { start(mailjetPort.value) } }

  private val invitedParticipant by lazy {
    repositories.findParticipant(getIdentifier("invitedParticipant"))!!
  }
  private val invitingUser by lazy { repositories.findUser(getIdentifier("userCsm1"))!! }
  private val invitingParticipant by lazy {
    repositories.findParticipant(invitingParticipantIdentifier)!!
  }
  private val invitingParticipantIdentifier by lazy { getIdentifier("participantCsm1") }
  private val project by lazy { repositories.findProject(getIdentifier("project").asProjectId())!! }

  @BeforeEach
  fun setup() {
    eventStreamGenerator
        .setupDatasetTestData()
        .setUserContext("userCsm1")
        .submitUser("invitedUser")
        .submitParticipantG3("invitedParticipant")

    setAuthentication("userCsm1")
    projectEventStoreUtils.reset()
    mockServer.respondWithSuccess()
  }

  @AfterEach fun tearDown() = mockServer.shutdown()

  @Test
  fun `is sent to invited participant`() {
    transactionTemplate.execute {
      cut.sendParticipantInvited(
          project.identifier, invitedParticipant.email!!, expirationDate, invitingParticipant)
    }
    val recipient = mockServer.takeRequest().recipient()

    assertThat(recipient.getString("Email")).isEqualTo(invitedParticipant.user!!.email)
  }

  @Test
  fun `is sent in bcc to configured bcc mail address`() {
    transactionTemplate.execute {
      cut.sendParticipantInvited(
          project.identifier, invitedParticipant.email!!, expirationDate, invitingParticipant)
    }
    val bcc = mockServer.takeRequest().bcc()

    assertThat(bcc.getString("Email")).isEqualTo("bcc@example.com")
    assertThat(bcc.has("Name")).isFalse
  }

  @Test
  fun `has correct variables`() {
    transactionTemplate.execute {
      cut.sendParticipantInvited(
          project.identifier, invitedParticipant.email!!, expirationDate, invitingParticipant)
    }

    val variables = mockServer.takeRequest().variables()

    assertThat(variables.getString(ORIGINATOR_NAME)).isEqualTo(invitingUser.getDisplayName())
    assertThat(variables.getString(PROJECT_NAME)).isEqualTo(project.getDisplayName())
    assertThat(variables.getString(REGISTER_URL)).isEqualTo("https://localhost/auth/register")
    assertThat(variables.getJSONObject(EXPIRATION_DATE)).isNotNull
  }

  @TestFactory
  fun `uses the correct template id for country code`() =
      templateInvitedProperties.countries.map {
        val countryCode = it.key
        val expectedTemplateId = it.value

        // create one test for each supported country
        dynamicTest(countryCode) {
          mockServer.enqueue(MockResponse().mailjetSuccess())
          val countryName = IsoCountryCodeEnum.fromCountryCode(countryCode)!!.countryName

          // because the mail language is determined by the country of the originator (who is
          // sending the invitation), we build a company located in the country to be tested
          eventStreamGenerator.submitCompanyWithPostboxAddress {
            it.postBoxAddressBuilder.country = countryName
          }

          val invitingParticipant = repositories.findParticipant(invitingParticipantIdentifier)!!

          transactionTemplate.execute {
            cut.sendParticipantInvited(
                project.identifier, invitedParticipant.email!!, expirationDate, invitingParticipant)
          }
          val request = mockServer.takeRequest()

          assertThat(request.templateId()).isEqualTo(expectedTemplateId)
        }
      }

  @Test
  fun `uses default template for unsupported countries`() {
    val defaultTemplateId = templateInvitedProperties.default
    eventStreamGenerator.submitCompanyWithPostboxAddress { company ->
      company.postBoxAddressBuilder.country = "Greenland"
    }

    transactionTemplate.execute {
      cut.sendParticipantInvited(
          project.identifier, invitedParticipant.email!!, expirationDate, invitingParticipant)
    }
    val request = mockServer.takeRequest()

    assertThat(request.templateId()).isEqualTo(defaultTemplateId)
  }

  @Test
  fun `uses template based on country of street address if given`() {
    val expectedTemplateId = templateInvitedProperties.countries["DE"]!!
    eventStreamGenerator.submitCompanyWithBothAddresses {
      it.streetAddressBuilder.country = "Germany"
      it.postBoxAddressBuilder.country = "United States of America (the)"
    }

    transactionTemplate.execute {
      cut.sendParticipantInvited(
          project.identifier, invitedParticipant.email!!, expirationDate, invitingParticipant)
    }
    val request = mockServer.takeRequest()

    assertThat(request.templateId()).isEqualTo(expectedTemplateId)
  }

  @Test
  fun `uses template based on country of post box address if no street address is given`() {
    val expectedTemplateId = templateInvitedProperties.countries["US"]!!
    eventStreamGenerator.submitCompanyWithPostboxAddress {
      it.postBoxAddressBuilder.country = "United States of America (the)"
    }

    transactionTemplate.execute {
      cut.sendParticipantInvited(
          project.identifier, invitedParticipant.email!!, expirationDate, invitingParticipant)
    }
    val request = mockServer.takeRequest()

    assertThat(request.templateId()).isEqualTo(expectedTemplateId)
  }
}
