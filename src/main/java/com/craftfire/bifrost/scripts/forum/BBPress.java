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

import java.util.List;

import com.craftfire.bifrost.classes.Ban;
import com.craftfire.bifrost.classes.Group;
import com.craftfire.bifrost.classes.Post;
import com.craftfire.bifrost.classes.PrivateMessage;
import com.craftfire.bifrost.classes.Script;
import com.craftfire.bifrost.classes.ScriptUser;
import com.craftfire.bifrost.classes.Thread;
import com.craftfire.bifrost.enums.Scripts;
import com.craftfire.commons.managers.DataManager;

public class BBPress extends Script {
    private final String scriptName = "bbpress";
    private final String shortName = "bbp";
    private final String encryption = "sha1"; /*TODO*/
    private final String[] versionRanges = {"1.0.4"}; /*TODO*/
    private String currentUsername = null;

    public BBPress(Scripts script, String version, DataManager dataManager) {
        super(script, version, dataManager);
    }

    @Override
    public String[] getVersionRanges() {
        return this.versionRanges;
    }

    @Override
    public String getLatestVersion() {
        /*TODO*/
        return this.versionRanges[0];
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
        return false;
    }

    @Override
    public String hashPassword(String salt, String password) {
        /*TODO*/
        return null;
    }

    @Override
    public String getUsername(int userid) {
        /*TODO*/
        return null;
    }

    @Override
    public int getUserID(String username) {
        /*TODO*/
        return 0;
    }

    @Override
    public ScriptUser getLastRegUser() {
        /*TODO*/
        return null;
    }

    @Override
    public ScriptUser getUser(String username) {
        /*TODO*/
        return null;
    }

    @Override
    public ScriptUser getUser(int userid) {
        /*TODO*/
        return null;
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
    public List<Group> getGroups(int limit) {
        /*TODO*/
        return null;
    }

    @Override
    public int getGroupID(String group) {
        /*TODO*/
        return 0;
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
