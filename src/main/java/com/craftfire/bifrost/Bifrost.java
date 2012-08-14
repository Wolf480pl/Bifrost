/*
 * This file is part of Bifrost <http://www.craftfire.com/>.
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

import com.craftfire.commons.managers.LoggingManager;

//TODO: Javadoc, analytics and logging.
public class Bifrost {
    private static Bifrost instance;
    private final String version = "1.0.0";
    private final ScriptAPI scriptAPI;
	private final LoggingManager loggingManager = new LoggingManager("CraftFire.Bifrost", "[Bifrost]");

    public Bifrost() {
        this.scriptAPI = new ScriptAPI();
        instance = this;
        this.loggingManager.debug("Initialized Bifrost version " + this.version);
    }

    public static Bifrost getInstance() {
        return instance;
    }

    public String getVersion() {
        return this.version;
    }

    public ScriptAPI getScriptAPI() {
        return this.scriptAPI;
    }

	public LoggingManager getLoggingManager() {
		return this.loggingManager;
	}
}
