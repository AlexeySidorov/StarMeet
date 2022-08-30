package starmeet.convergentmobile.com.starmeet.Models;


import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by xaycc on 14.11.2018.
 */

@DatabaseTable(tableName = "country")
public class Country {

    @SerializedName("D")
    @DatabaseField
    public int id;

    @DatabaseField(columnName = "countryId", generatedId = true)
    public int countryId;

    @SerializedName("Code")
    @DatabaseField
    public String code;

    @SerializedName("Title")
    @DatabaseField
    public String title;

    @DatabaseField(foreign = true)
    private Country country;
}
