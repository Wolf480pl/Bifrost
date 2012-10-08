package com.craftfire.bifrost.scripts.cms;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.craftfire.commons.CraftCommons;
import com.craftfire.commons.classes.Version;
import com.craftfire.commons.classes.VersionRange;
import com.craftfire.commons.database.DataRow;
import com.craftfire.commons.database.Results;
import com.craftfire.commons.enums.Encryption;
import com.craftfire.commons.managers.DataManager;

import com.craftfire.bifrost.classes.cms.CMSScript;
import com.craftfire.bifrost.classes.cms.CMSUser;
import com.craftfire.bifrost.classes.general.Group;
import com.craftfire.bifrost.classes.general.ScriptUser;
import com.craftfire.bifrost.enums.Scripts;
import com.craftfire.bifrost.exceptions.UnsupportedMethod;

public class Joomla extends CMSScript {
    private static Random random;

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
    public CMSUser getUser(int userid) throws SQLException {
        if (this.getDataManager().exist("users", "id", userid)) {
            CMSUser user = new CMSUser(this, userid, null, null);
            Results res = this.getDataManager().getResults("SELECT * FROM `" + this.getDataManager().getPrefix() + "users` WHERE `id` = '" + userid + "' LIMIT 1");
            DataRow record = res.getFirstResult();
            if (record != null) {
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

    protected String genSalt() {
        StringBuffer buf = new StringBuffer();
        if (random == null) {
            random = new SecureRandom();
        }
        for (int i = 0; i < 32; ++i) {
            buf.append(Integer.toString(random.nextInt(36), 36));
        }
        return buf.toString();
    }

    @Override
    public void updateUser(ScriptUser user) throws SQLException {
        HashMap<String, Object> data = new HashMap<String, Object>();
        if (user.getPasswordSalt() == null) {
            user.setPasswordSalt(genSalt());
        }
        try {
            CraftCommons.convertHexToString(user.getPassword());
        } catch (NumberFormatException e) {
            user.setPassword(hashPassword(user.getPasswordSalt(), user.getPassword()));
        }
        data.put("name", user.getNickname());
        data.put("username", user.getUsername());
        data.put("email", user.getEmail());
        data.put("password", user.getPassword() + ":" + user.getPasswordSalt());
        if (getVersion().inVersionRange(getVersionRanges()[0])) {
            data.put("usertype", user.getUserTitle());
        }
        data.put("registerDate", user.getRegDate());
        data.put("lastvisitDate", user.getLastLogin());
        if (user.isActivated()) {
            if (getVersion().inVersionRange(getVersionRanges()[0]) || getVersion().inVersionRange(getVersionRanges()[1])) {
                data.put("activation", "");
            } else {
                data.put("activation", "0");
            }
        }
        getDataManager().updateFields(data, "users", "`id` = '" + user.getID() + "'");
    }

    @Override
    public void createUser(ScriptUser user) throws SQLException {
        HashMap<String, Object> data = new HashMap<String, Object>();
        if (user.getPasswordSalt() == null) {
            user.setPasswordSalt(genSalt());
        }
        try {
            CraftCommons.convertHexToString(user.getPassword());
        } catch (NumberFormatException e) {
            user.setPassword(hashPassword(user.getPasswordSalt(), user.getPassword()));
        }
        if (user.getLastLogin() == null) {
            user.setLastLogin(new Date());
        }
        if (user.getRegDate() == null) {
            user.setRegDate(new Date());
        }
        data.put("name", user.getNickname());
        data.put("username", user.getUsername());
        data.put("email", user.getEmail());
        data.put("password", user.getPassword() + ":" + user.getPasswordSalt());
        if (getVersion().inVersionRange(getVersionRanges()[0])) {
            data.put("usertype", user.getUserTitle());
        }
        data.put("registerDate", user.getRegDate());
        data.put("lastvisitDate", user.getLastLogin());
        if (user.isActivated()) {
            if (getVersion().inVersionRange(getVersionRanges()[0]) || getVersion().inVersionRange(getVersionRanges()[1])) {
                data.put("activation", "");
            } else {
                data.put("activation", "0");
            }
        }
        getDataManager().insertFields(data, "users");
        user.setID(getDataManager().getLastID("id", "users"));
    }

    @Override
    public List<Group> getGroups(int limit) throws SQLException, UnsupportedMethod {
        String limitstring = "";
        boolean j15 = getVersion().inVersionRange(getVersionRanges()[0]);
        List<Group> groups = new ArrayList<Group>();
        if (limit > 0) {
            limitstring = " LIMIT 0," + limit;
        }
        Results res = getDataManager().getResults("SELECT `id` FROM `" + getDataManager().getPrefix() + (j15 ? "groups`" : "usergroups`") + limitstring);
        List<DataRow> rows = res.getArray();
        Iterator<DataRow> it = rows.iterator();
        while (it.hasNext()) {
            groups.add(getHandle().getGroup(it.next().getIntField("id")));
        }
        return groups;
    }

    @Override
    public int getGroupID(String group) {
        boolean j15 = getVersion().inVersionRange(getVersionRanges()[0]);
        return getDataManager().getIntegerField((j15 ? "groups" : "usergroups"), "id", "`" + (j15 ? "name" : "title") + "` = '" + group + "'");
    }

    @Override
    public Group getGroup(int groupid) throws SQLException, UnsupportedMethod {
        boolean j15 = getVersion().inVersionRange(getVersionRanges()[0]);
        Results res = getDataManager().getResults("SELECT * FROM " + getDataManager().getPrefix() + (j15 ? "groups" : "usergroups") + "` WHERE `id` = '" + groupid + "' LIMIT 1");
        DataRow record = res.getFirstResult();
        if (record != null) {
            Group group = new Group(this, groupid, record.getStringField(j15 ? "name" : "title"));
            List<ScriptUser> users = new ArrayList<ScriptUser>();
            Results res1;
            if (j15) {
                res1 = getDataManager().getResults("SELECT `id` FROM `" + getDataManager().getPrefix() + "users` WHERE `gid` = '" + groupid + "'");
            } else {
                res1 = getDataManager().getResults("SELECT `user_id` FROM `" + getDataManager().getPrefix() + "user_usergroup_map` WHERE `group_id` = '" + groupid + "'");
            }
            List<DataRow> rows = res1.getArray();
            Iterator<DataRow> it = rows.iterator();
            while (it.hasNext()) {
                users.add(getHandle().getUser(it.next().getIntField(j15 ? "id" : "user_id")));
            }
            group.setUsers(users);
            group.setUserCount(users.size());
            return group;
        }
        return null;
    }

    @Override
    public Group getGroup(String group) throws SQLException, UnsupportedMethod {
        return getHandle().getGroup(getGroupID(group));
    }

    @Override
    public List<Group> getUserGroups(String username) throws SQLException, UnsupportedMethod {
        int userid = getUserID(username);
        List<Group> groups = new ArrayList<Group>();
        boolean j15 = getVersion().inVersionRange(getVersionRanges()[0]);
        Results res;
        if (j15) {
            res = getDataManager().getResults("SELECT `gid` FROM `" + getDataManager().getPrefix() + "users` WHERE `id` = '" + userid + "'");
        } else {
            res = getDataManager().getResults("SELECT `group_id` FROM `" + getDataManager().getPrefix() + "user_usergroup_map` WHERE `user_id` = '" + userid + "'");
        }
        Iterator<DataRow> it = res.getArray().iterator();
        while (it.hasNext()) {
            groups.add(getHandle().getGroup(it.next().getIntField(j15 ? "gid" : "group_id")));
        }
        return groups;
    }

    @Override
    public void updateGroup(Group group) throws SQLException {
        HashMap<String, Object> data = new HashMap<String, Object>();
        boolean j15 = getVersion().inVersionRange(getVersionRanges()[0]);
        data.put(j15 ? "name" : "title", group.getName());
        getDataManager().updateFields(data, j15 ? "groups" : "usergroups", "`id` = '" + group.getID() + "'");
        data.clear();
        List<ScriptUser> users = group.getUsers();
        if (!j15) {
            getDataManager().executeQuery("DELETE FROM `" + getDataManager().getPrefix() + "user_usergroup_map` WHERE `group_id` = '" + group.getID() + "'");
        }
        setGroupUsers(j15, group.getID(), group.getUsers());
    }

    @Override
    public void createGroup(Group group) throws SQLException {
        HashMap<String, Object> data = new HashMap<String, Object>();
        boolean j15 = getVersion().inVersionRange(getVersionRanges()[0]);
        data.put(j15 ? "name" : "title", group.getName());
        getDataManager().insertFields(data, j15 ? "groups" : "usergroups");
        group.setID(getDataManager().getLastID("id", j15 ? "groups" : "usergroups"));
        data.clear();
        setGroupUsers(j15, group.getID(), group.getUsers());
    }

    protected void setGroupUsers(boolean j15, int groupid, List<ScriptUser> users) throws SQLException {
        if (users == null) {
            return;
        }
        HashMap<String, Object> data = new HashMap<String, Object>();
        Iterator<ScriptUser> it = users.iterator();
        while (it.hasNext()) {
            int userid = it.next().getID();
            data.put(j15 ? "gid" : "group_id", groupid);
            if (j15) {
                getDataManager().updateFields(data, "users", "`id` = '" + userid + "'");
            } else {
                data.put("user_id", userid);
                getDataManager().insertFields(data, "user_usergroup_map");
            }
            data.clear();
        }
    }
}