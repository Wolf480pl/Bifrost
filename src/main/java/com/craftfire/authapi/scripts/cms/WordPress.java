/*
 * This file is part of AuthAPI.
 *
 * Copyright (c) 2011-2012, CraftFire <http://www.craftfire.com/>
 * AuthAPI is licensed under the GNU Lesser General Public License.
 *
 * AuthAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AuthAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.craftfire.authapi.scripts.cms;

import java.net.URLEncoder;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.craftfire.authapi.ScriptAPI;
import com.craftfire.authapi.classes.Ban;
import com.craftfire.authapi.classes.Gender;
import com.craftfire.authapi.classes.Group;
import com.craftfire.authapi.classes.Post;
import com.craftfire.authapi.classes.PrivateMessage;
import com.craftfire.authapi.classes.Script;
import com.craftfire.authapi.classes.ScriptUser;
import com.craftfire.authapi.classes.Thread;
import com.craftfire.commons.CraftCommons;
import com.craftfire.commons.DataManager;

public class WordPress extends Script {
    private final String scriptName = "wordpress";
    private final String shortName = "wp";
    private final String encryption = "sha1"; /*TODO*/
    private final String[] versionRanges = {"3.4.0"}; /*TODO: Does it work with other versions?*/
    private final String userVersion;
    private final DataManager dataManager;
    private String currentUsername = null;

    public WordPress(ScriptAPI.Scripts script, String version, DataManager dataManager) {
        super(script, version);
        this.userVersion = version;
        this.dataManager = dataManager;
    }

    public String[] getVersionRanges() {
        return this.versionRanges;
    }

    public String getLatestVersion() {
        /*TODO*/
        return this.versionRanges[0];
    }

    public String getVersion() {
        return this.userVersion;
    }

    public String getEncryption() {
        return this.encryption;
    }

    public String getScriptName() {
        return this.scriptName;
    }

    public String getScriptShortname() {
        return this.shortName;
    }

    public boolean authenticate(String username, String password) {
        /*TODO*/
        return false;
    }

    public String hashPassword(String salt, String password) {
        /*TODO*/
        return null;
    }

    public String getUsername(int userid) {
        return this.dataManager.getStringField("users", "user_login", "`ID` = '" + userid + "'");
    }

    public int getUserID(String username) {
        return this.dataManager.getIntegerField("users", "ID", "`user_login` = '" + username + "'");
    }

    public ScriptUser getLastRegUser() {
        return getUser(this.dataManager.getLastID("ID", "users"));
/*        return getUser(this.dataManager.getIntegerField("SELECT `ID` FROM `" + 
                                        this.dataManager.getPrefix() + 
                                        "users` ORDER BY `ID` ASC LIMIT 1"));*/
    }

    public ScriptUser getUser(String username) {
        return getUser(getUserID(username));
    }

    public ScriptUser getUser(int userid) {
        if (isRegistered(getUsername(userid))) {
            ScriptUser user = new ScriptUser(this, userid, null, null);
            HashMap<String, Object> array = this.dataManager.getArray(
                    "SELECT * FROM `" + this.dataManager.getPrefix() + 
                    "users` WHERE `ID` = '" + userid + "' LIMIT 1");
            if (array.size() > 0) {
                String activation = this.dataManager.getStringField("usermeta",
                                "meta_value", "`user_id` = '" + userid + 
                                "' AND `meta_key1 = 'uae_user_activation_code'");
                if (activation != null && activation.equalsIgnoreCase("active")) {
                    user.setActivated(true);
                } else {
                    user.setActivated(false);
                }
                user.setEmail(array.get("user_email").toString());
                user.setGender(Gender.UNKNOWN);
                user.setGroups(getUserGroups(array.get("user_login")
                                                    .toString()));
                if (array.get("user_registered") instanceof Date)
                    user.setRegDate(new java.util.Date(
                            ((Date) array.get("user_registered")).getTime()));
                user.setPassword(array.get("user_pass").toString());
                user.setUsername(array.get("user_login").toString());
                user.setAvatarURL("http://www.gravatar.com/avatar/" +
                        CraftCommons.md5(array.get("user_email")
                                    .toString().toLowerCase()));
                user.setFirstName(this.dataManager.getStringField("usermeta",
                        "meta_value", "`user_id` = '" + user.getID() +
                        "' AND `meta_key` = 'first_name'"));
                user.setLastName(this.dataManager.getStringField("usermeta",
                        "meta_value", "`user_id` = '" + user.getID() +
                        "' AND `meta_key` = 'last_name'"));
                user.setNickname(this.dataManager.getStringField("usermeta",
                        "meta_value", "`user_id` = '" + user.getID() +
                        "' AND `meta_key` = 'nickname'"));
                String lastlogin = this.dataManager.getStringField("usermeta",
                        "meta_value", "`user_id` = '" + user.getID() +
                        "' AND `meta_key` = 'last_user_login'");
                if (!CraftCommons.isLong(lastlogin))
                    lastlogin = this.dataManager.getStringField("usermeta",
                            "meta_value", "`user_id` = '" + user.getID() +
                            "' AND `meta_key` = 'wp-last-login'");
                if (CraftCommons.isLong(lastlogin))
                    user.setLastLogin(new java.util.Date(
                            Long.parseLong(lastlogin)));
                return user;
            }
        }
        return null;
    }

    public void updateUser(ScriptUser user) throws SQLException {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("user_login",user.getUsername());
        data.put("user_email", user.getEmail());
        data.put("user_registered", new Date(user.getRegDate().getTime()));
        
        if(user.getPassword().length() != 34) {
            /*TODO*/
        }
        
        this.dataManager.updateFields(data, "users", "`ID` = '" + user.getID()
                + "'");
        data.clear();
        
        data.put("meta_value", user.getNickname());
        this.dataManager.updateFields(data, "usermeta", "`user_id` = '" +
                        user.getID() + "' AND `meta_key` = 'nickname'");
        data.put("meta_value", user.getFirstName());
        this.dataManager.updateFields(data, "usermeta", "`user_id` = '" +
                user.getID() + "' AND `meta_key` = 'first_name'");
        data.put("meta_value", user.getLastName());
        this.dataManager.updateFields(data, "usermeta", "`user_id` = '" +
                user.getID() + "' AND `meta_key` = 'last_name'");
        data.put("meta_value", String.valueOf(user.getLastLogin().getTime()));
        this.dataManager.updateFields(data, "usermeta", "`user_id` = '" +
                    user.getID() + "' AND `meta_key` = 'last_user_login'");
        this.dataManager.updateFields(data, "usermeta", "`user_id` = '" +
                    user.getID() + "' AND `meta_key` = 'wp-last-login'");
        data.clear();
    }

    public void createUser(ScriptUser user) throws SQLException {
        /*TODO: Password hashing here*/
        user.setLastLogin(new java.util.Date());
        user.setRegDate(new java.util.Date());
        HashMap<String, Object> data = new HashMap<String, Object>();
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
        data.put("meta_value", "a:1:{s:10:\"subscriber\";s:1:\"1\";}");
        this.dataManager.insertFields(data, "usermeta");
    }

    public List<Group> getGroups(int limit) {
        if (limit > getGroupCount() | limit <= 0) limit = getGroupCount();
        List<Group> groups = new ArrayList<Group>();
        for (int i = 1; i <= limit; ++i) {  //GroupID 0 might be used for none group if needed.
            groups.add(getGroup(i));
        }
        return groups;
    }

    public Group getGroup(int groupid) {
        String groupname = "";
        switch (groupid) {
        case 1: groupname = "Subscriber";
                break;
        case 2: groupname = "Contributor";
                break;
        case 3: groupname = "Author";
                break;
        case 4: groupname = "Editor";
                break;
        case 5: groupname = "Administrator";
                break;
        case 6: groupname = "Super Admin";
                break;                
        default: return null;
        }
        Group group = new Group(this, groupid, groupname);
        //TODO: Add users to group, waiting for php unserialize
        return group;
    }

    public Group getGroup(String group) {
        List<Group> groups = getGroups(0);
        for (Group grp : groups){
            if (grp.getName().equalsIgnoreCase(group)) return grp;
        }
        return null;
    }

    public List<Group> getUserGroups(String username) {
        //TODO, waiting for php unserialize
        return null;
    }

    public void updateGroup(Group group) {
        //TODO, waiting for php unserialize
    }

    public void createGroup(Group group) {
        /*TODO: Delete this function and let the parent throw an exception. Custom groups are not supported in WordPress*/
    }

    public PrivateMessage getPM(int pmid) {
        /*TODO: Delete this function and let the parent throw an exception. Private messages are not supported in WordPress*/
        return null;
    }

    public List<PrivateMessage> getPMsSent(String username, int limit) {
        /*TODO: Delete this function and let the parent throw an exception. Private messages are not supported in WordPress*/
        return null;
    }

    public List<PrivateMessage> getPMsReceived(String username, int limit) {
        /*TODO: Delete this function and let the parent throw an exception. Private messages are not supported in WordPress*/
        return null;
    }

    public int getPMSentCount(String username) {
        /*TODO: Delete this function and let the parent throw an exception. Private messages are not supported in WordPress*/
        return 0;
    }

    public int getPMReceivedCount(String username) {
        /*TODO: Delete this function and let the parent throw an exception. Private messages are not supported in WordPress*/
        return 0;
    }

    public void updatePrivateMessage(PrivateMessage privateMessage) {
        /*TODO: Delete this function and let the parent throw an exception. Private messages are not supported in WordPress*/
    }

    public void createPrivateMessage(PrivateMessage privateMessage) {
        /*TODO: Delete this function and let the parent throw an exception. Private messages are not supported in WordPress*/
    }

    public int getPostCount(String username) {
        return this.dataManager.getCount("comments", "`comment_author` = '" + username
                                                                        + "'");
    }

    public int getTotalPostCount() {
        return this.dataManager.getCount("comments");
    }

    public Post getLastPost() {
        /*TODO*/
        return null;
    }

    public Post getLastUserPost(String username) {
        /*TODO*/
        return null;
    }

    public List<Post> getPosts(int limit) {
        String limitstring = "";
        if (limit > 0)
            limitstring = " LIMIT 0," + limit;
        List<HashMap<String, Object>> array = this.dataManager.getArrayList(
                "SELECT `comment_ID` FROM `" + this.dataManager.getPrefix() +
                "comments` ORDER BY `post_id` ASC" + limitstring);
        List<Post> posts = new ArrayList<Post>();
        for (HashMap<String, Object> record : array) {
            posts.add(getPost(Integer.parseInt(
                    record.get("comment_ID").toString())));
        }
        return posts;
    }

    public List<Post> getPostsFromThread(int threadid, int limit) {
        String limitstring = "";
        if (limit > 0)
            limitstring = " LIMIT 0," + limit;
        List<HashMap<String, Object>> array = this.dataManager.getArrayList(
                "SELECT `comment_ID` FROM `" + this.dataManager.getPrefix() +
                "comments` WHERE `comment_post_ID` = " + threadid +
                "' ORDER BY `post_id` ASC" + limitstring);
        List<Post> posts = new ArrayList<Post>();
        for (HashMap<String, Object> record : array) {
            posts.add(getPost(Integer.parseInt(
                    record.get("comment_ID").toString())));
        }
        return posts;
    }

    public Post getPost(int postid) {
        HashMap<String, Object> array = this.dataManager.getArray(
                "SELECT * FROM `" + this.dataManager.getPrefix() +
                "comments` WHERE `comment_ID` = '" + postid);
        int board = this.dataManager.getStringField("posts", "post_type",
                "`ID` = '" + array.get("comment_post_ID") + "'").equalsIgnoreCase("post") ? 0 : 1 ;
        Post post = new Post(this,
                Integer.parseInt(array.get("comment_ID").toString()),
                Integer.parseInt(array.get("comment_post_ID").toString()),
                board);
        post.setAuthor(getUser(Integer.parseInt(array.get("user_id").toString())));
        post.setBody(array.get("comment_content").toString());
        post.setPostDate(new java.util.Date((
                (Date) array.get("comment_date")).getTime()));
        return post;
    }

    public void updatePost(Post post) throws SQLException {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("comment_post_ID", post.getThreadID());
        if (post.getAuthor() != null) {
            data.put("comment_author", (post.getAuthor().getNickname() != null)
                    ? post.getAuthor().getNickname() :
                        post.getAuthor().getUsername());
            data.put("comment_author_email", post.getAuthor().getEmail());
            data.put("comment_author_IP", post.getAuthor().getLastIP());
            data.put("user_id", post.getAuthor().getID());
        }
        data.put("comment_date", new Date(new java.util.Date().getTime()));
        data.put("comment_date_gtm", null /*TODO*/);
        data.put("comment_content", post.getBody());
        this.dataManager.updateFields(data, "comments", "`comment_ID` = '"
                                                    + post.getID() + "'");
    }

    public void createPost(Post post) throws SQLException {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("comment_post_ID", post.getThreadID());
        if (post.getAuthor() != null) {
            data.put("comment_author", (post.getAuthor().getNickname() != null)
                    ? post.getAuthor().getNickname() :
                        post.getAuthor().getUsername());
            data.put("comment_author_email", post.getAuthor().getEmail());
            data.put("comment_author_IP", post.getAuthor().getLastIP());
            data.put("user_id", post.getAuthor().getID());
        }
        data.put("comment_date", new Date(new java.util.Date().getTime()));
        data.put("comment_date_gmt", null /*TODO*/);
        data.put("comment_content", post.getBody());
        this.dataManager.insertFields(data, "comments");
        post.setID(this.dataManager.getLastID("comment_ID", "comments"));
    }

    public int getThreadCount(String username) {
        return this.dataManager.getCount("posts", "`post_author` = '" +
                                            getUserID(username) + "'");
    }

    public int getTotalThreadCount() {
        return this.dataManager.getCount("posts");
    }

    public com.craftfire.authapi.classes.Thread getLastThread() {
        return getThread(this.dataManager.getLastID("post_id", "posts"));
    }

    public Thread getLastUserThread(String username) {
        return getThread(this.dataManager.getLastID("post_id", "posts",
                        "`post_author` = '" + getUserID(username) + "'"));
    }

    public Thread getThread(int threadid) {
        HashMap<String, Object> array = this.dataManager.getArray(
                 "SELECT * FROM `" + this.dataManager.getPrefix() +
                 "posts` WHERE `ID` = '" + threadid + "'");
        List<HashMap<String, Object>> array1 = this.dataManager.getArrayList(
                "SELECT `comment_ID` FROM `" + this.dataManager.getPrefix() +
                "comments` WHERE `comment_post_ID` = " + threadid +
                "' ORDER BY `post_id` ASC");
        int firstpost = Integer.parseInt(array1.get(0).get("comment_ID").toString());
        int lastpost = Integer.parseInt(array1.get(array1.size()).get("comment_ID")
                                                                        .toString());
        int boardid = array.get("post_type").toString()
                        .equalsIgnoreCase("post") ? 0 : 1;
        if (boardid > 0 && 
                !array.get("post_type").toString().equalsIgnoreCase("page"))
            return null;
        Thread thread = new Thread(this, firstpost, lastpost, threadid, boardid);
        thread.setAuthor(getUser(Integer.parseInt(array.get("post_author")
                                                           .toString())));
        thread.setBody(array.get("post_content").toString());
        thread.setSubject("post_title");
        thread.setThreadDate(new java.util.Date((
                (Date) array.get("post_date")).getTime()));
        thread.setReplies(Integer.parseInt(array.get("comment_count").toString()));
        thread.setSticky(false); //TODO, (table: options, option_name: sticky_posts) waiting for php unserialize
        thread.setLocked(array.get("comment_status").toString().equalsIgnoreCase("closed"));
        return thread;
    }

    public List<Thread> getThreads(int limit) {
        String limitstring = "";
        if (limit > 0)
            limitstring = " LIMIT 0," + limit;
        List<HashMap<String, Object>> array = this.dataManager.getArrayList(
                "SELECT `ID` FROM `" + this.dataManager.getPrefix() + "posts`"
                + limitstring);
        List<Thread> threads = new ArrayList<Thread>();
        for (HashMap<String,Object> record : array)
            threads.add(getThread(Integer.parseInt(
                    record.get("ID").toString())));
        return threads;
    }

    public void updateThread(Thread thread) throws SQLException {
        HashMap<String, Object> data = new HashMap<String, Object>();/*this.dataManager.getArray(
                "SELECT * FROM `" + this.dataManager.getPrefix() +
                "posts` WHERE `ID` = '" + thread.getID() + "'");*/
        data.put("post_author", thread.getAuthor().getID());
        data.put("post_date", new Date(thread.getThreadDate().getTime()));
        data.put("post_date_gmt", null /*TODO*/);
        data.put("post_content", thread.getBody());
        data.put("post_title", thread.getSubject());
        data.put("post_name", URLEncoder.encode(thread.getSubject().toLowerCase().replaceAll(" ", "-")));
        data.put("post_modified", new Date(new java.util.Date().getTime()));
        data.put("post_modified_gmt", null /*TODO*/);
        data.put("post_type", thread.getBoardID() == 0 ? "post" : "page");
        data.put("comment_count", thread.getReplies());
        data.put("comment_status", thread.isLocked() ? "closed" : "open");
        this.dataManager.updateFields(data, "posts", "`ID` = '" + thread.getID() + "'");
        //TODO update sticky, waiting for php (un)serialize
    }

    public void createThread(Thread thread) throws SQLException {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("post_author", thread.getAuthor().getID());
        data.put("post_date", new Date(new java.util.Date().getTime()));
        data.put("post_date_gmt", null /*TODO*/);
        data.put("post_content", thread.getBody());
        data.put("post_title", thread.getSubject());
        data.put("post_name", URLEncoder.encode(thread.getSubject().toLowerCase().replaceAll(" ", "-")));
        data.put("post_modified", new Date(new java.util.Date().getTime()));
        data.put("post_modified_gmt", null /*TODO*/);
        data.put("post_type", thread.getBoardID() == 0 ? "post" : "page");
        data.put("comment_count", thread.getReplies());
        data.put("post_type", thread.getBoardID() == 0 ? "post" : "page");
        this.dataManager.insertFields(data, "posts");
        thread.setID(this.dataManager.getLastID("ID", "posts"));
        //TODO update sticky, waiting for php (un)serialize
    }

    public int getUserCount() {
        return this.dataManager.getCount("users");
    }

    public int getGroupCount() {
        return 6;   // 6 WordPress roles: Subscriber, Contributor, Author, Editor, Administrator, Super Admin 
    }

    public String getHomeURL() {
        /*TODO*/
        return null;
    }

    public String getForumURL() {
        /*TODO*/
        return null;
    }

    public List<String> getIPs(String username) {
        // Not supported by WordPress
        return null;
    }

    public List<Ban> getBans(int limit) {
        // Not supported by WordPress
        return null;
    }

    public void updateBan(Ban ban) {
        // Not supported by WordPress
    }

    public void addBan(Ban ban) {
        // Not supported by WordPress
    }

    public int getBanCount() {
        // Not supported by WordPress
        return 0;
    }

    public boolean isBanned(String string) {
        // Not supported by WordPress
        return false;
    }

    public boolean isRegistered(String username) {
        return this.dataManager.exist("users", "user_login", username);
    }
}
