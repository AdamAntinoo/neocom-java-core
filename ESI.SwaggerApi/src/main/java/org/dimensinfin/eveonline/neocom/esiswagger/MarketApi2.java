package org.dimensinfin.eveonline.neocom.esiswagger;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsRegionIdOrders200Ok;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

//Fixes Retrofit errors like
 //A @Path parameter must not come after a @Query. (parameter #2) for method MarketApi.getMarketsRegionIdOrders
public interface MarketApi2 {

  /**
   * List orders in a region
   * Return a list of orders in a region  --- Alternate route: &#x60;/dev/markets/{region_id}/orders/&#x60;  Alternate route: &#x60;/legacy/markets/{region_id}/orders/&#x60;  Alternate route: &#x60;/v1/markets/{region_id}/orders/&#x60;  --- This route is cached for up to 300 seconds
   * @param orderType Filter buy/sell orders, return all orders by default. If you query without type_id, we always return both buy and sell orders. (required)
   * @param regionId Return orders in this region (required)
   * @param datasource The server name you would like data from (optional, default to tranquility)
   * @param page Which page of results to return (optional, default to 1)
   * @param typeId Return orders only for this type (optional)
   * @param userAgent Client identifier, takes precedence over headers (optional)
   * @param xUserAgent Client identifier, takes precedence over User-Agent (optional)
   * @return Call&lt;List<GetMarketsRegionIdOrders200Ok>&gt;
   */
  
  @GET("markets/{region_id}/orders/")
  Call<List<GetMarketsRegionIdOrders200Ok>> getMarketsRegionIdOrders(
          @Path("region_id") Integer regionId, @Query("order_type") String orderType, @Query("datasource") String datasource, @Query("page") Integer page, @Query("type_id") Integer typeId, @Query("user_agent") String userAgent, @Header("X-User-Agent") String xUserAgent
  );

}
