/*
 * This file is part of Bifrost.
 *
 * Copyright (c) 2011-2012, CraftFire <http://www.craftfire.com/>
 * Bifrost is licensed under the GNU Lesser General Public License.
 *
 * Bifrost is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bifrost is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.craftfire.bifrost.scripts.cms;

import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.craftfire.bifrost.Bifrost;
import com.craftfire.bifrost.ScriptHandle;
import com.craftfire.bifrost.classes.Ban;
import com.craftfire.bifrost.classes.Gender;
import com.craftfire.bifrost.classes.Group;
import com.craftfire.bifrost.classes.Post;
import com.craftfire.bifrost.classes.PrivateMessage;
import com.craftfire.bifrost.classes.Script;
import com.craftfire.bifrost.classes.ScriptUser;
import com.craftfire.bifrost.classes.Thread;
import com.craftfire.bifrost.enums.CacheGroup;
import com.craftfire.bifrost.enums.Scripts;
import com.craftfire.bifrost.exceptions.UnsupportedFunction;
import com.craftfire.commons.CraftCommons;
import com.craftfire.commons.database.DataRow;
import com.craftfire.commons.database.Results;
import com.craftfire.commons.enums.Encryption;
import com.craftfire.commons.managers.DataManager;

public class WordPress extends Script {
    private final String scriptName = "wordpress";
    private final String shortName = "wp";
    private final String encryption = "phpass";

    /* TODO: Does it work with other versions? */
    private final String[] versionRanges = { "3.4.0", "3.4.1" };

    private String currentUsername = null;
    private DataManager dataManager = null;
    private ScriptHandle handle = null;
    private boolean init = false;

    public WordPress(Scripts script, String version, DataManager dataManager) {
        super(script, version, dataManager);
        this.dataManager = getDataManager();
        this.handle = Bifrost.getInstance().getScriptAPI().getHandle();
    }

    public void init() {
        // TODO: Remove this method.
        if (this.init) {
            return;
        }
        /*
         * Do some lazy initialization stuff. Not necessary anymore, I left it
         * just in case.
         */
        this.init = true;
    }

    @Override
    public String[] getVersionRanges() {
        return this.versionRanges;
    }

    @Override
    public String getLatestVersion() {
        /* TODO */
        return this.versionRanges[this.versionRanges.length - 1];
    }

    @Override
    public String getEncryption() {
        return this.encryption;
    }

    @Override
    public String getScriptName() {
        return this.scriptName;
    }

    @Override
    public String getScriptShortname() {
        return this.shortName;
    }

    @Override
    public boolean authenticate(String username, String password) {
        init();
        String hash = this.dataManager.getStringField("users", "user_pass",
                "`user_login` = '" + username + "'");
        if (hash == null) {
            return false;
        }
        return hashPassword(hash, password).equals(hash);
    }

    @Override
    public String hashPassword(String salt, String password) {
        String hash = CraftCommons.encrypt(Encryption.PHPASS, password, salt);
        if (hash.startsWith("*")) {
            hash = CraftCommons.encrypt(CraftCommons.unixHashIdentify(salt),
                    password, salt);
        }
        return hash;
    }

    @Override
    public String getUsername(int userid) {
        init();
        return this.dataManager.getStringField("users", "user_login",
                "`ID` = '" + userid + "'");
    }

    @Override
    public int getUserID(String username) {
        init();
        return this.dataManager.getIntegerField("users", "ID",
                "`user_login` = '" + username + "'");
    }

    @Override
    public ScriptUser getLastRegUser() throws UnsupportedFunction, SQLException {
        init();
        return this.handle
                .getUser(this.dataManager.getLastID("ID", "users"));
    }

    @Override
    public ScriptUser getUser(String username) throws UnsupportedFunction,
            SQLException {
        init();
        return this.handle.getUser(getUserID(username));
    }

    @Override
    public ScriptUser getUser(int userid) {
        init();
        if (this.dataManager.exist("users", "ID", userid)) {
            ScriptUser user = new ScriptUser(this, userid, null, null);
            HashMap<String, Object> array = this.dataManager
                    .getArray("SELECT * FROM `" + this.dataManager.getPrefix()
                            + "users` WHERE `ID` = '" + userid + "' LIMIT 1");
            if (array.size() > 0) {
                String lastlogin;
                String activation = this.dataManager
                        .getStringField(
                                "usermeta",
                                "meta_value",
                                "`user_id` = '"
                                        + userid
                                        + "' AND `meta_key` = 'uae_user_activation_code'");
                if (activation == null || activation.equalsIgnoreCase("active")) {
                    user.setActivated(true);
                } else {
                    user.setActivated(false);
                }
                user.setEmail(array.get("user_email").toString());
                user.setGender(Gender.UNKNOWN);
                if (array.get("user_registered") instanceof Date) {
                    user.setRegDate((Date) array.get("user_registered"));
                }
                user.setPassword(array.get("user_pass").toString());
                user.setUsername(array.get("user_login").toString());
                user.setAvatarURL("http://www.gravatar.com/avatar/"
                        + CraftCommons.encrypt(Encryption.MD5,
                                array.get("user_email").toString()
                                        .toLowerCase()));
                user.setFirstName(this.dataManager.getStringField("usermeta",
                        "meta_value", "`user_id` = '" + user.getID()
                                + "' AND `meta_key` = 'first_name'"));
                user.setLastName(this.dataManager.getStringField("usermeta",
                        "meta_value", "`user_id` = '" + user.getID()
                                + "' AND `meta_key` = 'last_name'"));
                user.setRealName(user.getFirstName() + user.getLastName());
                user.setNickname(this.dataManager.getStringField("usermeta",
                        "meta_value", "`user_id` = '" + user.getID()
                                + "' AND `meta_key` = 'nickname'"));
                lastlogin = this.dataManager.getStringField("usermeta",
                        "meta_value", "`user_id` = '" + user.getID()
                                + "' AND `meta_key` = 'last_user_login'");
                if (!CraftCommons.isLong(lastlogin)) {
                    lastlogin = this.dataManager.getStringField("usermeta",
                            "meta_value", "`user_id` = '" + user.getID()
                                    + "' AND `meta_key` = 'wp-last-login'");
                }
                if (CraftCommons.isLong(lastlogin)) {
                    user.setLastLogin(new java.util.Date(Long
                            .parseLong(lastlogin)));
                }
                return user;
            }
        }
        return null;
    }

    @Override
    public void updateUser(ScriptUser user) throws SQLException {
        init();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("user_login", user.getUsername());
        data.put("user_email", user.getEmail());
        if (user.getRegDate() != null) {
            data.put("user_registered", new Date(user.getRegDate().getTime()));
        }
        if (CraftCommons.unixHashIdentify(user.getPassword()) == null) {
            user.setPassword(hashPassword(null, user.getPassword()));
            data.put("user_pass", user.getPassword());
        }
        this.dataManager.updateFields(data, "users", "`ID` = '" + user.getID()
                + "'");
        data.clear();

        data.put("meta_value", user.getNickname());
        this.dataManager.updateFields(data, "usermeta",
                "`user_id` = '" + user.getID()
                        + "' AND `meta_key` = 'nickname'");
        data.put("meta_value", user.getFirstName());
        this.dataManager.updateFields(data, "usermeta",
                "`user_id` = '" + user.getID()
                        + "' AND `meta_key` = 'first_name'");
        data.put("meta_value", user.getLastName());
        this.dataManager.updateFields(data, "usermeta",
                "`user_id` = '" + user.getID()
                        + "' AND `meta_key` = 'last_name'");
        if (user.getLastLogin() != null) {
            data.put("meta_value",
                    String.valueOf(user.getLastLogin().getTime()));
            this.dataManager.updateFields(data, "usermeta", "`user_id` = '"
                    + user.getID() + "' AND `meta_key` = 'last_user_login'");
            this.dataManager.updateFields(data, "usermeta", "`user_id` = '"
                    + user.getID() + "' AND `meta_key` = 'wp-last-login'");
        }
        data.clear();
        // TODO: Should we skip setting groups if no groups are cached?
        try {
            setUserGroups(user.getUsername(), user.getGroups());
        } catch (UnsupportedFunction e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createUser(ScriptUser user) throws SQLException {
        init();
        HashMap<String, Object> data;
        if (CraftCommons.unixHashIdentify(user.getPassword()) == null) {
            user.setPassword(hashPassword(null, user.getPassword()));
        }
        user.setLastLogin(new java.util.Date());
        user.setRegDate(new java.util.Date());
        data = new HashMap<String, Object>();
        data.put("user_login", user.getUsername());
        data.put("user_pass", user.getPassword());
        data.put("user_nicename", user.getUsername().toLowerCase());
        data.put("user_email", user.getEmail());
        data.put("user_registered", user.getRegDate());
        data.put("user_status", 0);
        data.put("display_name", user.getUsername());
        this.dataManager.insertFields(data, "users");
        data.clear();
        user.setID(this.dataManager.getLastID("ID", "users"));
        data.put("user_id", user.getID());
        data.put("meta_key", "nickname");
        data.put("meta_value", user.getNickname());
        this.dataManager.insertFields(data, "usermeta");
        data.put("meta_key", "first_name");
        data.put("meta_value", user.getFirstName());
        this.dataManager.insertFields(data, "usermeta");
        data.put("meta_key", "last_name");
        data.put("meta_value", user.getLastName());
        this.dataManager.insertFields(data, "usermeta");
        data.put("meta_key", "rich_editing");
        data.put("meta_value", true);
        this.dataManager.insertFields(data, "usermeta");
        data.put("meta_key", "comment_shortcuts");
        data.put("meta_value", false);
        this.dataManager.insertFields(data, "usermeta");
        data.put("meta_key", "admin_color");
        data.put("meta_value", "fresh");
        this.dataManager.insertFields(data, "usermeta");
        data.put("meta_key", "show_admin_bar_front");
        data.put("meta_value", true);
        this.dataManager.insertFields(data, "usermeta");
        data.put("meta_key", "use_ssl");
        data.put("meta_value", 0);
        this.dataManager.insertFields(data, "usermeta");
        data.put("meta_key", "default_password_nag");
        data.put("meta_value", 1);
        this.dataManager.insertFields(data, "usermeta");
        data.put("meta_key", "wp_user_level");
        data.put("meta_value", 0);
        this.dataManager.insertFields(data, "usermeta");
        data.put("meta_key", "wp_capabilities");

        // TODO: Should we add some default for groups?
        try {
            setUserGroups(user.getUsername(), user.getGroups());
        } catch (UnsupportedFunction e) {
            e.printStackTrace();
        }
        // data.put("meta_value", "a:1:{s:10:\"subscriber\";s:1:\"1\";}");

        this.dataManager.insertFields(data, "usermeta");
    }

    @Override
    public List<Group> getGroups(int limit) throws UnsupportedFunction,
            SQLException {
        return getGroups(limit, false);
    }

    public List<Group> getGroups(int limit, boolean namesonly)
            throws UnsupportedFunction, SQLException {
        init();
        List<Group> groups = new ArrayList<Group>();
        if (limit > getGroupCount() | limit <= 0) {
            limit = getGroupCount();
        }

        // GroupID 0 might be used for none group if needed.
        for (int i = 1; i <= limit; ++i) {
            if (namesonly) {
                groups.add(getGroup(i, true));
            } else {
                groups.add(this.handle.getGroup(i));
            }
        }
        return groups;
    }

    @Override
    public Group getGroup(int groupid) throws UnsupportedFunction, SQLException {
        return getGroup(groupid, false);
    }

    public Group getGroup(int groupid, boolean namesonly)
            throws UnsupportedFunction, SQLException {
        String groupname = "";
        Group group;
        switch (groupid) {
        case 1:
            groupname = "Subscriber";
            break;
        case 2:
            groupname = "Contributor";
            break;
        case 3:
            groupname = "Author";
            break;
        case 4:
            groupname = "Editor";
            break;
        case 5:
            groupname = "Administrator";
            break;
        case 6:
            groupname = "Super Admin";
            break;
        default:
            return null;
        }
        group = new Group(this, groupid, groupname);
        if (namesonly) {
            return group;
        }
        init();
        List<ScriptUser> userlist = new ArrayList<ScriptUser>();
        if (groupid == 6) {
            if (this.dataManager.exist("sitemeta", "meta_key", "site_admins")) {
                String admins = this.dataManager.getStringField("sitemeta",
                        "meta_value", "`meta_key` = 'site_admins'");
                Map<Object, String> adminmap = (Map<Object, String>) CraftCommons
                        .getUtil().phpUnserialize(admins);
                Iterator<String> I = adminmap.values().iterator();
                while (I.hasNext()) {
                    userlist.add(this.handle.getUser(I.next()));
                }
            } else {
                return null;
            }
        } else {
            Results results = this.dataManager
                    .getResults("SELECT `meta_value`, `user_id` FROM `"
                            + this.dataManager.getPrefix()
                            + "usermeta` WHERE `meta_key` = 'wp_capabilities'");
            List<DataRow> records = results.getArray();
            Iterator<DataRow> I = records.iterator();
            while (I.hasNext()) {
                DataRow d = I.next();
                String capabilities = d.getStringField("meta_value");
                int userid = d.getIntField("user_id");
                Map<String, String> capmap = null;
                try {
                    capmap = (Map<String, String>) CraftCommons.getUtil()
                            .phpUnserialize(capabilities);
                } catch (IllegalStateException e) {
                    continue;
                }
                String gname = groupname.toLowerCase();
                if (capmap.containsKey(gname) && capmap.get(gname).equals("1")) {
                    userlist.add(this.handle.getUser(userid));
                }
            }
            group.setUsers(userlist);
            group.setUserCount(userlist.size());
        }
        return group;
    }

    @Override
    public Group getGroup(String group) throws UnsupportedFunction,
            SQLException {
        init();
        List<Group> groups = getGroups(0, true);
        for (Group grp : groups) {
            if (grp.getName().equalsIgnoreCase(group)) {
                return this.handle.getGroup(grp.getID());
            }
        }
        return null;
    }

    @Override
    public List<Group> getUserGroups(String username)
            throws UnsupportedFunction, SQLException {
        init();
        int userid = this.handle.getUserID(username);
        String capabilities = this.dataManager.getStringField("usermeta",
                "meta_value",
                "`meta_key` = 'wp_capabilities' AND `user_id` = '" + userid
                        + "'");
        Map<Object, Object> capmap = null;
        if (capabilities != null && !capabilities.isEmpty()) {
            capmap = (Map<Object, Object>) CraftCommons.getUtil()
                    .phpUnserialize(capabilities);
        }
        List<Group> allGroups = this.handle.getGroups(0);
        List<Group> uGroups = new ArrayList<Group>();
        Iterator<Group> I = allGroups.iterator();
        while (I.hasNext()) {
            Group g = I.next();
            if (g.getID() == 6) {
                if (this.dataManager.exist("sitemeta", "meta_key",
                        "site_admins")) {
                    String admins = this.dataManager.getStringField("sitemeta",
                            "meta_value", "`meta_key` = 'site_admins'");
                    Map<Object, Object> adminmap = (Map<Object, Object>) CraftCommons
                            .getUtil().phpUnserialize(admins);
                    if (adminmap.containsValue(username)) {
                        uGroups.add(g);
                    }
                }
            } else if (capmap != null) {
                String gname = g.getName().toLowerCase();
                if (capmap.containsKey(gname) && capmap.get(gname).equals("1")) {
                    uGroups.add(g);
                }
            }
        }
        return uGroups;
    }

    public void setUserGroups(String username, List<Group> groups)
            throws SQLException {
        init();
        int userid = this.getUserID(username);
        Map<String, String> capmap = new HashMap<String, String>();
        List<String> adminlist = null;
        if (this.dataManager.exist("sitemeta", "meta_key", "site_admins")) {
            String admins = this.dataManager.getStringField("sitemeta",
                    "meta_value", "`meta_key` = 'site_admins'");
            Map<Object, String> adminmap = (Map<Object, String>) CraftCommons
                    .getUtil().phpUnserialize(admins);
            adminlist = new ArrayList<String>();
            Iterator<Object> I1 = adminmap.keySet().iterator();
            while (I1.hasNext()) {
                adminlist.add(adminmap.get(I1.next()));
            }
            adminlist.remove(username);
        }
        Iterator<Group> I = groups.iterator();
        while (I.hasNext()) {
            Group g = I.next();
            if (g.getID() == 6) {
                if (adminlist != null) {
                    adminlist.add(username);
                }
            } else {
                String gname = g.getName().toLowerCase();
                capmap.put(gname, "1");
            }
        }
        String capabilities = CraftCommons.getUtil().phpSerialize(capmap);
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("meta_value", capabilities);
        this.dataManager.updateFields(data, "usermeta",
                "`meta_key` = 'wp_capabilities' AND `user_id` = '" + userid
                        + "'");
        if (adminlist != null) {
            String admins = CraftCommons.getUtil().phpSerialize(adminlist);
            data.put("meta_value", admins);
            this.dataManager.updateFields(data, "sitemeta",
                    "`meta_key` = 'site_admins'");
        }
        I = groups.iterator();
        while (I.hasNext()) {
            getCache().getCacheManager()
                    .remove(CacheGroup.GROUP.toString(), I.next().getID());
            // TODO: Should I do this for sure? Should we do this a level
            // higher?
        }
        getCache().getCacheManager()
                .remove(CacheGroup.USER_GROUP.toString(), username);
    }

    @Override
    public void updateGroup(Group group) throws UnsupportedFunction,
            SQLException {
        init();
        if (!getGroup(group.getID(), true).getName().equalsIgnoreCase(
                group.getName())) {
            throw new UnsupportedFunction(
                    "The script doesn't support changing group names or IDs.");
        }
        if (group.getID() == 6) {
            if (this.dataManager.exist("sitemeta", "meta_key", "site_admins")) {
                List<String> adminlist = new ArrayList<String>();
                Iterator<ScriptUser> I = group.getUsers().iterator();
                while (I.hasNext()) {
                    adminlist.add(I.next().getUsername());
                }
                String admins = CraftCommons.getUtil().phpSerialize(adminlist);
                HashMap<String, Object> data = new HashMap<String, Object>();
                data.put("meta_value", admins);
                this.dataManager.updateFields(data, "sitemeta",
                        "`meta_key` = 'site_admins'");
            }
        } else {
            List<ScriptUser> oldUsers = getGroup(group.getID()).getUsers();
            List<ScriptUser> newUsers = group.getUsers();
            List<ScriptUser> unchangedUsers = new ArrayList<ScriptUser>(
                    oldUsers);
            unchangedUsers.retainAll(newUsers);
            oldUsers.removeAll(unchangedUsers);
            newUsers.removeAll(unchangedUsers);
            Iterator<ScriptUser> Iold = oldUsers.iterator();
            while (Iold.hasNext()) {
                ScriptUser u = Iold.next();
                List<Group> groups = this.handle.getUserGroups(u
                        .getUsername());
                groups.remove(group);
                setUserGroups(u.getUsername(), groups);
            }
            Iterator<ScriptUser> Inew = newUsers.iterator();
            while (Inew.hasNext()) {
                ScriptUser u = Iold.next();
                List<Group> groups = this.handle.getUserGroups(u
                        .getUsername());
                groups.add(group);
                setUserGroups(u.getUsername(), groups);
            }
        }
    }

    @Override
    public void createGroup(Group group) throws SQLException,
            UnsupportedFunction {
        // Not supported by WordPress
        super.createGroup(group);
    }

    @Override
    public PrivateMessage getPM(int pmid) throws UnsupportedFunction {
        // Not supported by WordPress
        return super.getPM(pmid);
    }

    @Override
    public List<PrivateMessage> getPMsSent(String username, int limit)
            throws UnsupportedFunction {
        // Not supported by WordPress
        return super.getPMsSent(username, limit);
    }

    @Override
    public List<PrivateMessage> getPMsReceived(String username, int limit)
            throws UnsupportedFunction {
        // Not supported by WordPress
        return super.getPMsReceived(username, limit);
    }

    @Override
    public int getPMSentCount(String username) throws UnsupportedFunction {
        // Not supported by WordPress
        return super.getPMSentCount(username);
    }

    @Override
    public int getPMReceivedCount(String username) throws UnsupportedFunction {
        // Not supported by WordPress
        return super.getPMReceivedCount(username);
    }

    @Override
    public void updatePrivateMessage(PrivateMessage privateMessage)
            throws SQLException, UnsupportedFunction {
        // Not supported by WordPress
        super.updatePrivateMessage(privateMessage);
    }

    @Override
    public void createPrivateMessage(PrivateMessage privateMessage)
            throws SQLException, UnsupportedFunction {
        // Not supported by WordPress
        super.createPrivateMessage(privateMessage);
    }

    @Override
    public int getPostCount(String username) {
        init();
        return this.dataManager.getCount("comments", "`comment_author` = '"
                + username + "'");
    }

    @Override
    public int getTotalPostCount() {
        init();
        return this.dataManager.getCount("comments");
    }

    @Override
    public Post getLastPost() throws NumberFormatException,
            UnsupportedFunction, SQLException {
        init();
        return this.handle.getPost(this.dataManager.getLastID("comment_ID",
                "comments"));
    }

    @Override
    public Post getLastUserPost(String username) throws NumberFormatException,
            UnsupportedFunction, SQLException {
        init();
        return this.handle.getPost(this.dataManager.getLastID("comment_ID",
                "comments", "`comment_author` = '" + username + "'"));
    }

    @Override
    public List<Post> getPosts(int limit) throws NumberFormatException,
            UnsupportedFunction, SQLException {
        init();
        String limitstring = "";
        List<HashMap<String, Object>> array;
        List<Post> posts;
        if (limit > 0) {
            limitstring = " LIMIT 0," + limit;
        }
        array = this.dataManager.getArrayList("SELECT `comment_ID` FROM `"
                + this.dataManager.getPrefix()
                + "comments` ORDER BY `comment_id` ASC" + limitstring);
        posts = new ArrayList<Post>();
        for (HashMap<String, Object> record : array) {
            posts.add(this.handle.getPost(Integer.parseInt(record.get(
                    "comment_ID").toString())));
        }
        return posts;
    }

    @Override
    public List<Post> getPostsFromThread(int threadid, int limit)
            throws UnsupportedFunction, NumberFormatException, SQLException {
        init();
        String limitstring = "";
        List<HashMap<String, Object>> array;
        List<Post> posts;
        if (limit > 0) {
            limitstring = " LIMIT 0," + limit;
        }
        array = this.dataManager.getArrayList("SELECT `comment_ID` FROM `"
                + this.dataManager.getPrefix()
                + "comments` WHERE `comment_post_ID` = " + threadid
                + "' ORDER BY `post_id` ASC" + limitstring);
        posts = new ArrayList<Post>();
        if (array != null) {
            for (HashMap<String, Object> record : array) {
                posts.add(this.handle.getPost(Integer.parseInt(record.get(
                        "comment_ID").toString())));
            }
        }
        return posts;
    }

    @Override
    public Post getPost(int postid) throws NumberFormatException,
            UnsupportedFunction, SQLException {
        init();
        HashMap<String, Object> array = this.dataManager
                .getArray("SELECT * FROM `" + this.dataManager.getPrefix()
                        + "comments` WHERE `comment_ID` = '" + postid + "'");
        if (array.isEmpty()) {
            return null;
        }
        int board = this.dataManager.getStringField("posts", "post_type",
                "`ID` = '" + array.get("comment_post_ID") + "'")
                .equalsIgnoreCase("post") ? 0 : 1;
        Post post = new Post(this, Integer.parseInt(array.get("comment_ID")
                .toString()), Integer.parseInt(array.get("comment_post_ID")
                .toString()), board);
        post.setAuthor(this.handle.getUser(Integer.parseInt(array
                .get(
                "user_id").toString())));
        post.setBody(array.get("comment_content").toString());
        post.setPostDate((Date) array.get("comment_date"));
        return post;
    }

    @Override
    public void updatePost(Post post) throws SQLException {
        init();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("comment_post_ID", post.getThreadID());
        if (post.getAuthor() != null) {
            data.put("comment_author",
                    (post.getAuthor().getNickname() != null) ? post.getAuthor()
                            .getNickname() : post.getAuthor().getUsername());
            data.put("comment_author_email", post.getAuthor().getEmail());
            data.put("comment_author_IP", post.getAuthor().getLastIP());
            data.put("user_id", post.getAuthor().getID());
        }
        data.put("comment_date", new Date(new java.util.Date().getTime()));
        data.put("comment_date_gmt", null /* TODO */);
        data.put("comment_content", post.getBody());
        this.dataManager.updateFields(data, "comments", "`comment_ID` = '"
                + post.getID() + "'");
    }

    @Override
    public void createPost(Post post) throws SQLException {
        init();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("comment_post_ID", post.getThreadID());
        if (post.getAuthor() != null) {
            data.put("comment_author",
                    (post.getAuthor().getNickname() != null) ? post.getAuthor()
                            .getNickname() : post.getAuthor().getUsername());
            data.put("comment_author_email", post.getAuthor().getEmail());
            data.put("comment_author_IP", post.getAuthor().getLastIP());
            data.put("user_id", post.getAuthor().getID());
        }
        data.put("comment_date", new Date(new java.util.Date().getTime()));
        data.put("comment_date_gmt", null /* TODO */);
        data.put("comment_content", post.getBody());
        this.dataManager.insertFields(data, "comments");
        post.setID(this.dataManager.getLastID("comment_ID", "comments"));
    }

    @Override
    public int getThreadCount(String username) {
        init();
        return this.dataManager.getCount("posts", "`post_author` = '"
                + getUserID(username) + "'");
    }

    @Override
    public int getTotalThreadCount() {
        init();
        return this.dataManager.getCount("posts");
    }

    @Override
    public Thread getLastThread() throws UnsupportedFunction,
            NumberFormatException, SQLException {
        init();
        return this.handle.getThread(this.dataManager.getLastID("ID",
                "posts"));
    }

    @Override
    public Thread getLastUserThread(String username)
            throws UnsupportedFunction, NumberFormatException, SQLException {
        init();
        return this.handle.getThread(this.dataManager.getLastID("ID",
                "posts", "`post_author` = '" + getUserID(username) + "'"));
    }

    @Override
    public Thread getThread(int threadid) throws NumberFormatException,
            UnsupportedFunction, SQLException {
        init();
        Thread thread;
        HashMap<String, Object> array = this.dataManager
                .getArray("SELECT * FROM `" + this.dataManager.getPrefix()
                        + "posts` WHERE `ID` = '" + threadid + "'");
        List<HashMap<String, Object>> array1 = this.dataManager
                .getArrayList("SELECT `comment_ID` FROM `"
                        + this.dataManager.getPrefix()
                        + "comments` WHERE `comment_post_ID` = '" + threadid
                        + "' ORDER BY `comment_id` ASC");
        if (array.isEmpty()) {
            return null;
        }
        int firstpost = 0;
        int lastpost = 0;
        if (!array1.isEmpty()) {
            firstpost = Integer.parseInt(array1.get(0).get("comment_ID")
                    .toString());
            lastpost = Integer.parseInt(array1.get(array1.size() - 1)
                    .get("comment_ID").toString());
        }
        int boardid = array.get("post_type").toString()
                .equalsIgnoreCase("post") ? 0 : 1;
        if (boardid > 0
                && !array.get("post_type").toString().equalsIgnoreCase("page")) {
            return null;
        }
        thread = new Thread(this, firstpost, lastpost, threadid, boardid);
        thread.setAuthor(this.handle.getUser(Integer.parseInt(array.get(
                "post_author").toString())));
        thread.setBody(array.get("post_content").toString());
        thread.setSubject("post_title");
        thread.setThreadDate((Date) array.get("post_date"));
        thread.setReplies(Integer.parseInt(array.get("comment_count")
                .toString()));

        String sticky_posts = this.dataManager.getStringField("options",
                "option_value", "`option_name` = 'sticky_posts'");
        Map<Object, Object> stickyMap = (Map<Object, Object>) CraftCommons
                .getUtil().phpUnserialize(sticky_posts);
        thread.setSticky(stickyMap.containsValue(threadid));

        thread.setLocked(array.get("comment_status").toString()
                .equalsIgnoreCase("closed"));
        return thread;
    }

    @Override
    public List<Thread> getThreads(int limit) throws NumberFormatException,
            UnsupportedFunction, SQLException {
        init();
        String limitstring = "";
        List<Thread> threads;
        List<HashMap<String, Object>> array;
        if (limit > 0) {
            limitstring = " LIMIT 0," + limit;
        }
        array = this.dataManager.getArrayList("SELECT `ID` FROM `"
                + this.dataManager.getPrefix() + "posts`" + limitstring);
        threads = new ArrayList<Thread>();
        for (HashMap<String, Object> record : array) {
            threads.add(this.handle.getThread(Integer.parseInt(record.get(
                    "ID").toString())));
        }
        return threads;
    }

    @Override
    public void updateThread(Thread thread) throws SQLException {
        init();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("post_author", thread.getAuthor().getID());
        if (thread.getThreadDate() != null) {
            data.put("post_date", new Date(thread.getThreadDate().getTime()));
        }
        data.put("post_date_gmt", null /* TODO */);
        data.put("post_content", thread.getBody());
        data.put("post_title", thread.getSubject());
        data.put(
                "post_name",
                URLEncoder.encode(thread.getSubject().toLowerCase()
                        .replaceAll(" ", "-")));
        data.put("post_modified", new Date(new java.util.Date().getTime()));
        data.put("post_modified_gmt", null /* TODO */);
        data.put("post_type", thread.getBoardID() == 0 ? "post" : "page");
        data.put("guid", getHomeURL() + "/?p=" + thread.getID());
        data.put("comment_count", thread.getReplies());
        data.put("comment_status", thread.isLocked() ? "closed" : "open");
        this.dataManager.updateFields(data, "posts",
                "`ID` = '" + thread.getID() + "'");
        data.clear();

        String sticky_posts = this.dataManager.getStringField("options",
                "option_value", "`option_name` = 'sticky_posts'");
        Map<Object, Object> stickyMap = (Map<Object, Object>) CraftCommons
                .getUtil().phpUnserialize(sticky_posts);
        List<Object> stickyList = new ArrayList<Object>(stickyMap.values());
        if (thread.isSticky()) {
            if (!stickyList.contains(thread.getID())) {
                stickyList.add(thread.getID());
            }
        } else {
            if (stickyList.contains(thread.getID())) {
                stickyList.remove(stickyList.indexOf(thread.getID()));
            }
        }
        sticky_posts = CraftCommons.getUtil().phpSerialize(stickyList);
        data.put("option_value", sticky_posts);
        this.dataManager.updateFields(data, "options",
                "`option_name` = 'sticky_posts'");
    }

    @Override
    public void createThread(Thread thread) throws SQLException {
        init();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("post_author", thread.getAuthor().getID());
        data.put("post_date", new Date(new java.util.Date().getTime()));
        data.put("post_date_gmt", null /* TODO */);
        data.put("post_content", thread.getBody());
        data.put("post_title", thread.getSubject());
        data.put(
                "post_name",
                URLEncoder.encode(thread.getSubject().toLowerCase()
                        .replaceAll(" ", "-")));
        data.put("post_modified", new Date(new java.util.Date().getTime()));
        data.put("post_modified_gmt", null /* TODO */);
        data.put("post_type", thread.getBoardID() == 0 ? "post" : "page");
        data.put("comment_count", thread.getReplies());
        data.put("post_type", thread.getBoardID() == 0 ? "post" : "page");
        this.dataManager.insertFields(data, "posts");
        thread.setID(this.dataManager.getLastID("ID", "posts"));
        data.clear();
        data.put("guid", getHomeURL() + "/?p=" + thread.getID());
        this.dataManager.updateFields(data, "posts",
                "`ID` = '" + thread.getID() + "'");
        data.clear();

        String sticky_posts = this.dataManager.getStringField("options",
                "option_value", "`option_name` = 'sticky_posts'");
        Map<Object, Object> stickyMap = (Map<Object, Object>) CraftCommons
                .getUtil().phpUnserialize(sticky_posts);
        List<Object> stickyList = new ArrayList<Object>(stickyMap.values());
        if (thread.isSticky()) {
            stickyList.add(thread.getID());
        }
        sticky_posts = CraftCommons.getUtil().phpSerialize(stickyList);
        data.put("option_value", sticky_posts);
        this.dataManager.updateFields(data, "options",
                "`option_name` = 'sticky_posts'");
    }

    @Override
    public int getUserCount() {
        init();
        return this.dataManager.getCount("users");
    }

    @Override
    public int getGroupCount() {
        /*
         * 6 WordPress roles: Subscriber, Contributor, Author, Editor,
         * Administrator, Super Admin
         */
        init();
        if (this.dataManager.exist("sitemeta", "meta_key", "site_admins")) {
            // Super Admin doesn't always exist.
            return 6;
        } else {
            return 5;
        }
    }

    @Override
    public String getHomeURL() {
        init();
        return this.dataManager.getStringField("options", "option_value",
                "`option_name` = 'siteurl'");
    }

    @Override
    public String getForumURL() {
        /* TODO: Should check for BBPress plugin? */
        return getHomeURL();
    }

    @Override
    public List<String> getIPs(String username) throws UnsupportedFunction {
        // Not supported by WordPress
        return super.getIPs(username);
    }

    @Override
    public List<Ban> getBans(int limit) throws UnsupportedFunction {
        // Not supported by WordPress
        return super.getBans(limit);
    }

    @Override
    public void updateBan(Ban ban) throws SQLException, UnsupportedFunction {
        // Not supported by WordPress
        super.updateBan(ban);
    }

    @Override
    public void addBan(Ban ban) throws SQLException, UnsupportedFunction {
        // Not supported by WordPress
        super.addBan(ban);
    }

    @Override
    public int getBanCount() throws UnsupportedFunction {
        // Not supported by WordPress
        return super.getBanCount();
    }

    @Override
    public boolean isBanned(String string) throws UnsupportedFunction {
        // Not supported by WordPress
        return super.isBanned(string);
    }

    @Override
    public boolean isRegistered(String username) {
        init();
        return this.dataManager.exist("users", "user_login", username);
    }
}
