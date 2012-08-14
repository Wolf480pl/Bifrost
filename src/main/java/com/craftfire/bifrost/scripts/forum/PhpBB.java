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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.craftfire.bifrost.scripts.forum;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.craftfire.bifrost.classes.Ban;
import com.craftfire.bifrost.classes.Group;
import com.craftfire.bifrost.classes.Post;
import com.craftfire.bifrost.classes.PrivateMessage;
import com.craftfire.bifrost.classes.Script;
import com.craftfire.bifrost.classes.ScriptUser;
import com.craftfire.bifrost.classes.Thread;
import com.craftfire.bifrost.enums.Scripts;
import com.craftfire.commons.CraftCommons;
import com.craftfire.commons.database.DataList;
import com.craftfire.commons.database.Results;
import com.craftfire.commons.enums.Encryption;
import com.craftfire.commons.managers.DataManager;

public class PhpBB extends Script {
    private final String scriptName = "phpbb";
    private final String shortName = "phpbb";
    private final String encryption = "sha1"; /*TODO*/
    private final String[] versionRanges = {"20.0.23", "3.0.10"};
    private String membernamefield = "username", groupfield = "additional_groups";
    private String currentUsername = null;

    public PhpBB(Scripts script, String version, DataManager dataManager) {
        super(script, version, dataManager);
    }

    @Override
    public String[] getVersionRanges() {
        return this.versionRanges;
    }

    @Override
    public String getLatestVersion() {
        return this.versionRanges[1];
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
        /*TODO*/
        String passwordHash = this.getDataManager().getStringField("users",
                                                "user_password", "`" + this.membernamefield + "` = '" + username + "'");
        return hashPassword(username, password).equals(passwordHash);
    }

    @Override
    public String hashPassword(String username, String password) {
        /*TODO*/
        return CraftCommons.encrypt(Encryption.PHPASS, username.toLowerCase() + password, null, 0);
    }

    @Override
    public String getUsername(int userid) {
        return this.getDataManager().getStringField("users", "username", "`user_id` = '" + userid + "'");
    }

    @Override
    public int getUserID(String username) {
        /*TODO*/
        return this.getDataManager().getIntegerField("users", "user_id", "`username` = '" + username + "'");
    }

    @Override
    public ScriptUser getLastRegUser() throws SQLException {
        /*TODO*/
        return getUser(this.getDataManager().getIntegerField("SELECT `user_id` FROM `" + 
                this.getDataManager().getPrefix() + "users` ORDER BY `user_id` ASC LIMIT 1"));
    }

    @Override
    public ScriptUser getUser(String username) throws SQLException {
        return getUser(getUserID(username));
    }

    @Override
    public ScriptUser getUser(int userid) throws SQLException {
        /*TODO*/
        Results results = this.getDataManager().getResults("SELECT * FROM `" + this.getDataManager().getPrefix() +
                                                            "` WHERE `user_id` = " + userid);
        DataList result = results.getFirstResult();
        ScriptUser user = new ScriptUser(this, result.getIntField("user_id"), 
                                        result.getStringField("username"), 
                                        result.getStringField("user_password"));
       // user.setActivated();
        //user.setAvatarURL();
        //user.setBirthday();
        user.setEmail(result.getStringField("user_email"));
        //user.setGender();
        //user.setLastIP();
        //user.setLastLogin();

        user.setRegDate(new Date(result.getIntField("user_regdate")));
        user.setRegIP(result.getStringField("user_regdate"));
        return user;
    }

    @Override
    public void updateUser(ScriptUser user) {
        /*TODO*/
    }

    @Override
    public void createUser(ScriptUser user) {
        /*TODO*/
    }

    @Override
    public List<Group> getGroups(int limit) throws SQLException {
        /*TODO*/
        String limitstring = "";
        if (limit > 0) {
            limitstring = " LIMIT 0 , " + limit;
        }
        List<Group> groups = new ArrayList<Group>();
        Results results = this.getDataManager().getResults("SELECT `group_id` FROM `" +
                                this.getDataManager().getPrefix() + "groups` ORDER BY `group_id` ASC" + limitstring);
        for (DataList dataList : results.getArray()) {
            groups.add(getGroup(dataList.getIntField("group_id")));
        }
        return groups;
    }

