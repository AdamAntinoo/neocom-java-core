package org.dimensinfin.eveonline.neocom.auth.mock

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody

/**
 * This will help us to test our networking code while a particular API is not implemented
 * yet on Backend side.
 */
class MockInterceptor : Interceptor {

	override fun intercept(chain: Interceptor.Chain): Response {
		val uri = chain.request().url().uri().toString() // Get the URL requested
		var responseString = matchRequest(uri)
		return Response.Builder()
				.request(chain.request())
				.code(200)
				.protocol(Protocol.HTTP_1_1)
				.message(responseString)
				.body(ResponseBody.create(MediaType.parse("application/json"),
						responseString.toByteArray()))
				.addHeader("content-type", "application/json")
				.build()
	}

	/*
	 * REMEMBER: The order should be from more restricted to less precise predicates.
	 */
	fun matchRequest(uri: String): String {
		return when {
			matches(uri, "characters", "93813310", "planets", "40208303") -> planetDetails40208303
			matches(uri, "characters", "93813310", "planets") -> planetListPericoTuerto93813310
			matches(uri, "characters", "planets", "40237824") -> getMockPlanetAdvancedFactories
			matches(uri, "characters", "planets") -> getMockPlanets
			uri.contains("2561") -> getType2561
			uri.contains("2483") -> getType2483
			else -> {
				""
			}
		}
	}

	fun matches(url: String, match1: String): Boolean {
		if (url.contains(match1)) return true
		return false
	}

	fun matches(url: String, match1: String, match2: String): Boolean {
		if (url.contains(match1) && url.contains(match2)) return true
		return false
	}

	fun matches(url: String, match1: String, match2: String, match3: String): Boolean {
		if (url.contains(match1) && url.contains(match2) && url.contains(match3)) return true
		return false
	}

	fun matches(url: String, match1: String, match2: String, match3: String, match4: String):
			Boolean {
		if (url.contains(match1) && url.contains(match2)
				&& url.contains(match3) && url.contains(match4)) return true
		return false
	}
}

