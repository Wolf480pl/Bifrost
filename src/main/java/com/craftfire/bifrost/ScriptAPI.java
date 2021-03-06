/*
 * This file is part of Bifrost.
 *
 * Copyright (c) 2011 CraftFire <http://www.craftfire.com/>
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
package com.craftfire.bifrost;

import java.util.HashMap;
import java.util.Map;

import com.craftfire.bifrost.exceptions.ScriptException;

import com.craftfire.commons.database.DataManager;
import com.craftfire.commons.util.LoggingManager;

import com.craftfire.bifrost.classes.cms.CMSHandle;
import com.craftfire.bifrost.classes.cms.CMSScript;
import com.craftfire.bifrost.classes.forum.ForumHandle;
import com.craftfire.bifrost.classes.forum.ForumScript;
import com.craftfire.bifrost.classes.general.Script;
import com.craftfire.bifrost.classes.general.ScriptHandle;
import com.craftfire.bifrost.enums.Scripts;
import com.craftfire.bifrost.scripts.cms.WordPress;
import com.craftfire.bifrost.scripts.forum.PhpBB;
import com.craftfire.bifrost.scripts.forum.SMF;
import com.craftfire.bifrost.scripts.forum.XenForo;

//TODO: Add example to the description
/**
 * ScriptAPI contains all the methods for creating and grabbing script handles.
 *
 * @see ScriptHandle
 */
public class ScriptAPI {
    private final Bifrost bifrost;
    private int handleID = 0;
    private Map<Integer, ScriptHandle> handles = new HashMap<Integer, ScriptHandle>();
    private ScriptHandle lastHandle = null;

    /**
     * The default constructor.
     *
     * @param bifrost  Bifrost instance
     */
    protected ScriptAPI(Bifrost bifrost) {
        this.bifrost = bifrost;
    }

    /**
     * Returns the Bifrost instance.
     *
     * @return Bifrost instance
     * @see    Bifrost
     */
    public Bifrost getBifrost() {
        return this.bifrost;
    }

    /**
     * Returns the LoggingManager of Bifrost.
     *
     * @return LoggingManager of Bifrost
     * @see    LoggingManager
     */
    public LoggingManager getLogger() {
        return getBifrost().getLogger();
    }

    /**
     * Returns the current handle ID.
     *
     * @return current handle ID
     */
    public int getHandleID() {
        return this.handleID;
    }

    /**
     * Creates a new handle ID.
     *
     * @return the new handle ID
     */
    protected int getNewHandleID() {
        return this.handleID++;
    }

    /**
     * Returns a {@link Script} object depending on which {@code script} and {@code version} has been used.
     * <p>
     * Returns {@code null} if the {@code script} is not supported.
     *
     * @param script            the script
     * @param version           the version of the script
     * @param dataManager       the {@link DataManager} for the script
     * @return                  a {@link Script} object, returns null if not supported
     * @throws ScriptException  if the {@code version} is not supported by the script
     */
    public static Script setScript(Scripts script, String version, DataManager dataManager) throws ScriptException {
        Script scriptInstance;
        switch (script) {
            case WP:
                scriptInstance = new WordPress(script, version, dataManager);
            break;
            case PHPBB:
                scriptInstance = new PhpBB(script, version, dataManager);
            break;
            case SMF:
                scriptInstance = new SMF(script, version, dataManager);
            break;
            case XF:
                scriptInstance = new XenForo(script, version, dataManager);
            break;
            default:
                return null;
        }
        if (!scriptInstance.isSupportedVersion()) {
            throw new ScriptException(ScriptException.Type.UNSUPPORTED_VERSION,
                                      "Version " + scriptInstance.getVersion() + " of " + scriptInstance.getScriptName() + " is not currently supported");
        }
        return scriptInstance;
    }

    /**
     * Returns a Map with all the {@link ScriptHandle}s that have been created.
     *
     * @return Map with all the script handles
     */
    public Map<Integer, ScriptHandle> getHandles() {
        getLogger().debug("ScriptAPI: Getting all handles, size: " + this.handles.size());
        return this.handles;
    }

