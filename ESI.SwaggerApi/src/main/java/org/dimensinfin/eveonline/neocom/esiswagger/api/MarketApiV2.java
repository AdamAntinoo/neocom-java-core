package org.dimensinfin.eveonline.neocom.esiswagger.api;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsRegionIdOrders200Ok;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;
/**
 * This new version api is required to fix a Retrofit error or parameter ordering.
 * A @Path parameter must not come after a @Query. (parameter #2) for method MarketApi.getMarketsRegionIdOrders
 */
public interface MarketApiV2 {
	/**
	 * List orders in a region
	 * Return a list of orders in a region  --- Alternate route: &#x60;/dev/markets/{region_id}/orders/&#x60;  Alternate route: &#x60;/legacy/markets/{region_id}/orders/&#x60;  Alternate route: &#x60;/v1/markets/{region_id}/orders/&#x60;  --- This route is cached for up to 300 seconds
	 * @param regionId Return orders in this region (required)
	 * @param orderType Filter buy/sell orders, return all orders by default. If you query without type_id, we always return both buy and sell orders. (required)
	 * @param datasource The server name you would like data from (optional, default to tranquility)
	 * @param page Which page of results to return (optional, default to 1)
	 * @param typeId Return orders only for this type (optional)
	 * @param ifNoneMatch ETag from a previous request. A 304 will be returned if this matches the current ETag (optional)
	 * @return Call&lt;List<GetMarketsRegionIdOrders200Ok>&gt;
	 */
	@GET("markets/{region_id}/orders/")
	Call<List<GetMarketsRegionIdOrders200Ok>> getMarketsRegionIdOrders(
			@Path("region_id") Integer regionId, @Query("order_type") String orderType, @Query("datasource") String datasource, @Query("page") Integer page, @Query("type_id") Integer typeId, @Header("If-None-Match") String ifNoneMatch
	);
}
