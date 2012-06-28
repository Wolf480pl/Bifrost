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

import java.util.List;

import com.craftfire.authapi.ScriptAPI;
import com.craftfire.authapi.classes.Ban;
import com.craftfire.authapi.classes.Group;
import com.craftfire.authapi.classes.Post;
import com.craftfire.authapi.classes.PrivateMessage;
import com.craftfire.authapi.classes.Script;
import com.craftfire.authapi.classes.ScriptUser;
import com.craftfire.authapi.classes.Thread;
import com.craftfire.commons.DataManager;

public class WordPress extends Script {
    private final String scriptName = "wordpress";
    private final String shortName = "wp";
    private final String encryption = "sha1"; /*TODO*/
    private final String[] versionRanges = {"1.0.4"}; /*TODO*/
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
        return this.dataManager.getStringField("users", "user_login", "`ID` = '" + userid + "'");;
    }

    public int getUserID(String username) {
        return this.dataManager.getIntegerField("users", "ID", "`user_login` = '" + username + "'");
    }

    public ScriptUser getLastRegUser() {
        return getUser(this.dataManager.getIntegerField("SELECT `ID` FROM `" + this.dataManager.getPrefix() +
                                                        "users` ORDER BY `ID` ASC LIMIT 1"));;
    }

    public ScriptUser getUser(String username) {
        return getUser(getUserID(username));
    }

    public ScriptUser getUser(int userid) {
        if (isRegistered(getUsername(userid))) {
            ScriptUser user = new ScriptUser(this, userid, null, null);
            HashMap<String, Object> array = this.dataManager.getArray(
                    "SELECT * FROM `" + this.dataManager.getPrefix() + "users` WHERE `ID` = '" +
                    userid + "' LIMIT 1");
            if (array.size() > 0) {
                String activation = this.dataManager.getStringField("usermeta", "meta_value", "`user_id` = '" + userid + "' AND `meta_key1 = 'uae_user_activation_code'")
                if (activation != null && activation.equalsIgnoreCase("active")) {
                    user.setActivated(true);
                } else {
                    user.setActivated(false);
                }
                user.setEmail(array.get("user_email").toString());
                user.setGender(Gender.UNKNOWN);
                user.setGroups(getUserGroups(array.get("user_login").toString()));
                if (array.get("user_registered") instanceof Date)
                    user.setRegDate((Date) array.get("user_registered"));
                user.setPassword(array.get("user_pass").toString())
                user.setUsername(array.get("user_login").toString());
                return user
            }
        }
        return null;
    }

    public void updateUser(ScriptUser user) {
        /*TODO*/
    }

    public void createUser(ScriptUser user) {
        /*TODO*/
    }

    public List<Group> getGroups(int limit) {
        /*TODO*/
        return null;
    }

    public Group getGroup(int groupid) {
        /*TODO*/
        return null;
    }

    public Group getGroup(String group) {
        /* TODO */
        return null;
    }

    public List<Group> getUserGroups(String username) {
        /*TODO*/
        return null;
    }

    public void updateGroup(Group group) {
        /*TODO*/
    }

    public void createGroup(Group group) {
        /*TODO*/
    }

    public PrivateMessage getPM(int pmid) {
        /*TODO*/
        return null;
    }

    public List<PrivateMessage> getPMsSent(String username, int limit) {
        /*TODO*/
        return null;
    }

    public List<PrivateMessage> getPMsReceived(String username, int limit) {
        /*TODO*/
        return null;
    }

    public int getPMSentCount(String username) {
        /*TODO*/
        return 0;
    }

    public int getPMReceivedCount(String username) {
        /*TODO*/
        return 0;
    }

    public void updatePrivateMessage(PrivateMessage privateMessage) {
        /*TODO*/
    }

    public void createPrivateMessage(PrivateMessage privateMessage) {
        /*TODO*/
    }

    public int getPostCount(String username) {
        /*TODO*/
        return 0;
    }

    public int getTotalPostCount() {
        /*TODO*/
        return 0;
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
        /*TODO*/
        return null;
    }

    public List<Post> getPostsFromThread(int threadid, int limit) {
        /*TODO*/
        return null;
    }

    public Post getPost(int postid) {
        /*TODO*/
        return null;
    }

    public void updatePost(Post post) {
        /*TODO*/
    }

    public void createPost(Post post) {
        /*TODO*/
    }

    public int getThreadCount(String username) {
        /*TODO*/
        return 0;
    }

    public int getTotalThreadCount() {
        /*TODO*/
        return 0;
    }

    public com.craftfire.authapi.classes.Thread getLastThread() {
        /*TODO*/
        return null;
    }

    public Thread getLastUserThread(String username) {
        /*TODO*/
        return null;
    }

    public Thread getThread(int threadid) {
        /*TODO*/
        return null;
    }

    public List<Thread> getThreads(int limit) {
        /*TODO*/
        return null;
    }

    public void updateThread(Thread thread) {
        /*TODO*/
    }

    public void createThread(Thread thread) {
        /*TODO*/
    }

    public int getUserCount() {
        /*TODO*/
        return 0;
    }

    public int getGroupCount() {
        /*TODO*/
        return 0;
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
        /*TODO*/
        return null;
    }

    public List<Ban> getBans(int limit) {
        /*TODO*/
        return null;
    }

    public void updateBan(Ban ban) {
        /*TODO*/
    }

    public void addBan(Ban ban) {
        /*TODO*/
    }

    public int getBanCount() {
        /*TODO*/
        return 0;
    }

    public boolean isBanned(String string) {
        /*TODO*/
        return false;
    }

    public boolean isRegistered(String username) {
        /*TODO*/
        return false;
    }
}
