package com.craftfire.bifrost.scripts.cms;

import java.sql.SQLException;

import com.craftfire.commons.CraftCommons;
import com.craftfire.commons.classes.Version;
import com.craftfire.commons.classes.VersionRange;
import com.craftfire.commons.database.DataRow;
import com.craftfire.commons.database.Results;
import com.craftfire.commons.enums.Encryption;
import com.craftfire.commons.managers.DataManager;

import com.craftfire.bifrost.classes.cms.CMSScript;
import com.craftfire.bifrost.classes.cms.CMSUser;
import com.craftfire.bifrost.enums.Scripts;
import com.craftfire.bifrost.exceptions.UnsupportedMethod;

public class Joomla extends CMSScript {

    /**
     * Default constructor for Joomla.
     *
     * @param script       the {@link Scripts} enum
     * @param version      the version of the script
     * @param dataManager  the {@link DataManager}
     */
    public Joomla(Scripts script, String version, DataManager dataManager) {
        super(script, version, dataManager);
        setScriptName("joomla");
        setShortName("jml");
        setVersionRanges(new VersionRange[] { new VersionRange("1.5", "1.5"), new VersionRange("1.6", "1.7.5"), new VersionRange("2.5", "2.5.7"), new VersionRange("3.0", "3.0") });
    }

    // Start Generic Methods

    @Override
    public Version getLatestVersion() {
        return getVersionRanges()[3].getMax();
    }

    @Override
    public boolean authenticate(String username, String password) {
        String dbField = getDataManager().getStringField("users", "password", "`username` = '" + username + "'");
        if (dbField == null) {
            return false;
        }
        String[] array = dbField.split(":", 2);
        String hash = array[0];
        String salt = "";
        if (array.length > 1) {
            salt = array[1];
        }
        return hash.equals(hashPassword(salt, password));
    }

    @Override
    public String hashPassword(String salt, String password) {
        return CraftCommons.convertStringToHex(CraftCommons.encrypt(Encryption.MD5, password + salt));
    }

    @Override
    public String getUsername(int userid) {
        return getDataManager().getStringField("users", "username", "`id` = '" + userid + "'");
    }

    @Override
    public int getUserID(String username) {
        return getDataManager().getIntegerField("users", "id", "`username` = '" + username + "'");
    }

    @Override
    public CMSUser getLastRegUser() throws UnsupportedMethod, SQLException {
        return getHandle().getUser(getDataManager().getIntegerField("SELECT `id` FROM `" + getDataManager().getPrefix() + "users` ORDER BY `registerDate` DESC LIMIT 1"));
    }

    @Override
    public CMSUser getUser(String username) throws UnsupportedMethod, SQLException {
        return getHandle().getUser(getUserID(username));
    }

    @Override
    public CMSUser getUser(int userid) throws SQLException, UnsupportedMethod {
        if (this.getDataManager().exist("users", "id", userid)) {
            CMSUser user = new CMSUser(this, userid, null, null);
            Results res = this.getDataManager().getResults("SELECT * FROM `" + this.getDataManager().getPrefix() + "users` WHERE `id` = '" + userid + "' LIMIT 1");
            if (res != null && res.getFirstResult() != null) {
                DataRow record = res.getFirstResult();
                String activation = record.getStringField("activation");
                user.setActivated(activation.isEmpty() || activation.equals("0"));
                user.setEmail(record.getStringField("email"));
                user.setLastLogin(record.getDateField("lastvisitDate"));
                user.setNickname(record.getStringField("name"));
                String pwhash = record.getStringField("password");
                if (pwhash != null) {
                    String[] array = pwhash.split(":", 2);
                    user.setPassword(array[0]);
                    user.setPasswordSalt("");
                    if (array.length > 1) {
                        user.setPasswordSalt(array[1]);
                    }
                }
                user.setRegDate(record.getDateField("registerDate"));
                user.setUsername(record.getStringField("username"));
                if (getVersion().inVersionRange(getVersionRanges()[0])) {
                    user.setUserTitle(record.getStringField("usertype"));
                }
                return user;
            }
        }
        return null;
    }
}