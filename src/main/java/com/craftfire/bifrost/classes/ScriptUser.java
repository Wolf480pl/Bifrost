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
package com.craftfire.bifrost.classes;

import java.awt.Image;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.craftfire.bifrost.Bifrost;
import com.craftfire.bifrost.ScriptHandle;
import com.craftfire.bifrost.enums.CacheGroup;
import com.craftfire.bifrost.exceptions.UnsupportedFunction;
import com.craftfire.commons.CraftCommons;

public class ScriptUser implements ScriptUserInterface {
    private int userid;
    private Date regdate, lastlogin, birthday;
    private Gender gender;
    private String username, title, nickname, realname, firstname, lastname, email, password, passwordsalt,
            statusmessage, avatarurl, profileurl, regip, lastip;
    private boolean activated;
    private List<Group> groups = new ArrayList<Group>();
    private final Script script;

    public ScriptUser(Script script, int userid, String username, String password) {
        this.script = script;
        this.username = username;
        this.userid = userid;
        this.password = password;
    }

    public ScriptUser(Script script, String username, String password) {
        this.script = script;
        this.username = username;
        this.password = password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public int getID() {
        return this.userid;
    }

    @Override
    public void setID(int id) {
        this.userid = id;
    }

    @Override
    public Date getRegDate() {
        return this.regdate;
    }

    @Override
    public void setRegDate(Date date) {
        this.regdate = date;
    }

    @Override
    public Date getLastLogin() {
        return this.lastlogin;
    }

    @Override
    public void setLastLogin(Date date) {
        this.lastlogin = date;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getUserTitle() {
        return this.title;
    }

    @Override
    public void setUserTitle(String title) {
        this.title = title;
    }

    @Override
    public String getNickname() {
        return this.nickname;
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String getRealName() {
        return this.realname;
    }

    @Override
    public void setRealName(String realname) {
        this.realname = realname;
    }

    @Override
    public String getFirstName() {
        return this.firstname;
    }

    @Override
    public void setFirstName(String firstname) {
        this.firstname = firstname;
    }

    @Override
    public String getLastName() {
        return this.lastname;
    }

    @Override
    public void setLastName(String lastname) {
        this.lastname = lastname;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Group> getGroups() throws UnsupportedFunction, SQLException {
        return Bifrost.getInstance().getScriptAPI().getHandle(this.script.getScript()).getUserGroups(this.username);
    }

    @Override
    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPasswordSalt() {
        return this.passwordsalt;
    }

    @Override
    public void setPasswordSalt(String passwordsalt) {
        this.passwordsalt = passwordsalt;
    }

    @Override
    public Date getBirthday() {
        return this.birthday;
    }

    @Override
    public void setBirthday(Date date) {
        this.birthday = date;
    }

    @Override
    public Gender getGender() {
        return this.gender;
    }

    @Override
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public String getStatusMessage() {
        return this.statusmessage;
    }

    @Override
    public void setStatusMessage(String message) {
        this.statusmessage = message;
    }

    @Override
    public Image getAvatar() {
        return CraftCommons.urlToImage(this.avatarurl);
    }

    @Override
    public String getAvatarURL() {
        return this.avatarurl;
    }

    @Override
    public void setAvatarURL(String url) {
        this.avatarurl = url;
    }

    @Override
    public String getProfileURL() {
        return this.profileurl;
    }

    @Override
    public void setProfileURL(String url) {
        this.profileurl = url;
    }

    @Override
    public String getRegIP() {
        return this.regip;
    }

    @Override
    public void setRegIP(String ip) {
        this.regip = ip;
    }

    @Override
    public String getLastIP() {
        return this.lastip;
    }

    @Override
    public void setLastIP(String ip) {
        this.lastip = ip;
    }

    @Override
    public boolean isActivated() {
        return this.activated;
    }

    @Override
    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    @Override
    public List<PrivateMessage> getPMsSent(int limit) throws UnsupportedFunction {
        return Bifrost.getInstance().getScriptAPI().getHandle(this.script.getScript()).getPMsSent(this.username, limit);
    }

    @Override
    public List<PrivateMessage> getPMsReceived(int limit) throws UnsupportedFunction {
        return Bifrost.getInstance().getScriptAPI().getHandle(this.script.getScript()).getPMsReceived(this.username, limit);
    }

    @Override
    public int getPMSentCount() throws UnsupportedFunction {
        return Bifrost.getInstance().getScriptAPI().getHandle(this.script.getScript()).getPMSentCount(this.username);
    }

    @Override
    public int getPMReceivedCount() throws UnsupportedFunction {
        return Bifrost.getInstance().getScriptAPI().getHandle(this.script.getScript()).getPMReceivedCount(this.username);
    }

    @Override
    public int getPostCount() throws UnsupportedFunction {
        return Bifrost.getInstance().getScriptAPI().getHandle(this.script.getScript()).getPostCount(this.username);
    }

    @Override
    public int getThreadCount() throws UnsupportedFunction {
        return Bifrost.getInstance().getScriptAPI().getHandle(this.script.getScript()).getThreadCount(this.username);
    }

    @Override
    public boolean isBanned() throws UnsupportedFunction {
        if (Bifrost.getInstance().getScriptAPI().getHandle(this.script.getScript()).isBanned(this.username)) {
            return true;
        } else if (Bifrost.getInstance().getScriptAPI().getHandle(this.script.getScript()).isBanned(this.email)) {
            return true;
        } else if (Bifrost.getInstance().getScriptAPI().getHandle(this.script.getScript()).isBanned(this.lastip)) {
            return true;
        } else if (Bifrost.getInstance().getScriptAPI().getHandle(this.script.getScript()).isBanned(this.regip)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isRegistered() throws UnsupportedFunction {
        return Bifrost.getInstance().getScriptAPI().getHandle(this.script.getScript()).isRegistered(this.username);
    }

    @Override
    public List<String> getIPs() throws UnsupportedFunction {
        return Bifrost.getInstance().getScriptAPI().getHandle(this.script.getScript()).getIPs(this.username);
    }

    @Override
    public Thread getLastThread() throws UnsupportedFunction,
            NumberFormatException, SQLException {
        return Bifrost.getInstance().getScriptAPI().getHandle(this.script.getScript()).getLastUserThread(this.username);
    }

    @Override
    public Post getLastPost() throws UnsupportedFunction,
            NumberFormatException, SQLException {
        return Bifrost.getInstance().getScriptAPI().getHandle(this.script.getScript()).getLastUserPost(this.username);
    }

    @Override
    public void updateUser() throws SQLException, UnsupportedFunction {
        Bifrost.getInstance().getScriptAPI().getHandle(this.script.getScript()).updateUser(this);
    }

    @Override
    public void createUser() throws SQLException, UnsupportedFunction {
        Bifrost.getInstance().getScriptAPI().getHandle(this.script.getScript()).createUser(this);
    }

    public static boolean hasCache(ScriptHandle handle, Object id) {
        return handle.getCache().contains(CacheGroup.USER, id);
    }

    public static void addCache(ScriptHandle handle, ScriptUser scriptUser) {
        handle.getCache().put(CacheGroup.USER, scriptUser.getID(), scriptUser);
    }

    @SuppressWarnings("unchecked")
    public static ScriptUser getCache(ScriptHandle handle, Object id) {
        ScriptUser temp = null;
        if (handle.getCache().contains(CacheGroup.USER, id)) {
            temp = (ScriptUser) handle.getCache().get(CacheGroup.USER, id);
        }
        return temp;
    }
}
