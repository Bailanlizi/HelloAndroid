// ExchangeRateDbHelper.java
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.Date;

public class ExchangeRateDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ExchangeRate.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "exchange_rates";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_BASE = "base_currency";
    private static final String COLUMN_TARGET = "target_currency";
    private static final String COLUMN_RATE = "rate";
    private static final String COLUMN_DATE = "last_update_date";

    public ExchangeRateDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_BASE + " TEXT,"
                + COLUMN_TARGET + " TEXT,"
                + COLUMN_RATE + " REAL,"
                + COLUMN_DATE + " INTEGER)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // 插入或更新汇率数据
    public void insertOrUpdateRate(String baseCurrency, String targetCurrency, double rate) {
        SQLiteDatabase db = this.getWritableDatabase();

        // 检查是否已存在该货币对记录
        ExchangeRate existingRate = getRate(baseCurrency, targetCurrency);

        ContentValues values = new ContentValues();
        values.put(COLUMN_BASE, baseCurrency);
        values.put(COLUMN_TARGET, targetCurrency);
        values.put(COLUMN_RATE, rate);
        values.put(COLUMN_DATE, new Date().getTime());

        if (existingRate != null) {
            // 更新现有记录
            db.update(TABLE_NAME, values,
                    COLUMN_BASE + " = ? AND " + COLUMN_TARGET + " = ?",
                    new String[]{baseCurrency, targetCurrency});
        } else {
            // 插入新记录
            db.insert(TABLE_NAME, null, values);
        }
        db.close();
    }

    // 获取特定货币对的汇率
    @SuppressLint("Range")
    public ExchangeRate getRate(String baseCurrency, String targetCurrency) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_BASE, COLUMN_TARGET, COLUMN_RATE, COLUMN_DATE},
                COLUMN_BASE + " = ? AND " + COLUMN_TARGET + " = ?",
                new String[]{baseCurrency, targetCurrency},
                null, null, null, "1");

        if (cursor != null && cursor.moveToFirst()) {
            ExchangeRate rate = new ExchangeRate();
            rate.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            rate.baseCurrency = cursor.getString(cursor.getColumnIndex(COLUMN_BASE));
            rate.targetCurrency = cursor.getString(cursor.getColumnIndex(COLUMN_TARGET));
            rate.rate = cursor.getDouble(cursor.getColumnIndex(COLUMN_RATE));
            rate.lastUpdateDate = new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE)));
            cursor.close();
            return rate;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    // 检查是否是今天更新的
    public static boolean isUpdatedToday(Date lastUpdateDate) {
        if (lastUpdateDate == null) return false;

        Date today = new Date();
        return lastUpdateDate.getYear() == today.getYear() &&
                lastUpdateDate.getMonth() == today.getMonth() &&
                lastUpdateDate.getDate() == today.getDate();
    }
}