package com.gamecard.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by bridgeit on 12/7/16.
 */

public class GameResponseModel extends RealmObject{

    private Integer id;

    private String gametittle;

    private String category;

    private String version;

    private String Size;

    private String gamedate;

    @PrimaryKey
    private String packagename;

    private String description;

    private Boolean isgame;

    public GameResponseModel(){

    }

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The gametittle
     */
    public String getGametittle() {
        return gametittle;
    }

    /**
     *
     * @param gametittle
     * The gametittle
     */
    public void setGametittle(String gametittle) {
        this.gametittle = gametittle;
    }

    /**
     *
     * @return
     * The category
     */
    public String getCategory() {
        return category;
    }

    /**
     *
     * @param category
     * The category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     *
     * @return
     * The version
     */
    public String getVersion() {
        return version;
    }

    /**
     *
     * @param version
     * The version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     *
     * @return
     * The size
     */
    public String getSize() {
        return Size;
    }

    /**
     *
     * @param size
     * The Size
     */
    public void setSize(String size) {
        this.Size = size;
    }

    /**
     *
     * @return
     * The gamedate
     */
    public String getGamedate() {
        return gamedate;
    }

    /**
     *
     * @param gamedate
     * The gamedate
     */
    public void setGamedate(String gamedate) {
        this.gamedate = gamedate;
    }

    /**
     *
     * @return
     * The packagename
     */
    public String getPackagename() {
        return packagename;
    }

    /**
     *
     * @param packagename
     * The packagename
     */
    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }

    /**
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The isgame
     */
    public Boolean getIsgame() {
        return isgame;
    }

    /**
     *
     * @param isgame
     * The isgame
     */
    public void setIsgame(Boolean isgame) {
        this.isgame = isgame;
    }
}
