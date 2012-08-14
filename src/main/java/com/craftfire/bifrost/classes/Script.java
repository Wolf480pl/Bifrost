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

import java.sql.SQLException;
import java.util.List;

import com.craftfire.bifrost.Bifrost;
import com.craftfire.bifrost.enums.Scripts;
import com.craftfire.bifrost.exceptions.UnsupportedFunction;
import com.craftfire.commons.CraftCommons;
import com.craftfire.commons.managers.DataManager;
import com.craftfire.commons.managers.LoggingManager;

public class Script implements ScriptInterface {
	private final String version;
	private final Scripts script;
    private final DataManager dataManager;
    private final Cache cache;

    protected Script(Scripts script, String version, DataManager dataManager) {
		this.version = version;
		this.script = script;
        this.dataManager = dataManager;
        this.cache = new Cache();
	}
    
    public Bifrost getBifrost() {
        return Bifrost.getInstance();
    }
    
    public LoggingManager getLoggingManager() {
        return Bifrost.getInstance().getLoggingManager();
    }

    public Cache getCache() {
        return this.cache;
    }
    
    public DataManager getDataManager() {
        return this.dataManager;
    }
    
    public Scripts getScript() {
        return this.script;
    }

	@Override
	public ScriptUser getUser(String username) throws UnsupportedFunction, SQLException {
		throw new UnsupportedFunction();
	}

	@Override
	public ScriptUser getUser(int userid) throws UnsupportedFunction, SQLException {
		throw new UnsupportedFunction();
	}

	@Override
	public ScriptUser getLastRegUser() throws UnsupportedFunction, SQLException {
		throw new UnsupportedFunction();
	}

	@Override
	public String getLatestVersion() {
		return null;
	}

	@Override
	public boolean isSupportedVersion() {
		for (int i=0; this.getVersionRanges().length > i; i++) {
			if (CraftCommons.inVersionRange(this.getVersionRanges()[i], this.version)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getVersion() {
		return this.version;
	}

	@Override
	public String[] getVersionRanges()  {
		return null;
	}

	@Override
	public String getEncryption() throws UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public String getScriptName() throws UnsupportedFunction {
        throw new UnsupportedFunction();
	}

    @Override
	public String getScriptShortname() throws UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public boolean authenticate(String username, String password) throws UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public String hashPassword(String salt, String password) throws UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public String getUsername(int userid) throws UnsupportedFunction, SQLException {
		throw new UnsupportedFunction();
	}

	@Override
	public int getUserID(String username) throws UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public void updateUser(ScriptUser user) throws SQLException, UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public void createUser(ScriptUser user) throws SQLException, UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public List<Group> getGroups(int limit) throws UnsupportedFunction, SQLException {
		throw new UnsupportedFunction();
	}

    @Override
    public int getGroupID(String group) throws UnsupportedFunction {
        throw new UnsupportedFunction();
    }

    @Override
    public Group getGroup(int groupid) throws UnsupportedFunction, SQLException {
		throw new UnsupportedFunction();
	}

	@Override
    public Group getGroup(String group) throws UnsupportedFunction,
            SQLException {
		throw new UnsupportedFunction();
	}

	@Override
    public List<Group> getUserGroups(String username)
            throws UnsupportedFunction, SQLException {
		throw new UnsupportedFunction();
	}

	@Override
	public void updateGroup(Group group) throws SQLException, UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public void createGroup(Group group) throws SQLException, UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public PrivateMessage getPM(int pmid) throws UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public List<PrivateMessage> getPMsSent(String username, int limit) throws UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public List<PrivateMessage> getPMsReceived(String username, int limit) throws UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public int getPMSentCount(String username) throws UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public int getPMReceivedCount(String username) throws UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public void updatePrivateMessage(PrivateMessage privateMessage) throws SQLException, UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public void createPrivateMessage(PrivateMessage privateMessage) throws SQLException, UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public int getPostCount(String username) throws UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public int getTotalPostCount() throws UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
    public Post getLastPost() throws UnsupportedFunction,
            NumberFormatException, SQLException {
		throw new UnsupportedFunction();
	}

	@Override
    public Post getLastUserPost(String username) throws UnsupportedFunction,
            NumberFormatException, SQLException {
		throw new UnsupportedFunction();
	}

	@Override
    public List<Post> getPosts(int limit) throws UnsupportedFunction,
            NumberFormatException, SQLException {
		throw new UnsupportedFunction();
	}

	@Override
    public List<Post> getPostsFromThread(int threadid, int limit)
            throws UnsupportedFunction, NumberFormatException, SQLException {
		throw new UnsupportedFunction();
	}

	@Override
    public Post getPost(int postid) throws UnsupportedFunction,
            NumberFormatException, SQLException {
		throw new UnsupportedFunction();
	}

	@Override
	public void updatePost(Post post) throws SQLException, UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public void createPost(Post post) throws SQLException, UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public int getThreadCount(String username) throws UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public int getTotalThreadCount() throws UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
    public Thread getLastThread() throws UnsupportedFunction,
            NumberFormatException, SQLException {
		throw new UnsupportedFunction();
	}

	@Override
    public Thread getLastUserThread(String username)
            throws UnsupportedFunction, NumberFormatException, SQLException {
		throw new UnsupportedFunction();
	}

	@Override
    public Thread getThread(int threadid) throws UnsupportedFunction,
            NumberFormatException, SQLException {
		throw new UnsupportedFunction();
	}

	@Override
    public List<Thread> getThreads(int limit) throws UnsupportedFunction,
            NumberFormatException, SQLException {
		throw new UnsupportedFunction();
	}

	@Override
	public void updateThread(Thread thread) throws SQLException, UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public void createThread(Thread thread) throws SQLException, UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public int getUserCount() throws UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public int getGroupCount() throws UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public String getHomeURL() throws UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public String getForumURL() throws UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public List<String> getIPs(String username) throws UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public List<Ban> getBans(int limit) throws UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public void updateBan(Ban ban) throws SQLException, UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public void addBan(Ban ban) throws SQLException, UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public int getBanCount() throws UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public boolean isBanned(String string) throws UnsupportedFunction {
		throw new UnsupportedFunction();
	}

	@Override
	public boolean isRegistered(String username) throws UnsupportedFunction {
		throw new UnsupportedFunction();
	}
}
