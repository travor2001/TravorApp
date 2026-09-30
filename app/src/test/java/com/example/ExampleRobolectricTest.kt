package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppRepository
import com.example.model.PaymentMethod
import com.example.model.RideCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("MOTO GO", appName)
  }

  @Test
  fun `verify fare calculation in Luanda`() {
    val origin = AppRepository.luandaLocations[0] // Maianga
    val dest = AppRepository.luandaLocations[2] // Talatona
    val (dist, time, fare) = AppRepository.calculateEstimate(origin, dest, RideCategory.ECONOMICA)

    assertTrue("Distance should be greater than 0", dist > 0.0)
    assertTrue("Time should be greater than 0", time > 0)
    assertTrue("Fare should be at least base fare in Kz", fare >= 400.0)
  }

  @Test
  fun `verify ride request lifecycle and code generation`() {
    val origin = AppRepository.luandaLocations[0]
    val dest = AppRepository.luandaLocations[1]
    val trip = AppRepository.requestRide(origin, dest, RideCategory.ECONOMICA, PaymentMethod.CASH)

    assertNotNull(trip)
    assertEquals(4, trip.tripCode.length)
    assertTrue("Should verify trip code correctly", AppRepository.verifyCodeAndStartRide(trip.tripCode))
  }
}

