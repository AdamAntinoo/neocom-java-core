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

	fun matchRequest(uri: String): String {
		return when {
			matches(uri, "characters", "planets","40208073") -> getMockPlanetAdvancedFactories
			matches(uri, "characters", "planets") -> getMockPlanets
//			uri.contains("40237774") -> getMockPlanetStuctures
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
}

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
const val getMockPlanetAdvancedFactories = """
{
  "links": [
    {
      "destination_pin_id": 1024811499528,
      "link_level": 0,
      "source_pin_id": 1012696414582
    },
    {
      "destination_pin_id": 1024811525162,
      "link_level": 0,
      "source_pin_id": 1024811499528
    },
    {
      "destination_pin_id": 1024811499529,
      "link_level": 0,
      "source_pin_id": 1012696414582
    },
    {
      "destination_pin_id": 1024811525164,
      "link_level": 0,
      "source_pin_id": 1012696414582
    },
    {
      "destination_pin_id": 1024814936920,
      "link_level": 0,
      "source_pin_id": 1024811525162
    },
    {
      "destination_pin_id": 1024814936918,
      "link_level": 0,
      "source_pin_id": 1024814927793
    },
    {
      "destination_pin_id": 1024811499533,
      "link_level": 0,
      "source_pin_id": 1024811499528
    },
    {
      "destination_pin_id": 1024814927790,
      "link_level": 0,
      "source_pin_id": 1024811499528
    },
    {
      "destination_pin_id": 1024811499544,
      "link_level": 0,
      "source_pin_id": 1024811499529
    },
    {
      "destination_pin_id": 1024814927793,
      "link_level": 0,
      "source_pin_id": 1012696414582
    }
  ],
  "pins": [
    {
      "contents": [
        {
          "amount": 333,
          "type_id": 3689
        }
      ],
      "last_cycle_start": "2019-06-14T23:41:37Z",
      "latitude": 0.808934460793,
      "longitude": 4.74107389673,
      "pin_id": 1012696414582,
      "type_id": 2524
    },
    {
      "contents": [
        {
          "amount": 40,
          "type_id": 2398
        },
        {
          "amount": 40,
          "type_id": 2399
        }
      ],
      "last_cycle_start": "2019-06-14T22:37:15Z",
      "latitude": 0.809178342394,
      "longitude": 4.71122230433,
      "pin_id": 1024814936918,
      "schematic_id": 73,
      "type_id": 2474
    },
    {
      "contents": [
        {
          "amount": 40,
          "type_id": 2399
        }
      ],
      "last_cycle_start": "2019-06-14T22:37:15Z",
      "latitude": 0.809148885737,
      "longitude": 4.77027205957,
      "pin_id": 1024814936920,
      "schematic_id": 73,
      "type_id": 2474
    },
    {
      "expiry_time": "2019-06-23T05:33:07Z",
      "extractor_details": {
        "cycle_time": 7200,
        "head_radius": 0.0337348617613,
        "heads": [
          {
            "head_id": 0,
            "latitude": 0.896133453499,
            "longitude": 5.12300457819
          },
          {
            "head_id": 1,
            "latitude": 0.958408246292,
            "longitude": 5.08663048875
          },
          {
            "head_id": 2,
            "latitude": 0.904523304872,
            "longitude": 5.03122478869
          },
          {
            "head_id": 3,
            "latitude": 0.967623252851,
            "longitude": 5.00112794175
          },
          {
            "head_id": 4,
            "latitude": 0.957570338919,
            "longitude": 4.91837802861
          }
        ],
        "product_type_id": 2270,
        "qty_per_cycle": 5376
      },
      "install_time": "2019-06-14T23:33:07Z",
      "last_cycle_start": "2019-06-14T23:33:07Z",
      "latitude": 0.83315264167,
      "longitude": 4.74034355048,
      "pin_id": 1024811499544,
      "type_id": 2848
    },
    {
      "contents": [
        {
          "amount": 4000,
          "type_id": 3689
        },
        {
          "amount": 71898,
          "type_id": 2267
        },
        {
          "amount": 10900,
          "type_id": 2399
        }
      ],
      "last_cycle_start": "2019-06-14T23:42:06Z",
      "latitude": 0.796796216569,
      "longitude": 4.74084864085,
      "pin_id": 1024811499528,
      "type_id": 2541
    },
    {
      "contents": [
        {
          "amount": 4705,
          "type_id": 3689
        },
        {
          "amount": 79950,
          "type_id": 2267
        },
        {
          "amount": 50000,
          "type_id": 2270
        }
      ],
      "last_cycle_start": "2019-06-15T00:40:34Z",
      "latitude": 0.820994655635,
      "longitude": 4.74050339883,
      "pin_id": 1024811499529,
      "type_id": 2544
    },
    {
      "contents": [
        {
          "amount": 3000,
          "type_id": 2267
        }
      ],
      "last_cycle_start": "2019-06-14T23:07:15Z",
      "latitude": 0.802738796915,
      "longitude": 4.75563428247,
      "pin_id": 1024811525162,
      "schematic_id": 126,
      "type_id": 2473
    },
    {
      "last_cycle_start": "2019-06-10T15:18:38Z",
      "latitude": 0.81560809073,
      "longitude": 4.755343046,
      "pin_id": 1024811525164,
      "schematic_id": 127,
      "type_id": 2473
    },
    {
      "extractor_details": {
        "heads": [
          {
            "head_id": 0,
            "latitude": 0.602220743615,
            "longitude": 4.98520833942
          },
          {
            "head_id": 1,
            "latitude": 0.671973269926,
            "longitude": 4.98291496074
          },
          {
            "head_id": 2,
            "latitude": 0.639786770323,
            "longitude": 4.88497157768
          },
          {
            "head_id": 3,
            "latitude": 0.639955639896,
            "longitude": 5.08402607052
          }
        ]
      },
      "last_cycle_start": "2019-06-14T23:07:15Z",
      "latitude": 0.784725840572,
      "longitude": 4.74103527972,
      "pin_id": 1024811499533,
      "type_id": 2848
    },
    {
      "contents": [
        {
          "amount": 3000,
          "type_id": 2267
        }
      ],
      "last_cycle_start": "2019-06-14T23:07:15Z",
      "latitude": 0.803082801349,
      "longitude": 4.72601355333,
      "pin_id": 1024814927790,
      "schematic_id": 126,
      "type_id": 2473
    },
    {
      "contents": [
        {
          "amount": 2171,
          "type_id": 2270
        }
      ],
      "last_cycle_start": "2019-06-10T15:18:38Z",
      "latitude": 0.815408799205,
      "longitude": 4.7256722388,
      "pin_id": 1024814927793,
      "schematic_id": 127,
      "type_id": 2473
    }
  ],
  "routes": [
    {
      "content_type_id": 3689,
      "destination_pin_id": 1024811499529,
      "quantity": 5,
      "route_id": 828047395,
      "source_pin_id": 1024814936920,
      "waypoints": [
        1024811525162,
        1024811499528,
        1012696414582
      ]
    },
    {
      "content_type_id": 3689,
      "destination_pin_id": 1024811499529,
      "quantity": 5,
      "route_id": 828047396,
      "source_pin_id": 1024814936918,
      "waypoints": [
        1024814927793,
        1012696414582
      ]
    },
    {
      "content_type_id": 2399,
      "destination_pin_id": 1024814936920,
      "quantity": 40,
      "route_id": 828047397,
      "source_pin_id": 1024811499528,
      "waypoints": [
        1024811525162
      ]
    },
    {
      "content_type_id": 2399,
      "destination_pin_id": 1024814936918,
      "quantity": 40,
      "route_id": 828047398,
      "source_pin_id": 1024811499528,
      "waypoints": [
        1012696414582,
        1024814927793
      ]
    },
    {
      "content_type_id": 2398,
      "destination_pin_id": 1024814936920,
      "quantity": 40,
      "route_id": 828047399,
      "source_pin_id": 1024811499528,
      "waypoints": [
        1024811525162
      ]
    },
    {
      "content_type_id": 2398,
      "destination_pin_id": 1024814936918,
      "quantity": 40,
      "route_id": 828047400,
      "source_pin_id": 1024811499528,
      "waypoints": [
        1012696414582,
        1024814927793
      ]
    },
    {
      "content_type_id": 2398,
      "destination_pin_id": 1024811499528,
      "quantity": 20,
      "route_id": 828046764,
      "source_pin_id": 1024811525162
    },
    {
      "content_type_id": 2267,
      "destination_pin_id": 1024811525162,
      "quantity": 3000,
      "route_id": 828046765,
      "source_pin_id": 1024811499528
    },
    {
      "content_type_id": 2399,
      "destination_pin_id": 1024811499528,
      "quantity": 20,
      "route_id": 828046766,
      "source_pin_id": 1024811525164,
      "waypoints": [
        1012696414582
      ]
    },
    {
      "content_type_id": 2270,
      "destination_pin_id": 1024811525164,
      "quantity": 3000,
      "route_id": 828046767,
      "source_pin_id": 1024811499528,
      "waypoints": [
        1012696414582
      ]
    },
    {
      "content_type_id": 2267,
      "destination_pin_id": 1024811499528,
      "quantity": 68960,
      "route_id": 965905428,
      "source_pin_id": 1024811499533
    },
    {
      "content_type_id": 2399,
      "destination_pin_id": 1024811499528,
      "quantity": 20,
      "route_id": 828047221,
      "source_pin_id": 1024814927793,
      "waypoints": [
        1012696414582
      ]
    },
    {
      "content_type_id": 2267,
      "destination_pin_id": 1024814927790,
      "quantity": 3000,
      "route_id": 828047222,
      "source_pin_id": 1024811499528
    },
    {
      "content_type_id": 2270,
      "destination_pin_id": 1024814927793,
      "quantity": 3000,
      "route_id": 828047223,
      "source_pin_id": 1024811499528,
      "waypoints": [
        1012696414582
      ]
    },
    {
      "content_type_id": 2398,
      "destination_pin_id": 1024811499528,
      "quantity": 20,
      "route_id": 828047220,
      "source_pin_id": 1024814927790
    },
    {
      "content_type_id": 2270,
      "destination_pin_id": 1024811499528,
      "quantity": 77408,
      "route_id": 965940335,
      "source_pin_id": 1024811499544,
      "waypoints": [
        1024811499529,
        1012696414582
      ]
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
