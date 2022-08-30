package starmeet.convergentmobile.com.starmeet.Services;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import starmeet.convergentmobile.com.starmeet.Models.Account;

/**
 * Created by alexeysidorov on 23.02.2018.
 */

public class AccountService extends BaseDaoImpl<Account, Integer> {

    public AccountService(ConnectionSource connectionSource, Class<Account> accountClass) throws SQLException {
        super(connectionSource, accountClass);
    }

    //Добавить или обновить логин
    public void addOrUpdateAccount(Account account) throws SQLException {
        Account acc = this.queryBuilder().where().eq("login", account.getLogin()).queryForFirst();
        if (acc == null)
            this.createOrUpdate(account);
        else {
            account.setId(acc.getId());
            this.update(account);
        }
    }

    //Последний логин
    public Account getLastAccount() throws SQLException {
        List<Account> accounts = this.queryBuilder().orderBy("lastDate", false).query();
        Account account = accounts == null || accounts.size() == 0 ? null : accounts.get(0);

       // if (account == null) return null;

        //int expires = account.getExpiresIn();
       // Date time = new Date((long) expires * 1000);

       // if (time.getTime() > 500)
        //    return account;

       // account.setAuth(false);
       // addOrUpdateAccount(account);
        return account;
    }

    //сброс флага автологина
    public void clearAuth() throws SQLException {
        Account acc = getLastAccount();
        if (acc != null && acc.getId() != 0) {
            acc.setAuth(false);
            this.createOrUpdate(acc);
        }
    }
}