package starmeet.convergentmobile.com.starmeet.Services;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import starmeet.convergentmobile.com.starmeet.Models.Country;

/**
 * Created by alexeysidorov on 14.02.2018.
 */

public class CountryService extends BaseDaoImpl<Country, Integer> {

    public CountryService(ConnectionSource connectionSource, Class<Country> countryClass) throws SQLException {
        super(connectionSource, countryClass);
    }

    //Добавить или обновить логин
    public void addCountry(List<Country> countries) throws SQLException {
            this.create(countries);
    }

    public List<Country> getCountries() throws SQLException {
        return this.queryBuilder().query();
    }


    public boolean getIsCounry() throws SQLException {
        List<Country> countries = this.queryBuilder().query();

        return countries != null && countries.size() > 0;
    }
}