package org.dimensinfin.eveonline.neocom.auth

import okhttp3.*

/**
 * This will help us to test our networking code while a particular API is not implemented
 * yet on Backend side.
 */
class MockInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
//        if (BuildConfig.DEBUG) {
        val uri = chain.request().url().uri().toString()
        var responseString = ""
        responseString = when {
            uri.contains("planets") -> getMockPlanets
            else -> responseString
        }
        responseString = when {
            uri.contains("40237774") -> getMockPlanetStuctures
            else -> responseString
        }

        return Response.Builder()
                .request(chain.request())
                .code(200)
                .protocol(Protocol.HTTP_1_1)
                .message(responseString)
                .body(ResponseBody.create(MediaType.parse("application/json"),
                        responseString.toByteArray()))
                .addHeader("content-type", "application/json")
                .build()

//        return chain.request()
//                .newBuilder()
//                .code(200)
//                .protocol(Protocol.HTTP_1_1)
//                .message(responseString)
//                .body(ResponseBody.create(MediaType.parse("application/json"),
//                        responseString.toByteArray()))
//                .addHeader("content-type", "application/json")
//                .build()
//        } else {
//            //just to be on safe side.
//            throw IllegalAccessError("MockInterceptor is only meant for Testing Purposes and " +
//                    "bound to be used only with DEBUG mode")
//        }
    }

}

