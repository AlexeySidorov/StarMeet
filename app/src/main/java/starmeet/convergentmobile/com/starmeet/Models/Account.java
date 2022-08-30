package starmeet.convergentmobile.com.starmeet.Models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by alexeysidorov on 23.02.2018.
 */

@DatabaseTable(tableName = "account")
public class Account {
    //ид
    @DatabaseField(columnName = "id", generatedId = true)
    private int id;

    //Тип юзера
    @DatabaseField
    private int roleId;

    //Дата
    @DatabaseField(columnName = "LASTDATE", dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
    private Date lastDate;

    //Логин
    @DatabaseField
    private String login;

    //Пароль
    @DatabaseField
    private String password;

    //Nick name
    @DatabaseField
    private String userName;

    //Токен
    @DatabaseField
    private String token;

    //Refresh token
    @DatabaseField
    private String refreshToken;

    //Срок жизни
    @DatabaseField
    private int expiresIn;

    //Флаг авто логина
    @DatabaseField
    private boolean isAuth;

    @DatabaseField(foreign = true)
    private Account account;

    // id
    public int getId() {
        return id;
    }

    // set id user
    public void setId(int id) {
        this.id = id;
    }

    // get login user
    public String getLogin() {
        return login;
    }

    // set login user
    public void setLogin(String login) {
        this.login = login;
    }

    // Get password
    public String getPassword() {
        return password;
    }

    // Set password
    public void setPassword(String password) {
        this.password = password;
    }

    // Get flag auto auth
    public boolean getAuth() {
        return isAuth;
    }

    // Set flag auto auth
    public void setAuth(boolean isAuth) {
        this.isAuth = isAuth;
    }

    // Get дату последнего входа
    public Date getlastDate() {
        return lastDate;
    }

    // Set дату последнего входа
    public void setlastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

    //Get token
    public String getToken() {
        return token;
    }

    //Set token
    public void setToken(String token) {
        this.token = token;
    }

    //Get token
    public String getRefreshToken() {
        return refreshToken;
    }

    //Set token
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    //Get expires date
    public int getExpiresIn() {
        return expiresIn;
    }

    //Set expires date
    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