    @Override
    public int getGroupID(String group) {
        /*TODO*/
        return this.getDataManager().getIntegerField("groups", "group_id", "`group_name` = '" + group + "'");
    }

    @Override
    public Group getGroup(int groupid) {
        /*TODO*/
        return null;
    }

    @Override
    public Group getGroup(String group) {
        /* TODO */
        return null;
    }

    @Override
    public List<Group> getUserGroups(String username) {
        /*TODO*/
        return null;
    }

    @Override
    public void updateGroup(Group group) {
        /*TODO*/
    }

    @Override
    public void createGroup(Group group) {
        /*TODO*/
    }

    @Override
    public PrivateMessage getPM(int pmid) {
        /*TODO*/
        return null;
    }

    @Override
    public List<PrivateMessage> getPMsSent(String username, int limit) {
        /*TODO*/
        return null;
    }

    @Override
    public List<PrivateMessage> getPMsReceived(String username, int limit) {
        /*TODO*/
        return null;
    }

    @Override
    public int getPMSentCount(String username) {
        /*TODO*/
        return 0;
    }

    @Override
    public int getPMReceivedCount(String username) {
        /*TODO*/
        return 0;
    }

    @Override
    public void updatePrivateMessage(PrivateMessage privateMessage) {
        /*TODO*/
    }

    @Override
    public void createPrivateMessage(PrivateMessage privateMessage) {
        /*TODO*/
    }

    @Override
    public int getPostCount(String username) {
        /*TODO*/
        return 0;
    }

    @Override
    public int getTotalPostCount() {
        /*TODO*/
        return 0;
    }

    @Override
    public Post getLastPost() {
        /*TODO*/
        return null;
    }

    @Override
    public Post getLastUserPost(String username) {
        /*TODO*/
        return null;
    }

    @Override
    public List<Post> getPosts(int limit) {
        /*TODO*/
        return null;
    }

    @Override
    public List<Post> getPostsFromThread(int threadid, int limit) {
        /*TODO*/
        return null;
    }

    @Override
    public Post getPost(int postid) {
        /*TODO*/
        return null;
    }

    @Override
    public void updatePost(Post post) {
        /*TODO*/
    }

    @Override
    public void createPost(Post post) {
        /*TODO*/
    }

    @Override
    public int getThreadCount(String username) {
        /*TODO*/
        return 0;
    }

    @Override
    public int getTotalThreadCount() {
        /*TODO*/
        return 0;
    }

    @Override
    public com.craftfire.bifrost.classes.Thread getLastThread() {
        /*TODO*/
        return null;
    }

    @Override
    public Thread getLastUserThread(String username) {
        /*TODO*/
        return null;
    }

    @Override
    public Thread getThread(int threadid) {
        /*TODO*/
        return null;
    }

    @Override
    public List<Thread> getThreads(int limit) {
        /*TODO*/
        return null;
    }

    @Override
    public void updateThread(Thread thread) {
        /*TODO*/
    }

    @Override
    public void createThread(Thread thread) {
        /*TODO*/
    }

    @Override
    public int getUserCount() {
        /*TODO*/
        return 0;
    }

    @Override
    public int getGroupCount() {
        /*TODO*/
        return 0;
    }

    @Override
    public String getHomeURL() {
        /*TODO*/
        return null;
    }

    @Override
    public String getForumURL() {
        /*TODO*/
        return null;
    }

    @Override
    public List<String> getIPs(String username) {
        /*TODO*/
        return null;
    }

    @Override
    public List<Ban> getBans(int limit) {
        /*TODO*/
        return null;
    }

    @Override
    public void updateBan(Ban ban) {
        /*TODO*/
    }

    @Override
    public void addBan(Ban ban) {
        /*TODO*/
    }

    @Override
    public int getBanCount() {
        /*TODO*/
        return 0;
    }

    @Override
    public boolean isBanned(String string) {
        /*TODO*/
        return false;
    }

    @Override
    public boolean isRegistered(String username) {
        /*TODO*/
        return false;
    }
}
