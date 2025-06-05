// ExchangeRateApiService.java
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ExchangeRateApiService {
    @GET("latest")
    Call<ExchangeRateResponse> getLatestRates(@Query("base") String baseCurrency);
}