package org.dimensinfin.eveonline.neocom.auth.mock

import org.junit.Assert
import org.junit.Test

class MockInterceptorTest {
	@Test
	fun matches_onevalue_true() {
		var interceptor = MockInterceptor()
		Assert.assertTrue(interceptor.matches("test string with matchA and matchB", "matchA"))
	}

	@Test
	fun matches_onevalue_false() {
		var interceptor = MockInterceptor()
		Assert.assertFalse(interceptor.matches("test string with matchA and matchB", "matchC"))
	}

	@Test
	fun matches_twovalues_true() {
		var interceptor = MockInterceptor()
		Assert.assertTrue(interceptor.matches("test string with matchA and matchB",
				"matchA", "matchB"))
	}

	@Test
	fun matches_twovalues_false() {
		var interceptor = MockInterceptor()
		Assert.assertFalse(interceptor.matches("test string with matchA and matchB",
				"matchA", "matchC"))
	}

	@Test
	fun matches_threevalues_true() {
		var interceptor = MockInterceptor()
		Assert.assertTrue(interceptor.matches("test string with matchA and matchB and matchC",
				"matchA", "matchB", "matchC"))
	}

	@Test
	fun matches_threevalues_false() {
		var interceptor = MockInterceptor()
		Assert.assertFalse(interceptor.matches("test string with matchA and matchB and matchC",
				"matchA", "matchD", "matchC"))
	}

	@Test
	fun matches_true() {
		var interceptor = MockInterceptor()
		Assert.assertTrue(interceptor.matches("test string with matchA and matchB", "matchA", "matchB"))
	}

	@Test
	fun matches_fourvalues_true() {
		var interceptor = MockInterceptor()
		Assert.assertTrue(interceptor.matches("test string with matchA and matchB and matchC and matchD",
				"matchA", "matchB", "matchC", "matchD"))
	}

	@Test
	fun matches_fourvalues_false1() {
		var interceptor = MockInterceptor()
		Assert.assertFalse(interceptor.matches("test string with matchA and matchB and matchC and matchD",
				"matchZ", "matchB", "matchC", "matchD"))
	}

	@Test
	fun matches_fourvalues_false2() {
		var interceptor = MockInterceptor()
		Assert.assertFalse(interceptor.matches("test string with matchA and matchB and matchC and matchD",
				"matchA", "matchY", "matchC", "matchD"))
	}

	@Test
	fun matches_fourvalues_false3() {
		var interceptor = MockInterceptor()
		Assert.assertFalse(interceptor.matches("test string with matchA and matchB and matchC and matchD",
				"matchA", "matchY", "matchZ", "matchD"))
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
		var obtained = interceptor.matchRequest("https://localhost:8448/characters/93813310/planets?datasource=tranquility")
		Assert.assertEquals("Check that the request content is found", expected, obtained)
	}
}

const val expected_matchRequest = """
[
  {
    "last_update": "2019-04-21T00:00:54Z",
    "num_pins": 11,
    "owner_id": 93813310,
    "planet_id": 40208303,
    "planet_type": "barren",
    "solar_system_id": 30003283,
    "upgrade_level": 4
  },
  {
    "last_update": "2019-04-21T00:01:07Z",
    "num_pins": 11,
    "owner_id": 93813310,
    "planet_id": 40208304,
    "planet_type": "plasma",
    "solar_system_id": 30003283,
    "upgrade_level": 4
  },
  {
    "last_update": "2019-04-21T00:01:23Z",
    "num_pins": 11,
    "owner_id": 93813310,
    "planet_id": 40208351,
    "planet_type": "gas",
    "solar_system_id": 30003283,
    "upgrade_level": 4
  },
  {
    "last_update": "2019-07-14T17:28:58Z",
    "num_pins": 11,
    "owner_id": 93813310,
    "planet_id": 40208380,
    "planet_type": "oceanic",
    "solar_system_id": 30003283,
    "upgrade_level": 4
  }
]
"""
