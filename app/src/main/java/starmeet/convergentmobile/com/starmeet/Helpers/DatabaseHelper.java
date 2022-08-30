package starmeet.convergentmobile.com.starmeet.Helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import starmeet.convergentmobile.com.starmeet.Models.Account;
import starmeet.convergentmobile.com.starmeet.Models.Country;
import starmeet.convergentmobile.com.starmeet.Services.AccountService;
import starmeet.convergentmobile.com.starmeet.Services.CountryService;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();
    //с каждым увеличением версии, при нахождении в устройстве БД с предыдущей версией будет выполнен метод onUpgrade();
    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "starmeet.sqlite";
    private AccountService accountService;
    private CountryService countryService;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Выполняется, когда файл с БД не найден на устройстве
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Account.class);
            TableUtils.createTable(connectionSource, Country.class);
        } catch (SQLException e) {
            Log.e(TAG, "error creating DB " + DATABASE_NAME);
            throw new RuntimeException(e);
        }
    }

    //Выполняется, когда БД имеет версию отличную от текущей
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer,
                          int newVer) {

        if(newVer == 6) {
            try {
                TableUtils.createTable(connectionSource, Country.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //синглтон для AccountService
    public AccountService getAccountService() throws SQLException, java.sql.SQLException {
        if (accountService == null) {
            accountService = new AccountService(getConnectionSource(), Account.class);
        }
        return accountService;
    }

    //синглтон для CountryService
    public CountryService getCountryService() throws SQLException, java.sql.SQLException {
        if (countryService == null) {
            countryService = new CountryService(getConnectionSource(), Country.class);
        }
        return countryService;
    }

    //выполняется при закрытии приложения
    @Override
    public void close() {
        super.close();

    }
}
