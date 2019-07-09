package org.dimensinfin.eveonline.neocom.auth.mock

import org.junit.Assert
import org.junit.Test

class MockInterceptorTest {
	@Test
	fun matches_true() {
		var interceptor = MockInterceptor()
		Assert.assertTrue(interceptor.matches("test string with matchA and matchB", "matchA", "matchB"))
	}

	@Test
	fun matches_false() {
		var interceptor = MockInterceptor()
		Assert.assertFalse(interceptor.matches("test string without matches", "matchA", "matchB"))
	}

	@Test
	fun matches_false_single() {
		var interceptor = MockInterceptor()
		Assert.assertFalse(interceptor.matches("test string without first matchB", "matchA", "matchB"))
	}

	@Test
	fun matchRequest() {
		var interceptor = MockInterceptor()
		var expected = expected_matchRequest
		var obtained = interceptor.matchRequest("https://localhost:8448/characters/92002067/planets?datasource=tranquility")
		Assert.assertEquals("Check that the request content is found", expected, obtained)
	}
}
const val expected_matchRequest = """
[
  {
    "last_update": "2019-04-20T23:56:39Z",
    "num_pins": 11,
    "owner_id": 92223647,
    "planet_id": 40208073,
    "planet_type": "barren",
    "solar_system_id": 30003280,
    "upgrade_level": 4
  }
]
"""
