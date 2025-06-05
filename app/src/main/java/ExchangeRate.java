// ExchangeRate.java
import java.util.Date;

public class ExchangeRate {
    public int id;
    public String baseCurrency;
    public String targetCurrency;
    public double rate;
    public Date lastUpdateDate;

    public boolean isUpdatedToday() {
        return ExchangeRateDbHelper.isUpdatedToday(lastUpdateDate);
    }
}