    /**
     * Returns the {@link ScriptHandle} for the specified {@code script}.
     *
     * @param  handleID  the ID you want to grab the script handle from
     * @return           the script handle for the specified {@code script}
     */
    public ScriptHandle getHandle(int handleID) {
        if (handleExists(handleID)) {
            getLogger().debug("ScriptAPI: Found handle for ID '" + handleID + "!");
            return this.handles.get(handleID);
        } else {
            getLogger().debug("ScriptAPI: Handle for ID '" + handleID + "' does not exist, " +
                                            "returning null");
            return null;
        }
    }

    /**
     * Returns the {@link ForumHandle} for the specified {@code script}.
     *
     * @param  handleID  the ID you want to grab the forum handle from
     * @return           the forum handle for the specified {@code script}
     */
    public ForumHandle getForumHandle(int handleID) {
        return (ForumHandle) getHandle(handleID);
    }

    /**
     * Returns the {@link CMSHandle} for the specified {@code script}.
     *
     * @param  handleID  the ID you want to grab the cms handle from
     * @return           the cms handle for the specified {@code script}
     */
    public CMSHandle getCMSHandle(int handleID) {
        return (CMSHandle) getHandle(handleID);
    }

    /**
     * Returns the latest script handle, returns null if there are no handles.
     *
     * @return latest script handle, return null if there are no handles
     */
    public ScriptHandle getHandle() {
        getLogger().debug("ScriptAPI: Returning last handle: " + this.lastHandle);
        return this.lastHandle;
    }

    /**
     * Returns the latest forum handle, returns null if there are no handles.
     *
     * @return latest forum handle, return null if there are no handles
     */
    public ForumHandle getForumHandle() {
        getLogger().debug("ScriptAPI: Returning last forum handle: " + this.lastHandle);
        return (ForumHandle) this.lastHandle;
    }

    /**
     * Returns the latest cms handle, returns null if there are no handles.
     *
     * @return latest cms handle, return null if there are no handles
     */
    public CMSHandle getCMSHandle() {
        getLogger().debug("ScriptAPI: Returning last cms handle: " + this.lastHandle);
        return (CMSHandle) this.lastHandle;
    }

    /**
     * Creates an instance of given standard Bifrost-supported script. Creates a handle for it and adds the handle to the list.
     *
     * @param  script           the script
     * @param  version          the version of the script
     * @param  dataManager      the {@link DataManager} of the script
     * @throws ScriptException  if the specified {@code script} or {@code version} is not supported by Bifrost/the script
     */
    public int addHandle(Scripts script, String version, DataManager dataManager) throws ScriptException {
        if (script == null || version == null || dataManager == null) {
            throw new IllegalArgumentException("None of the arguments can be null!");
        }
        Script scriptInstance = ScriptAPI.setScript(script, version, dataManager);
        if (scriptInstance == null) {
            throw new ScriptException();
        }
        return addHandle(scriptInstance);
    }

    /**
     * Creates a handle for given script and adds it to the list.
     * 
     * @param script  the script to create handle for
     * @return        an id assigned to the handle
     */
    public int addHandle(Script script) {
        if (script == null) {
            throw new IllegalArgumentException("Parameter 'script' cannot be null.");
        }
        ScriptHandle handle;
        int id = this.getNewHandleID();
        switch (script.getType()) {
        case CMS:
            if (!(script instanceof CMSScript)) {
                throw new IllegalStateException("Script type is CMS but the script is not instance of CMSScript!");
            }
            handle = new CMSHandle(id, (CMSScript) script);
            break;
        case FORUM:
            if (!(script instanceof ForumScript)) {
                throw new IllegalStateException("Script type is FORUM but the script is not instance of ForumScript!");
            }
            handle = new ForumHandle(id, (ForumScript) script);
            break;
        default:
            handle = new ScriptHandle(id, script);
        }
        getLogger().debug(
                "ScriptAPI: Adding handle ID: '" + id + "' with type: '" + script.getType() + "' for script: '" + script.getScriptName() + "', version: '" + script.getVersion() + "'");
        this.handles.put(id, handle);
        this.lastHandle = handle;
        return id;
    }

    /**
     * Checks if a script handle already exists, returns true if it does, false if not
     *
     * @param  handleID  the handle ID you want to check
     * @return           true if exists, false if not
     */
    protected boolean handleExists(int handleID) {
        return this.handles.containsKey(handleID);
    }
}