// - M O C K   D A T A
// - Planet List Perico Tuerto : 93813310
const val planetListPericoTuerto93813310 = """
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

// - Planet Details 40208303 Barren planet
const val planetDetails40208303 = """
{
  "links": [
    {
      "destination_pin_id": 1023179687929,
      "link_level": 0,
      "source_pin_id": 1023179683773
    },
    {
      "destination_pin_id": 1023179758278,
      "link_level": 0,
      "source_pin_id": 1023179721292
    },
    {
      "destination_pin_id": 1023179683769,
      "link_level": 0,
      "source_pin_id": 1022000304230
    },
    {
      "destination_pin_id": 1023179687935,
      "link_level": 0,
      "source_pin_id": 1023179683769
    },
    {
      "destination_pin_id": 1023179721303,
      "link_level": 0,
      "source_pin_id": 1022000304230
    },
    {
      "destination_pin_id": 1023179721292,
      "link_level": 0,
      "source_pin_id": 1023179683773
    },
    {
      "destination_pin_id": 1023179721299,
      "link_level": 0,
      "source_pin_id": 1023179683773
    },
    {
      "destination_pin_id": 1023179721301,
      "link_level": 0,
      "source_pin_id": 1022000304230
    },
    {
      "destination_pin_id": 1023179758288,
      "link_level": 0,
      "source_pin_id": 1023179721299
    },
    {
      "destination_pin_id": 1023179683773,
      "link_level": 0,
      "source_pin_id": 1022000304230
    }
  ],
  "pins": [
    {
      "contents": [
        {
          "amount": 470,
          "type_id": 2267
        }
      ],
      "last_cycle_start": "2017-07-29T01:50:34Z",
      "latitude": 0.819389899886,
      "longitude": 0.672961967931,
      "pin_id": 1023179721299,
      "schematic_id": 126,
      "type_id": 2473
    },
    {
      "contents": [
        {
          "amount": 1243,
          "type_id": 2073
        }
      ],
      "last_cycle_start": "2017-07-29T01:50:34Z",
      "latitude": 0.831400934104,
      "longitude": 0.702053646021,
      "pin_id": 1023179721301,
      "schematic_id": 131,
      "type_id": 2473
    },
    {
      "last_cycle_start": "2017-07-29T01:20:34Z",
      "latitude": 0.832326614025,
      "longitude": 0.672718073936,
      "pin_id": 1023179721303,
      "schematic_id": 131,
      "type_id": 2473
    },
    {
      "expiry_time": "2019-04-29T06:00:54Z",
      "extractor_details": {
        "cycle_time": 7200,
        "head_radius": 0.0337600186467,
        "heads": [
          {
            "head_id": 0,
            "latitude": 0.732570619907,
            "longitude": 0.553850417534
          },
          {
            "head_id": 1,
            "latitude": 0.62354742142,
            "longitude": 0.928776447241
          },
          {
            "head_id": 2,
            "latitude": 0.681819198608,
            "longitude": 0.282388407046
          },
          {
            "head_id": 3,
            "latitude": 0.731044681702,
            "longitude": 0.656734408405
          },
          {
            "head_id": 4,
            "latitude": 0.558015130796,
            "longitude": 0.963479687787
          },
          {
            "head_id": 5,
            "latitude": 0.615987651659,
            "longitude": 1.04831238507
          }
        ],
        "product_type_id": 2267,
        "qty_per_cycle": 7705
      },
      "install_time": "2019-04-21T00:00:54Z",
      "last_cycle_start": "2019-04-21T00:00:54Z",
      "latitude": 0.801152282613,
      "longitude": 0.687703468543,
      "pin_id": 1023179687929,
      "type_id": 2848
    },
    {
      "contents": [
        {
          "amount": 16600,
          "type_id": 2393
        }
      ],
      "last_cycle_start": "2017-07-20T21:51:56Z",
      "latitude": 0.813357783,
      "longitude": 0.687574449775,
      "pin_id": 1023179683773,
      "type_id": 2541
    },
    {
      "expiry_time": "2019-04-29T06:00:54Z",
      "extractor_details": {
        "cycle_time": 7200,
        "head_radius": 0.0337474383414,
        "heads": [
          {
            "head_id": 0,
            "latitude": 1.01458729659,
            "longitude": 0.690531536952
          },
          {
            "head_id": 1,
            "latitude": 1.00363475069,
            "longitude": 0.773111857952
          },
          {
            "head_id": 2,
            "latitude": 0.990843995405,
            "longitude": 0.854122301118
          },
          {
            "head_id": 3,
            "latitude": 0.970860864652,
            "longitude": 0.934489005998
          }
        ],
        "product_type_id": 2073,
        "qty_per_cycle": 7500
      },
      "install_time": "2019-04-21T00:00:54Z",
      "last_cycle_start": "2019-04-21T00:00:54Z",
      "latitude": 0.850120443812,
      "longitude": 0.687222498621,
      "pin_id": 1023179687935,
      "type_id": 2848
    },
    {
      "latitude": 0.825713878651,
      "longitude": 0.687579014003,
      "pin_id": 1022000304230,
      "type_id": 2524
    },
    {
      "contents": [
        {
          "amount": 40,
          "type_id": 2393
        },
        {
          "amount": 20,
          "type_id": 2398
        }
      ],
      "last_cycle_start": "2017-07-29T03:20:34Z",
      "latitude": 0.82475919413,
      "longitude": 0.716977446131,
      "pin_id": 1023179758278,
      "schematic_id": 78,
      "type_id": 2474
    },
    {
      "last_cycle_start": "2017-07-29T01:50:34Z",
      "latitude": 0.81935176621,
      "longitude": 0.702245841281,
      "pin_id": 1023179721292,
      "schematic_id": 126,
      "type_id": 2473
    },
    {
      "contents": [
        {
          "amount": 10000,
          "type_id": 2393
        },
        {
          "amount": 2130,
          "type_id": 2463
        }
      ],
      "latitude": 0.837832795749,
      "longitude": 0.687336456923,
      "pin_id": 1023179683769,
      "type_id": 2544
    },
    {
      "contents": [
        {
          "amount": 40,
          "type_id": 2393
        }
      ],
      "last_cycle_start": "2017-07-28T19:50:34Z",
      "latitude": 0.825484237862,
      "longitude": 0.658855361203,
      "pin_id": 1023179758288,
      "schematic_id": 78,
      "type_id": 2474
    }
  ],
  "routes": [
    {
      "content_type_id": 2267,
      "destination_pin_id": 1023179721299,
      "quantity": 3000,
      "route_id": 788335680,
      "source_pin_id": 1023179683773
    },
    {
      "content_type_id": 2393,
      "destination_pin_id": 1023179758288,
      "quantity": 40,
      "route_id": 788336704,
      "source_pin_id": 1023179683773,
      "waypoints": [
        1023179721299
      ]
    },
    {
      "content_type_id": 2398,
      "destination_pin_id": 1023179758288,
      "quantity": 40,
      "route_id": 788336706,
      "source_pin_id": 1023179683773,
      "waypoints": [
        1023179721299
      ]
    },
    {
      "content_type_id": 2463,
      "destination_pin_id": 1023179683769,
      "quantity": 5,
      "route_id": 788336267,
      "source_pin_id": 1023179758288,
      "waypoints": [
        1023179721299,
        1023179683773,
        1022000304230
      ]
    },
    {
      "content_type_id": 2398,
      "destination_pin_id": 1023179758278,
      "quantity": 40,
      "route_id": 788336705,
      "source_pin_id": 1023179683773,
      "waypoints": [
        1023179721292
      ]
    },
    {
      "content_type_id": 2463,
      "destination_pin_id": 1023179683769,
      "quantity": 5,
      "route_id": 788336268,
      "source_pin_id": 1023179758278,
      "waypoints": [
        1023179721292,
        1023179683773,
        1022000304230
      ]
    },
    {
      "content_type_id": 2393,
      "destination_pin_id": 1023179683773,
      "quantity": 20,
      "route_id": 788335819,
      "source_pin_id": 1023179721301,
      "waypoints": [
        1022000304230
      ]
    },
    {
      "content_type_id": 2393,
      "destination_pin_id": 1023179683773,
      "quantity": 20,
      "route_id": 788335820,
      "source_pin_id": 1023179721303,
      "waypoints": [
        1022000304230
      ]
    },
    {
      "content_type_id": 2073,
      "destination_pin_id": 1023179721301,
      "quantity": 3000,
      "route_id": 788335920,
      "source_pin_id": 1023179683773,
      "waypoints": [
        1022000304230
      ]
    },
    {
      "content_type_id": 2073,
      "destination_pin_id": 1023179721303,
      "quantity": 3000,
      "route_id": 788335921,
      "source_pin_id": 1023179683773,
      "waypoints": [
        1022000304230
      ]
    },
    {
      "content_type_id": 2267,
      "destination_pin_id": 1023179683773,
      "quantity": 110952,
      "route_id": 955929972,
      "source_pin_id": 1023179687929
    },
    {
      "content_type_id": 2073,
      "destination_pin_id": 1023179683773,
      "quantity": 108000,
      "route_id": 955929973,
      "source_pin_id": 1023179687935,
      "waypoints": [
        1023179683769,
        1022000304230
      ]
    },
    {
      "content_type_id": 2267,
      "destination_pin_id": 1023179721292,
      "quantity": 3000,
      "route_id": 788335679,
      "source_pin_id": 1023179683773
    },
    {
      "content_type_id": 2398,
      "destination_pin_id": 1023179683773,
      "quantity": 20,
      "route_id": 788335677,
      "source_pin_id": 1023179721292
    },
    {
      "content_type_id": 2398,
      "destination_pin_id": 1023179683773,
      "quantity": 20,
      "route_id": 788335678,
      "source_pin_id": 1023179721299
    },
    {
      "content_type_id": 2393,
      "destination_pin_id": 1023179758278,
      "quantity": 40,
      "route_id": 788336703,
      "source_pin_id": 1023179683773,
      "waypoints": [
        1023179721292
      ]
    }
  ]
}
"""

const val getMockCharactersCharacterIdMining = """
[
  {
    "date": "2019-05-21",
    "quantity": 480000,
    "solar_system_id": 30001647,
    "type_id": 34
  },
  {
    "date": "2019-05-21",
    "quantity": 780000,
    "solar_system_id": 30001647,
    "type_id": 35
  },
  {
    "date": "2019-05-21",
    "quantity": 88000,
    "solar_system_id": 30001647,
    "type_id": 17471
  },
  {
    "date": "2019-05-21",
    "quantity": 7800,
    "solar_system_id": 30001681,
    "type_id": 45498
  },
  {
    "date": "2019-05-20",
    "quantity": 480000,
    "solar_system_id": 30001647,
    "type_id": 34
  },
  {
    "date": "2019-05-20",
    "quantity": 780000,
    "solar_system_id": 30001647,
    "type_id": 35
  },
  {
    "date": "2019-05-20",
    "quantity": 88000,
    "solar_system_id": 30001647,
    "type_id": 17471
  },
  {
    "date": "2019-05-20",
    "quantity": 7800,
    "solar_system_id": 30001681,
    "type_id": 45498
  },
  {
    "date": "2019-05-19",
    "quantity": 180000,
    "solar_system_id": 30001647,
    "type_id": 34
  },
  {
    "date": "2019-05-19",
    "quantity": 480000,
    "solar_system_id": 30001647,
    "type_id": 35
  },
  {
    "date": "2019-05-19",
    "quantity": 68000,
    "solar_system_id": 30001647,
    "type_id": 17471
  },
  {
    "date": "2019-05-19",
    "quantity": 5800,
    "solar_system_id": 30001681,
    "type_id": 45498
  },
  {
    "date": "2019-05-18",
    "quantity": 28000,
    "solar_system_id": 30001647,
    "type_id": 17471
  },
  {
    "date": "2019-05-18",
    "quantity": 2800,
    "solar_system_id": 30001681,
    "type_id": 45498
  },
  {
    "date": "2019-05-18",
    "quantity": 17000,
    "solar_system_id": 30001647,
    "type_id": 17471
  },
  {
    "date": "2019-05-18",
    "quantity": 1700,
    "solar_system_id": 30001681,
    "type_id": 45498
  },
  {
    "date": "2019-05-05",
    "quantity": 31263,
    "solar_system_id": 30001647,
    "type_id": 17471
  },
  {
    "date": "2019-05-05",
    "quantity": 2105,
    "solar_system_id": 30001647,
    "type_id": 17460
  },
  {
    "date": "2019-05-14",
    "quantity": 834,
    "solar_system_id": 30001681,
    "type_id": 45498
  }
]
"""

const val getMockPlanets = """
[
  {
    "last_update": "2017-07-21T08:47:33Z",
    "num_pins": 24,
    "owner_id": 92002067,
    "planet_id": 40237824,
    "planet_type": "barren",
    "solar_system_id": 30003752,
    "upgrade_level": 5
  }
]
"""
const val getMockPlanetAdvancedFactories_fulldata = """
{
  "pins": [
    {
      "contents": [
        {
          "amount": 40,
          "type_id": 2393
        },
        {
          "amount": 40,
          "type_id": 3779
        }
      ],
      "last_cycle_start": "2017-07-21T07:51:20Z",
      "latitude": 2.26701029532,
      "longitude": 2.01422239033,
      "pin_id": 1022847333669,
      "schematic_id": 81,
      "type_id": 2474
    },
    {
      "contents": [
        {
          "amount": 10,
          "type_id": 9842
        },
        {
          "amount": 5,
          "type_id": 3828
        }
      ],
      "last_cycle_start": "2017-07-21T07:59:11Z",
      "latitude": 2.26702104759,
      "longitude": 1.9864644165,
      "pin_id": 1022847333673,
      "schematic_id": 98,
      "type_id": 2474
    },
    {
      "contents": [
        {
          "amount": 660,
          "type_id": 2400
        },
        {
          "amount": 280,
          "type_id": 2401
        },
        {
          "amount": 106000,
          "type_id": 2306
        },
        {
          "amount": 540,
          "type_id": 3779
        },
        {
          "amount": 96000,
          "type_id": 2308
        },
        {
          "amount": 57,
          "type_id": 2349
        },
        {
          "amount": 348000,
          "type_id": 2286
        },
        {
          "amount": 280,
          "type_id": 2389
        },
        {
          "amount": 340,
          "type_id": 2390
        },
        {
          "amount": 1100,
          "type_id": 2393
        },
        {
          "amount": 340,
          "type_id": 3645
        },
        {
          "amount": 560,
          "type_id": 2398
        }
      ],
      "last_cycle_start": "2017-07-21T11:46:12Z",
      "latitude": 2.32178261051,
      "longitude": 1.99824191983,
      "pin_id": 1022847351300,
      "type_id": 2544
    },
    {
      "contents": [
        {
          "amount": 3000,
          "type_id": 2286
        }
      ],
      "last_cycle_start": "2017-07-21T08:20:05Z",
      "latitude": 2.27901353422,
      "longitude": 1.95729407571,
      "pin_id": 1022847372316,
      "schematic_id": 132,
      "type_id": 2473
    },
    {
      "contents": [
        {
          "amount": 40,
          "type_id": 2401
        },
        {
          "amount": 40,
          "type_id": 2389
        }
      ],
      "last_cycle_start": "2017-07-21T07:53:46Z",
      "latitude": 2.3035284181,
      "longitude": 2.01324113569,
      "pin_id": 1022847338705,
      "schematic_id": 71,
      "type_id": 2474
    },
    {
      "contents": [
        {
          "amount": 10,
          "type_id": 9840
        },
        {
          "amount": 10,
          "type_id": 3775
        },
        {
          "amount": 10,
          "type_id": 3695
        }
      ],
      "last_cycle_start": "2017-07-21T08:44:24Z",
      "latitude": 2.30351251571,
      "longitude": 1.98446578125,
      "pin_id": 1022847338707,
      "schematic_id": 110,
      "type_id": 2474
    },
    {
      "contents": [
        {
          "amount": 40,
          "type_id": 2393
        },
        {
          "amount": 40,
          "type_id": 3779
        }
      ],
      "last_cycle_start": "2017-07-21T07:59:13Z",
      "latitude": 2.26099501369,
      "longitude": 2.00063565333,
      "pin_id": 1022847333661,
      "schematic_id": 81,
      "type_id": 2474
    },
    {
      "contents": [
        {
          "amount": 3000,
          "type_id": 2272
        }
      ],
      "last_cycle_start": "2017-07-21T08:47:33Z",
      "latitude": 2.29137084925,
      "longitude": 1.98516064676,
      "pin_id": 1024910917364,
      "schematic_id": 128,
      "type_id": 2473
    },
    {
      "contents": [
        {
          "amount": 3000,
          "type_id": 2308
        }
      ],
      "last_cycle_start": "2017-07-21T08:22:36Z",
      "latitude": 2.29137998788,
      "longitude": 2.01377589358,
      "pin_id": 1024910917365,
      "schematic_id": 122,
      "type_id": 2473
    },
    {
      "contents": [
        {
          "amount": 3000,
          "type_id": 2272
        }
      ],
      "last_cycle_start": "2017-07-21T08:47:33Z",
      "latitude": 2.27903152684,
      "longitude": 1.98521809739,
      "pin_id": 1024967744081,
      "schematic_id": 128,
      "type_id": 2473
    },
    {
      "contents": [
        {
          "amount": 545,
          "type_id": 9832
        },
        {
          "amount": 820,
          "type_id": 3689
        },
        {
          "amount": 840,
          "type_id": 3691
        },
        {
          "amount": 770,
          "type_id": 9836
        },
        {
          "amount": 820,
          "type_id": 9838
        },
        {
          "amount": 390,
          "type_id": 3695
        },
        {
          "amount": 435,
          "type_id": 9840
        },
        {
          "amount": 420,
          "type_id": 9842
        },
        {
          "amount": 810,
          "type_id": 2328
        },
        {
          "amount": 10,
          "type_id": 3775
        }
      ],
      "last_cycle_start": "2017-07-21T08:49:43Z",
      "latitude": 2.29729032221,
      "longitude": 1.99945532715,
      "pin_id": 1022847311747,
      "type_id": 2541
    },
    {
      "contents": [
        {
          "amount": 10,
          "type_id": 3689
        },
        {
          "amount": 10,
          "type_id": 9836
        }
      ],
      "last_cycle_start": "2017-07-21T08:25:49Z",
      "latitude": 2.28525672675,
      "longitude": 1.97089844736,
      "pin_id": 1022847372292,
      "schematic_id": 97,
      "type_id": 2474
    },
    {
      "last_cycle_start": "2017-07-21T09:43:48Z",
      "latitude": 2.27310658271,
      "longitude": 2.00004973793,
      "pin_id": 1022847311749,
      "type_id": 2541
    },
    {
      "contents": [
        {
          "amount": 10,
          "type_id": 3691
        },
        {
          "amount": 10,
          "type_id": 9838
        }
      ],
      "last_cycle_start": "2017-07-21T08:26:50Z",
      "latitude": 2.27239886788,
      "longitude": 1.97163458582,
      "pin_id": 1022847372295,
      "schematic_id": 89,
      "type_id": 2474
    },
    {
      "contents": [
        {
          "amount": 10,
          "type_id": 9840
        },
        {
          "amount": 10,
          "type_id": 3775
        },
        {
          "amount": 10,
          "type_id": 3695
        }
      ],
      "last_cycle_start": "2017-07-21T08:44:24Z",
      "latitude": 2.29749058264,
      "longitude": 1.9701484492,
      "pin_id": 1022847372299,
      "schematic_id": 110,
      "type_id": 2474
    },
    {
      "contents": [
        {
          "amount": 40,
          "type_id": 3645
        },
        {
          "amount": 40,
          "type_id": 2390
        }
      ],
      "last_cycle_start": "2017-07-21T08:38:27Z",
      "latitude": 2.28526935749,
      "longitude": 2.02800843524,
      "pin_id": 1022847372301,
      "schematic_id": 66,
      "type_id": 2474
    },
    {
      "contents": [
        {
          "amount": 40,
          "type_id": 2400
        },
        {
          "amount": 40,
          "type_id": 2398
        }
      ],
      "last_cycle_start": "2017-07-21T07:59:11Z",
      "latitude": 2.29751946566,
      "longitude": 2.02756223403,
      "pin_id": 1022847372303,
      "schematic_id": 74,
      "type_id": 2474
    },
    {
      "contents": [
        {
          "amount": 40,
          "type_id": 3645
        },
        {
          "amount": 40,
          "type_id": 2390
        }
      ],
      "last_cycle_start": "2017-07-21T08:38:27Z",
      "latitude": 2.27237778221,
      "longitude": 2.02905349659,
      "pin_id": 1022847372305,
      "schematic_id": 66,
      "type_id": 2474
    }
  ],
  "routes": [
    {
      "content_type_id": 3645,
      "destination_pin_id": 1022847372301,
      "quantity": 40,
      "route_id": 833392285,
      "source_pin_id": 1022847351300,
      "waypoints": [
        1022847333660,
        1022847311747,
        1019442390434,
        1024910917365
      ]
    },
    {
      "content_type_id": 2393,
      "destination_pin_id": 1022847351300,
      "quantity": 20,
      "route_id": 832424838,
      "source_pin_id": 1022847372312,
      "waypoints": [
        1022847372301,
        1024910917365,
        1019442390434,
        1022847311747,
        1022847333660
      ]
    },
    {
      "content_type_id": 2272,
      "destination_pin_id": 1024967744081,
      "quantity": 3000,
      "route_id": 833425799,
      "source_pin_id": 1022847351300,
      "waypoints": [
        1022847333660,
        1022847311747,
        1019442390434,
        1022847311749
      ]
    },
    {
      "content_type_id": 2272,
      "destination_pin_id": 1024910917364,
      "quantity": 3000,
      "route_id": 833425800,
      "source_pin_id": 1022847351300,
      "waypoints": [
        1022847333660,
        1022847311747,
        1019442390434
      ]
    },
    {
      "content_type_id": 2286,
      "destination_pin_id": 1022847372316,
      "quantity": 3000,
      "route_id": 832002860,
      "source_pin_id": 1022847351300,
      "waypoints": [
        1022847333660,
        1022847311747,
        1019442390434,
        1024910917364,
        1022847372292
      ]
    },
    {
      "content_type_id": 3779,
      "destination_pin_id": 1022847351300,
      "quantity": 20,
      "route_id": 832024514,
      "source_pin_id": 1022847372317,
      "waypoints": [
        1022847372299,
        1022847338707,
        1022847311747,
        1022847333660
      ]
    },
    {
      "content_type_id": 3775,
      "destination_pin_id": 1022847311747,
      "quantity": 5,
      "route_id": 832023565,
      "source_pin_id": 1022847333661,
      "waypoints": [
        1022847311749,
        1019442390434
      ]
    },
    {
      "content_type_id": 2401,
      "destination_pin_id": 1022847351300,
      "quantity": 20,
      "route_id": 833391128,
      "source_pin_id": 1024967449015,
      "waypoints": [
        1019442390434,
        1022847311747,
        1022847333660
      ]
    },
    {
      "content_type_id": 2286,
      "destination_pin_id": 1022847372317,
      "quantity": 3000,
      "route_id": 832024519,
      "source_pin_id": 1022847351300,
      "waypoints": [
        1022847333660,
        1022847311747,
        1022847338707,
        1022847372299
      ]
    },
    {
      "content_type_id": 9832,
      "destination_pin_id": 1022847311747,
      "quantity": 5,
      "route_id": 833381403,
      "source_pin_id": 1022847372305,
      "waypoints": [
        1022847372301,
        1024910917365,
        1019442390434
      ]
    },
    {
      "content_type_id": 2390,
      "destination_pin_id": 1022847372305,
      "quantity": 40,
      "route_id": 833381404,
      "source_pin_id": 1022847351300,
      "waypoints": [
        1022847333660,
        1022847311747,
        1019442390434,
        1024910917365,
        1022847372301
      ]
    },
    {
      "content_type_id": 3645,
      "destination_pin_id": 1022847372305,
      "quantity": 40,
      "route_id": 833381405,
      "source_pin_id": 1022847351300,
      "waypoints": [
        1022847333660,
        1022847311747,
        1019442390434,
        1024910917365,
        1022847372301
      ]
    },
    {
      "content_type_id": 3775,
      "destination_pin_id": 1022847351300,
      "quantity": 5,
      "route_id": 833424798,
      "source_pin_id": 1022847333669,
      "waypoints": [
        1022847311749,
        1019442390434,
        1022847311747,
        1022847333660
      ]
    },
    {
      "content_type_id": 2393,
      "destination_pin_id": 1022847333669,
      "quantity": 40,
      "route_id": 833424799,
      "source_pin_id": 1022847351300,
      "waypoints": [
        1022847333660,
        1022847311747,
        1019442390434,
        1022847311749
      ]
    },
    {
      "content_type_id": 3779,
      "destination_pin_id": 1022847333669,
      "quantity": 40,
      "route_id": 833424800,
      "source_pin_id": 1022847351300,
      "waypoints": [
        1022847333660,
        1022847311747,
        1019442390434,
        1022847311749
      ]
    },
    {
      "content_type_id": 2400,
      "destination_pin_id": 1022847351300,
      "quantity": 20,
      "route_id": 833425571,
      "source_pin_id": 1024967744081,
      "waypoints": [
        1022847311749,
        1019442390434,
        1022847311747,
        1022847333660
      ]
    },
    {
      "content_type_id": 2400,
      "destination_pin_id": 1022847351300,
      "quantity": 20,
      "route_id": 833425572,
      "source_pin_id": 1024910917364,
      "waypoints": [
        1019442390434,
        1022847311747,
        1022847333660
      ]
    },
    {
      "content_type_id": 2390,
      "destination_pin_id": 1022847372301,
      "quantity": 40,
      "route_id": 833392284,
      "source_pin_id": 1022847351300,
      "waypoints": [
        1022847333660,
        1022847311747,
        1019442390434,
        1024910917365
      ]
    },
    {
      "content_type_id": 17136,
      "destination_pin_id": 1022847311749,
      "quantity": 3,
      "route_id": 833381191,
      "source_pin_id": 1022847372295,
      "waypoints": [
        1022847333673
      ]
    },
    {
      "content_type_id": 9840,
      "destination_pin_id": 1022847311747,
      "quantity": 5,
      "route_id": 833425068,
      "source_pin_id": 1022847338705
    },
    {
      "content_type_id": 9848,
      "destination_pin_id": 1022847311749,
      "quantity": 3,
      "route_id": 833381022,
      "source_pin_id": 1022847372292,
      "waypoints": [
        1024910917364,
        1019442390434
      ]
    },
    {
      "content_type_id": 3689,
      "destination_pin_id": 1022847372292,
      "quantity": 10,
      "route_id": 833391688,
      "source_pin_id": 1022847311747,
      "waypoints": [
        1019442390434,
        1024910917364
      ]
    },
    {
      "content_type_id": 2306,
      "destination_pin_id": 1024967449015,
      "quantity": 3000,
      "route_id": 833391282,
      "source_pin_id": 1022847351300,
      "waypoints": [
        1022847333660,
        1022847311747,
        1019442390434
      ]
    },
    {
      "content_type_id": 2389,
      "destination_pin_id": 1022847351300,
      "quantity": 20,
      "route_id": 833391283,
      "source_pin_id": 1024910917365,
      "waypoints": [
        1019442390434,
        1022847311747,
        1022847333660
      ]
    },
    {
      "content_type_id": 2308,
      "destination_pin_id": 1024910917365,
      "quantity": 3000,
      "route_id": 833391284,
      "source_pin_id": 1022847351300,
      "waypoints": [
        1022847333660,
        1022847311747,
        1019442390434
      ]
    },
    {
      "content_type_id": 2393,
      "destination_pin_id": 1022847333661,
      "quantity": 40,
      "route_id": 833340213,
      "source_pin_id": 1022847351300,
      "waypoints": [
        1022847333660,
        1022847311747,
        1019442390434,
        1022847311749
      ]
    },
    {
      "content_type_id": 3779,
      "destination_pin_id": 1022847333661,
      "quantity": 40,
      "route_id": 833340214,
      "source_pin_id": 1022847351300,
      "waypoints": [
        1022847333660,
        1022847311747,
        1019442390434,
        1022847311749
      ]
    },
    {
      "content_type_id": 3779,
      "destination_pin_id": 1022847351300,
      "quantity": 20,
      "route_id": 831959737,
      "source_pin_id": 1022847372316,
      "waypoints": [
        1022847372292,
        1024910917364,
        1019442390434,
        1022847311747,
        1022847333660
      ]
    },
    {
      "content_type_id": 2349,
      "destination_pin_id": 1022847311749,
      "quantity": 3,
      "route_id": 831981626,
      "source_pin_id": 1022847333660,
      "waypoints": [
        1022847311747,
        1019442390434
      ]
    },
    {
      "content_type_id": 3695,
      "destination_pin_id": 1022847372299,
      "quantity": 10,
      "route_id": 833424699,
      "source_pin_id": 1022847311747,
      "waypoints": [
        1022847338707
      ]
    },
    {
      "content_type_id": 3775,
      "destination_pin_id": 1022847372299,
      "quantity": 10,
      "route_id": 833424700,
      "source_pin_id": 1022847311747,
      "waypoints": [
        1022847338707
      ]
    },
    {
      "content_type_id": 9840,
      "destination_pin_id": 1022847372299,
      "quantity": 10,
      "route_id": 833424701,
      "source_pin_id": 1022847311747,
      "waypoints": [
        1022847338707
      ]
    },
    {
      "content_type_id": 2328,
      "destination_pin_id": 1022847333660,
      "quantity": 10,
      "route_id": 831984832,
      "source_pin_id": 1022847311747
    },
    {
      "content_type_id": 9832,
      "destination_pin_id": 1022847333660,
      "quantity": 10,
      "route_id": 831984833,
      "source_pin_id": 1022847311747
    },
    {
      "content_type_id": 9836,
      "destination_pin_id": 1022847333660,
      "quantity": 10,
      "route_id": 831984834,
      "source_pin_id": 1022847311747
    },
    {
      "content_type_id": 9836,
      "destination_pin_id": 1022847372292,
      "quantity": 10,
      "route_id": 833391687,
      "source_pin_id": 1022847311747,
      "waypoints": [
        1019442390434,
        1024910917364
      ]
    },
    {
      "content_type_id": 9838,
      "destination_pin_id": 1022847372295,
      "quantity": 10,
      "route_id": 833381192,
      "source_pin_id": 1022847311747,
      "waypoints": [
        1019442390434,
        1022847311749,
        1022847333673
      ]
    },
    {
      "content_type_id": 3691,
      "destination_pin_id": 1022847372295,
      "quantity": 10,
      "route_id": 833381193,
      "source_pin_id": 1022847311747,
      "waypoints": [
        1019442390434,
        1022847311749,
        1022847333673
      ]
    },
    {
      "content_type_id": 2401,
      "destination_pin_id": 1022847338705,
      "quantity": 40,
      "route_id": 833425109,
      "source_pin_id": 1022847351300,
      "waypoints": [
        1022847333660,
        1022847311747
      ]
    },
    {
      "content_type_id": 2389,
      "destination_pin_id": 1022847338705,
      "quantity": 40,
      "route_id": 833425110,
      "source_pin_id": 1022847351300,
      "waypoints": [
        1022847333660,
        1022847311747
      ]
    },
    {
      "content_type_id": 3828,
      "destination_pin_id": 1022847311747,
      "quantity": 5,
      "route_id": 833424983,
      "source_pin_id": 1022847372303,
      "waypoints": [
        1022847338705
      ]
    },
    {
      "content_type_id": 9832,
      "destination_pin_id": 1022847311747,
      "quantity": 5,
      "route_id": 833392283,
      "source_pin_id": 1022847372301,
      "waypoints": [
        1024910917365,
        1019442390434
      ]
    },
    {
      "content_type_id": 2366,
      "destination_pin_id": 1022847311749,
      "quantity": 3,
      "route_id": 833424698,
      "source_pin_id": 1022847372299,
      "waypoints": [
        1022847338707,
        1022847311747,
        1019442390434
      ]
    },
    {
      "content_type_id": 9840,
      "destination_pin_id": 1022847338707,
      "quantity": 10,
      "route_id": 833424606,
      "source_pin_id": 1022847311747
    },
    {
      "content_type_id": 3695,
      "destination_pin_id": 1022847338707,
      "quantity": 10,
      "route_id": 833424607,
      "source_pin_id": 1022847311747
    },
    {
      "content_type_id": 3775,
      "destination_pin_id": 1022847338707,
      "quantity": 10,
      "route_id": 833424608,
      "source_pin_id": 1022847311747
    },
    {
      "content_type_id": 2366,
      "destination_pin_id": 1022847311749,
      "quantity": 3,
      "route_id": 831868296,
      "source_pin_id": 1022847338707,
      "waypoints": [
        1022847311747,
        1019442390434
      ]
    },
    {
      "content_type_id": 3828,
      "destination_pin_id": 1022847333673,
      "quantity": 10,
      "route_id": 833425252,
      "source_pin_id": 1022847311747,
      "waypoints": [
        1019442390434,
        1022847311749
      ]
    },
    {
      "content_type_id": 9842,
      "destination_pin_id": 1022847333673,
      "quantity": 10,
      "route_id": 833425253,
      "source_pin_id": 1022847311747,
      "waypoints": [
        1019442390434,
        1022847311749
      ]
    },
    {
      "content_type_id": 2351,
      "destination_pin_id": 1022847311749,
      "quantity": 3,
      "route_id": 833392112,
      "source_pin_id": 1022847333673
    },
    {
      "content_type_id": 2398,
      "destination_pin_id": 1022847372303,
      "quantity": 40,
      "route_id": 833425779,
      "source_pin_id": 1022847351300,
      "waypoints": [
        1022847333660,
        1022847311747,
        1022847338705
      ]
    },
    {
      "content_type_id": 2400,
      "destination_pin_id": 1022847372303,
      "quantity": 40,
      "route_id": 833425780,
      "source_pin_id": 1022847351300,
      "waypoints": [
        1022847333660,
        1022847311747,
        1022847338705
      ]
    },
    {
      "content_type_id": 2393,
      "destination_pin_id": 1022847351300,
      "quantity": 20,
      "route_id": 833342838,
      "source_pin_id": 1022847372311,
      "waypoints": [
        1022847372301,
        1024910917365,
        1019442390434,
        1022847311747,
        1022847333660
      ]
    },
    {
      "content_type_id": 2073,
      "destination_pin_id": 1022847372311,
      "quantity": 3000,
      "route_id": 833342839,
      "source_pin_id": 1022847351300,
      "waypoints": [
        1022847333660,
        1022847311747,
        1019442390434,
        1024910917365,
        1022847372301
      ]
    },
    {
      "content_type_id": 2073,
      "destination_pin_id": 1022847372312,
      "quantity": 3000,
      "route_id": 833342840,
      "source_pin_id": 1022847351300,
      "waypoints": [
        1022847333660,
        1022847311747,
        1019442390434,
        1024910917365,
        1022847372301
      ]
    }
  ]
}
"""

const val getMockPlanetAdvancedFactories = """
{
  "pins": [
    {
      "contents": [
        {
          "amount": 3000,
          "type_id": 2306
        }
      ],
      "last_cycle_start": "2017-07-21T08:22:36Z",
      "latitude": 2.27903719767,
      "longitude": 2.01406248364,
      "pin_id": 1024967449015,
      "schematic_id": 129,
      "type_id": 2473
    },
    {
      "contents": [
        {
          "amount": 2000,
          "type_id": 2306
        }
      ],
      "last_cycle_start": "2017-07-21T08:22:36Z",
      "latitude": 2.32178261051,
      "longitude": 1.99824191983,
      "pin_id": 1024967449015,
      "schematic_id": 129,
      "type_id": 2473
    },
    {
      "contents": [
        {
          "amount": 10,
          "type_id": 3691
        },
        {
          "amount": 10,
          "type_id": 9838
        }
      ],
      "last_cycle_start": "2017-07-21T08:26:50Z",
      "latitude": 2.27239886788,
      "longitude": 1.97163458582,
      "pin_id": 1022847372296,
      "schematic_id": 89,
      "type_id": 2474
    },
    {
      "contents": [
        {
          "amount": 5,
          "type_id": 3691
        },
        {
          "amount": 10,
          "type_id": 9838
        }
      ],
      "last_cycle_start": "2017-07-21T08:26:50Z",
      "latitude": 2.27947373989,
      "longitude": 2.04222191406,
      "pin_id": 1022847372295,
      "schematic_id": 89,
      "type_id": 2474
    },
    {
      "contents": [
        {
          "amount": 10,
          "type_id": 2328
        },
        {
          "amount": 8,
          "type_id": 9832
        },
        {
          "amount": 5,
          "type_id": 9836
        }
      ],
      "last_cycle_start": "2017-07-21T08:35:20Z",
      "latitude": 2.26701029532,
      "longitude": 2.01422239033,
      "pin_id": 1022847333660,
      "schematic_id": 96,
      "type_id": 2474
    },
    {
      "contents": [
        {
          "amount": 10,
          "type_id": 2328
        },
        {
          "amount": 10,
          "type_id": 9832
        },
        {
          "amount": 10,
          "type_id": 9836
        }
      ],
      "last_cycle_start": "2017-07-21T08:35:20Z",
      "latitude": 2.30938039038,
      "longitude": 1.99885095517,
      "pin_id": 1022847333660,
      "schematic_id": 96,
      "type_id": 2474
    },
    {
      "last_cycle_start": "2017-01-15T12:08:55Z",
      "latitude": 2.2852027294,
      "longitude": 1.99976467424,
      "pin_id": 1019442390434,
      "type_id": 2524
    }
  ]
}
"""
const val getType2561 = """
{
  "capacity": 12000,
  "description": "\"At some point, it all comes down to more metal.\" The designers of this storage site believed this adage above all else. The outer walls of each container are comprised of almost a meter of titanium alloy around a flexible, lightweight tritanium frame, all sealed with a few layers of active nanite coating to prevent microfractures and thermal warping. This combination allows the building to withstand nearly any environmental challenge. To prevent the tritanium supports from decaying, the interior is kept in a constant vacuum, and workers must wear fully sealed atmosphere suits at all times.",
  "dogma_attributes": [
    {
      "attribute_id": 1632,
      "value": 2017
    },
    {
      "attribute_id": 161,
      "value": 0
    },
    {
      "attribute_id": 162,
      "value": 0
    },
    {
      "attribute_id": 4,
      "value": 0
    },
    {
      "attribute_id": 38,
      "value": 12000
    },
    {
      "attribute_id": 15,
      "value": 700
    },
    {
      "attribute_id": 49,
      "value": 500
    }
  ],
  "graphic_id": 4572,
  "group_id": 1029,
  "mass": 0,
  "name": "Storm Storage Facility",
  "packaged_volume": 0,
  "portion_size": 1,
  "published": true,
  "radius": 1,
  "type_id": 2561,
  "volume": 0
}
"""
const val getType2483 = """
{
  "capacity": 0,
  "description": "Instead of laboring to shield the production lines of this industrial facility from the surrounding environment, designers opted instead to use the available heat, interference, and even crushing pressure to help power the planetaryFacility itself. A plant on an ice planet might have highly advanced extended heat sinks, while a factory on a plasma world might draw most, if not all of its electricity from magnetized coils specially attuned to the planet's local ion winds. Taking advantage of the indigenous features of each world helps offset the cost of building mass production infrastructure there, which usually involves protective coatings, environmental clothing, and reinforced foundations.",
  "dogma_attributes": [
    {
      "attribute_id": 1632,
      "value": 2017
    },
    {
      "attribute_id": 161,
      "value": 0
    },
    {
      "attribute_id": 162,
      "value": 0
    },
    {
      "attribute_id": 4,
      "value": 0
    },
    {
      "attribute_id": 38,
      "value": 0
    },
    {
      "attribute_id": 15,
      "value": 800
    },
    {
      "attribute_id": 49,
      "value": 200
    }
  ],
  "graphic_id": 4533,
  "group_id": 1028,
  "mass": 0,
  "name": "Storm Basic Industry Facility",
  "packaged_volume": 0,
  "portion_size": 1,
  "published": true,
  "radius": 1,
  "type_id": 2483,
  "volume": 0
}
"""
