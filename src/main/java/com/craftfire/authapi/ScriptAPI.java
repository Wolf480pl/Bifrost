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
package com.craftfire.authapi;

import com.craftfire.authapi.classes.Script;
import com.craftfire.authapi.exceptions.UnsupportedScript;
import com.craftfire.authapi.exceptions.UnsupportedVersion;
import com.craftfire.authapi.scripts.cms.WordPress;
import com.craftfire.authapi.scripts.forum.SMF;
import com.craftfire.authapi.scripts.forum.XenForo;

public class ScriptAPI {
    private AuthAPI authAPI;
    private Script script;
    private final Scripts scriptName;
    private final String version;

    public enum Scripts {
        SMF("simplemachines"),
        XF("xenforo"),
        WP("wordpress");
        
        public String alias;
        Scripts(String alias) {
            this.alias = alias;
        }
    }

    /**
     * @param script  The script using the enum list, for example: Scripts.SMF.
     * @param version The version that the user has set in his config.
     * @throws UnsupportedVersion if the input version is not found in the list of supported versions.
     */
    public ScriptAPI(AuthAPI authAPI, Scripts script, String version) throws UnsupportedVersion {
        this.authAPI = authAPI;
        this.scriptName = script;
        this.version = version;
        setScript();
        if (!this.script.isSupportedVersion()) {
            throw new UnsupportedVersion();
        }
    }

    /**
     * @param script  The script in a string, for example: smf.
     * @param version The version that the user has set in his config.
     * @throws UnsupportedScript if the input string is not found in the list of supported scripts.
     * @throws UnsupportedVersion if the input version is not found in the list of supported versions.
     */
    public ScriptAPI(AuthAPI authAPI, String script, String version) throws UnsupportedScript, UnsupportedVersion {
        this.authAPI = authAPI;
        this.scriptName = stringToScript(script);
        this.version = version;
        setScript();
        if (!this.script.isSupportedVersion()) {
            throw new UnsupportedVersion();
        }
    }

    /**
     * @return The Script object.
     */
    public Script getScript() {
        return this.script;
    }

    /**
     * Sets the script which is being used.
     */
    private void setScript() {
        switch (this.scriptName) {
            case SMF:
                this.script = new SMF(this.authAPI, this.scriptName, this.version);
                break;
            case XF:
                this.script = new XenForo(this.authAPI, this.scriptName, this.version);
                break;
            case WP:
                this.script = new WordPress(this.authAPI, this.scriptName, this.version);
                break;
        }
    }

    /**
     * Converts a string into a script enum.
     *
     * @param string The string which contains the script name
     * @return The script for the string, if none are found it returns null.
     * @throws UnsupportedScript if the input string is not found in the list of supported scripts.
     */
    private Scripts stringToScript(String string) throws UnsupportedScript {
        for (Scripts script : Scripts.values()) {
            if (string.equalsIgnoreCase(script.toString()) || string.equalsIgnoreCase(script.alias)) {
                return script;
            } else if (script.alias.contains(",")) {
                String[] aliases = script.alias.split(",");
                for (int i=0; aliases.length>i; i++) {
                    if (string.equalsIgnoreCase(aliases[i])) {
                        return script;
                    }
                }
            }
        }
        throw new UnsupportedScript();
    }
}
