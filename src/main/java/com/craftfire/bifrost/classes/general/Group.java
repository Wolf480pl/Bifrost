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
package com.craftfire.bifrost.classes.general;

import java.sql.SQLException;
import java.util.List;

import com.craftfire.bifrost.Bifrost;
import com.craftfire.bifrost.enums.CacheGroup;
import com.craftfire.bifrost.exceptions.UnsupportedMethod;
import com.craftfire.bifrost.handles.ScriptHandle;
import com.craftfire.bifrost.script.Script;

/**
 * This class should only be used with a group.
 * <p>
 * The first constructor should only be used by the script itself and not by the library user.
 * To update any changed values in the post, run {@see #updateGroup()}.
 * <p>
 * When creating a new Group make sure you use the correct constructor:
 * {@see #Group(com.craftfire.bifrost.script.Script, String)}.
 * <p>
 * Remember to run {@see #createGroup()} after creating a group to insert it into the script.
 */
public class Group implements IDable {
    private int groupid, usercount;
    private String groupname, groupdescription;
    private List<ScriptUser> users;
    private final Script script;

    /**
     * This constructor should only be used by the script and not by that library user.
     *
     * @param script     the script
     * @param groupid    the ID of the group
     * @param groupname  the name of the group
     */
    public Group(Script script, int groupid, String groupname) {
        this.script = script;
        this.groupid = groupid;
        this.groupname = groupname;
    }

    /**
     * This constructor should be used when creating a new group for the script.
     * <p>
     * Remember to run {@see #createGroup()} after creating a ban to insert it into the script.
     *
     * @param script     the script
     * @param groupname  the ID of the group
     */
    public Group(Script script, String groupname) {
        this.script = script;
        this.groupname = groupname;
    }

    @Override
    public int getID() {
        return this.groupid;
    }

    public void setID(int id) {
        this.groupid = id;
    }

    /**
     * Returns the name.
     * <p>
     * Returns <code>null</code> if no name was found.
     *
     * @return name of the group, <code>null</code> if no name was found
     */
    public String getName() {
        return this.groupname;
    }

    /**
     * Sets the name.
     *
     * @param name  the name of the group
     */
    public void setName(String name) {
        this.groupname = name;
    }

    /**
     * Returns the description.
     * <p>
     * Returns <code>null</code> if no description was found.
     *
     * @return description of the group, <code>null</code> if no description was found
     */
    public String getDescription() {
        return this.groupdescription;
    }

    /**
     * Sets the description.
     *
     * @param description  the description of the group
     */
    public void setDescription(String description) {
        this.groupdescription = description;
    }

    /**
     * Returns the list of users in the group.
     * <p>
     * The list is empty if there are no users in the group.
     *
     * @return the list of users in the group
     */
    public List<ScriptUser> getUsers() {
        return this.users;
    }

    /**
     * Sets the list of users in the group.
     *
     * @param users  the list of users that should be in the group
     */
    public void setUsers(List<ScriptUser> users) {
        this.users = users;
    }

    /**
     * Returns amount of users in the group.
     *
     * @return the amount of users in the group
     */
    public int getUserCount() {
        return this.usercount;
    }

    /**
     * Sets the amount of users in the group.
     *
     * @param usercount  the amount of users in the group
     */
    public void setUserCount(int usercount) {
        this.usercount = usercount;
    }

    /**
     * This method should be run after changing any group values.
     * <p>
     * It should <b>not</b> be run when creating a new group, only when editing an already existing group.
     *
     * @throws SQLException       if a SQL error concurs
     * @throws UnsupportedMethod  if the method is not supported by the script
     */
    public void updateGroup() throws SQLException, UnsupportedMethod {
        Bifrost.getInstance().getScriptAPI().getHandle(this.script.getScript()).updateGroup(this);
    }

    /**
     * This method should be run after creating a new group.
     * <p>
     * It should <b>not</b> be run when updating a group, only when creating a new group.
     *
     * @throws SQLException       if a SQL error concurs
     * @throws UnsupportedMethod  if the method is not supported by the script
     */
    public void createGroup() throws SQLException, UnsupportedMethod {
        Bifrost.getInstance().getScriptAPI().getHandle(this.script.getScript()).createGroup(this);
    }

    /**
     * Returns <code>true</code> if the handle contains a group cache with the given id parameter,
     * <code>false</code> if not.
     *
     * @param handle  the script handle
     * @param id      the id of the object to look for
     * @return        <code>true</code> if contains, <code>false</code> if not
     */
    public static boolean hasCache(ScriptHandle handle, Object id) {
        return handle.getCache().contains(CacheGroup.GROUP, id);
    }

    /**
     * Adds a Group to the cache with the given script handle
     *
     * @param handle  the script handle
     * @param group   the Group object
     */
    public static void addCache(ScriptHandle handle, Group group) {
        handle.getCache().put(CacheGroup.GROUP, group.getID(), group);
    }

    /**
     * Returns the Group object by the given id if found, returns <code>null</code> if no cache was found.
     *
     * @param handle  the script handle
     * @param id      the id of the post
     * @return        Group object if cache was found, <code>null</code> if no cache was found
     */
    public static Group getCache(ScriptHandle handle, Object id) {
        if (handle.getCache().contains(CacheGroup.GROUP, id)) {
            return (Group) handle.getCache().get(CacheGroup.GROUP, id);
        }
        return null;
    }
}
