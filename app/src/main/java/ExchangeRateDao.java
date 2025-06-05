// ExchangeRateDao.java
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ExchangeRateDao {
    @Query("SELECT * FROM exchange_rates WHERE baseCurrency = :base AND targetCurrency = :target LIMIT 1")
    ExchangeRate getRate(String base, String target);

    @Insert
    void insert(ExchangeRate rate);

    @Update
    void update(ExchangeRate rate);

    @Query("DELETE FROM exchange_rates")
    void deleteAll();
}