const val getMockPlanets = """
[
  {
    "last_update": "2017-07-20T09:38:30Z",
    "num_pins": 11,
    "owner_id": 92002067,
    "planet_id": 40237774,
    "planet_type": "storm",
    "solar_system_id": 30003752,
    "upgrade_level": 5
  },
  {
    "last_update": "2017-07-20T09:38:56Z",
    "num_pins": 11,
    "owner_id": 92002067,
    "planet_id": 40237775,
    "planet_type": "lava",
    "solar_system_id": 30003752,
    "upgrade_level": 5
  },
  {
    "last_update": "2017-07-21T08:47:33Z",
    "num_pins": 24,
    "owner_id": 92002067,
    "planet_id": 40237824,
    "planet_type": "barren",
    "solar_system_id": 30003752,
    "upgrade_level": 5
  },
  {
    "last_update": "2017-07-20T09:39:22Z",
    "num_pins": 11,
    "owner_id": 92002067,
    "planet_id": 40237847,
    "planet_type": "temperate",
    "solar_system_id": 30003753,
    "upgrade_level": 5
  },
  {
    "last_update": "2017-07-20T09:39:46Z",
    "num_pins": 11,
    "owner_id": 92002067,
    "planet_id": 40237856,
    "planet_type": "storm",
    "solar_system_id": 30003753,
    "upgrade_level": 5
  }
]
"""
const val getMockPlanetStuctures = """
{
  "links": [
    {
      "destination_pin_id": 1019807294631,
      "link_level": 0,
      "source_pin_id": 1019807294623
    },
    {
      "destination_pin_id": 1019807338948,
      "link_level": 0,
      "source_pin_id": 1019807294621
    },
    {
      "destination_pin_id": 1019807294621,
      "link_level": 0,
      "source_pin_id": 1019702622237
    },
    {
      "destination_pin_id": 1019807294627,
      "link_level": 0,
      "source_pin_id": 1019807294621
    },
    {
      "destination_pin_id": 1019807338950,
      "link_level": 0,
      "source_pin_id": 1019702622237
    },
    {
      "destination_pin_id": 1019807294623,
      "link_level": 0,
      "source_pin_id": 1019702622237
    },
    {
      "destination_pin_id": 1019807340566,
      "link_level": 0,
      "source_pin_id": 1019807338948
    },
    {
      "destination_pin_id": 1019807338954,
      "link_level": 0,
      "source_pin_id": 1019702622237
    },
    {
      "destination_pin_id": 1019807340565,
      "link_level": 0,
      "source_pin_id": 1019807338953
    },
    {
      "destination_pin_id": 1019807338953,
      "link_level": 0,
      "source_pin_id": 1019807294621
    }
  ],
  "pins": [
    {
      "contents": [
        {
          "amount": 40,
          "type_id": 2390
        }
      ],
      "last_cycle_start": "2017-07-16T03:40:00Z",
      "latitude": 1.88849123911,
      "longitude": 3.38853108318,
      "pin_id": 1019807340565,
      "schematic_id": 68,
      "type_id": 2484
    },
    {
      "contents": [
        {
          "amount": 40,
          "type_id": 2390
        }
      ],
      "last_cycle_start": "2017-07-16T01:40:00Z",
      "latitude": 1.88845339993,
      "longitude": 3.34305268418,
      "pin_id": 1019807340566,
      "schematic_id": 68,
      "type_id": 2484
    },
    {
      "last_cycle_start": "2016-12-14T01:02:08Z",
      "latitude": 1.88894420731,
      "longitude": 3.36619425702,
      "pin_id": 1019702622237,
      "type_id": 2550
    },
    {
      "contents": [
        {
          "amount": 430,
          "type_id": 3691
        }
      ],
      "last_cycle_start": "2017-07-13T00:01:30Z",
      "latitude": 1.90106627869,
      "longitude": 3.3659566216,
      "pin_id": 1019807294623,
      "type_id": 2557
    },
    {
      "expiry_time": "2017-07-28T15:38:30Z",
      "extractor_details": {
        "cycle_time": 7200,
        "head_radius": 0.0335378199816,
        "heads": [
          {
            "head_id": 0,
            "latitude": 1.77107866734,
            "longitude": 3.07543968832
          },
          {
            "head_id": 1,
            "latitude": 2.1152418849,
            "longitude": 3.1917297902
          },
          {
            "head_id": 2,
            "latitude": 2.14704160074,
            "longitude": 3.26556591428
          },
          {
            "head_id": 3,
            "latitude": 1.83806410531,
            "longitude": 3.05846620217
          },
          {
            "head_id": 4,
            "latitude": 1.90630508956,
            "longitude": 3.05709968193
          },
          {
            "head_id": 5,
            "latitude": 1.81924100547,
            "longitude": 3.12813590077
          },
          {
            "head_id": 6,
            "latitude": 2.07865609628,
            "longitude": 3.26024123174
          }
        ],
        "product_type_id": 2310,
        "qty_per_cycle": 7449
      },
      "install_time": "2017-07-20T09:38:30Z",
      "last_cycle_start": "2017-07-20T09:38:30Z",
      "latitude": 1.86431499718,
      "longitude": 3.36643186617,
      "pin_id": 1019807294627,
      "type_id": 3067
    },
    {
      "contents": [
        {
          "amount": 1981,
          "type_id": 2309
        }
      ],
      "last_cycle_start": "2017-07-16T02:10:00Z",
      "latitude": 1.88282233228,
      "longitude": 3.35490506667,
      "pin_id": 1019807338948,
      "schematic_id": 123,
      "type_id": 2483
    },
    {
      "last_cycle_start": "2017-07-16T01:40:00Z",
      "latitude": 1.8951405113,
      "longitude": 3.35492559106,
      "pin_id": 1019807338950,
      "schematic_id": 123,
      "type_id": 2483
    },
    {
      "expiry_time": "2017-07-28T15:38:30Z",
      "extractor_details": {
        "cycle_time": 7200,
        "head_radius": 0.0337600186467,
        "heads": [
          {
            "head_id": 0,
            "latitude": 2.19309955253,
            "longitude": 3.25282651613
          },
          {
            "head_id": 1,
            "latitude": 2.16028140988,
            "longitude": 3.18020869549
          },
          {
            "head_id": 2,
            "latitude": 2.11356598657,
            "longitude": 3.12213069219
          },
          {
            "head_id": 3,
            "latitude": 2.12286835327,
            "longitude": 3.25178549962
          },
          {
            "head_id": 4,
            "latitude": 2.078762868,
            "longitude": 3.19046217138
          },
          {
            "head_id": 5,
            "latitude": 2.05469105324,
            "longitude": 3.26312406704
          }
        ],
        "product_type_id": 2309,
        "qty_per_cycle": 7096
      },
      "install_time": "2017-07-20T09:38:30Z",
      "last_cycle_start": "2017-07-20T09:38:30Z",
      "latitude": 1.91315503154,
      "longitude": 3.36548570222,
      "pin_id": 1019807294631,
      "type_id": 3067
    },
    {
      "contents": [
        {
          "amount": 2236,
          "type_id": 2310
        }
      ],
      "last_cycle_start": "2017-07-16T02:10:00Z",
      "latitude": 1.88260168906,
      "longitude": 3.37716606267,
      "pin_id": 1019807338953,
      "schematic_id": 124,
      "type_id": 2483
    },
    {
      "last_cycle_start": "2017-07-16T01:40:00Z",
      "latitude": 1.89495363257,
      "longitude": 3.37714870261,
      "pin_id": 1019807338954,
      "schematic_id": 124,
      "type_id": 2483
    },
    {
      "contents": [
        {
          "amount": 3180,
          "type_id": 2390
        }
      ],
      "last_cycle_start": "2017-07-13T00:07:15Z",
      "latitude": 1.87643026216,
      "longitude": 3.36627555614,
      "pin_id": 1019807294621,
      "type_id": 2561
    }
  ],
  "routes": [
    {
      "content_type_id": 3691,
      "destination_pin_id": 1019807294623,
      "quantity": 5,
      "route_id": 716547680,
      "source_pin_id": 1019807340565,
      "waypoints": [
        1019807338953,
        1019807294621,
        1019702622237
      ]
    },
    {
      "content_type_id": 3691,
      "destination_pin_id": 1019807294623,
      "quantity": 5,
      "route_id": 717233380,
      "source_pin_id": 1019807340566,
      "waypoints": [
        1019807338948,
        1019807294621,
        1019702622237
      ]
    },
    {
      "content_type_id": 2390,
      "destination_pin_id": 1019807294621,
      "quantity": 20,
      "route_id": 781791208,
      "source_pin_id": 1019807338948
    },
    {
      "content_type_id": 2390,
      "destination_pin_id": 1019807294621,
      "quantity": 20,
      "route_id": 781791209,
      "source_pin_id": 1019807338950,
      "waypoints": [
        1019702622237
      ]
    },
    {
      "content_type_id": 2309,
      "destination_pin_id": 1019807338948,
      "quantity": 3000,
      "route_id": 781791210,
      "source_pin_id": 1019807294621
    },
    {
      "content_type_id": 2309,
      "destination_pin_id": 1019807338950,
      "quantity": 3000,
      "route_id": 781791211,
      "source_pin_id": 1019807294621,
      "waypoints": [
        1019702622237
      ]
    },
    {
      "content_type_id": 2310,
      "destination_pin_id": 1019807294621,
      "quantity": 107264,
      "route_id": 833313774,
      "source_pin_id": 1019807294627
    },
    {
      "content_type_id": 2309,
      "destination_pin_id": 1019807294621,
      "quantity": 102176,
      "route_id": 833313775,
      "source_pin_id": 1019807294631,
      "waypoints": [
        1019807294623,
        1019702622237
      ]
    },
    {
      "content_type_id": 2390,
      "destination_pin_id": 1019807340566,
      "quantity": 40,
      "route_id": 781791377,
      "source_pin_id": 1019807294621,
      "waypoints": [
        1019807338948
      ]
    },
    {
      "content_type_id": 2390,
      "destination_pin_id": 1019807340565,
      "quantity": 40,
      "route_id": 787108918,
      "source_pin_id": 1019807294621,
      "waypoints": [
        1019807338953
      ]
    },
    {
      "content_type_id": 3683,
      "destination_pin_id": 1019807340565,
      "quantity": 40,
      "route_id": 787108919,
      "source_pin_id": 1019807294621,
      "waypoints": [
        1019807338953
      ]
    },
    {
      "content_type_id": 3683,
      "destination_pin_id": 1019807340566,
      "quantity": 40,
      "route_id": 787108920,
      "source_pin_id": 1019807294621,
      "waypoints": [
        1019807338948
      ]
    },
    {
      "content_type_id": 3683,
      "destination_pin_id": 1019807294621,
      "quantity": 20,
      "route_id": 781790553,
      "source_pin_id": 1019807338953
    },
    {
      "content_type_id": 3683,
      "destination_pin_id": 1019807294621,
      "quantity": 20,
      "route_id": 781790554,
      "source_pin_id": 1019807338954,
      "waypoints": [
        1019702622237
      ]
    },
    {
      "content_type_id": 2310,
      "destination_pin_id": 1019807338953,
      "quantity": 3000,
      "route_id": 787108604,
      "source_pin_id": 1019807294621
    },
    {
      "content_type_id": 2310,
      "destination_pin_id": 1019807338954,
      "quantity": 3000,
      "route_id": 787108605,
      "source_pin_id": 1019807294621,
      "waypoints": [
        1019702622237
      ]
    }
  ]
}
"""