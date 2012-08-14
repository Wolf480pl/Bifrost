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
package com.craftfire.bifrost.classes;

import java.sql.SQLException;
import java.util.Date;

import com.craftfire.bifrost.exceptions.UnsupportedFunction;

public abstract interface PostInterface {
    public int getID();

    public void setID(int id);

    public int getThreadID();

    public Thread getThread() throws UnsupportedFunction;

    public int getBoardID();

    public Date getPostDate();

    public void setPostDate(Date postdate);

    public ScriptUser getAuthor();

    public void setAuthor(ScriptUser author);

    public String getSubject();

    public void setSubject(String subject);

    public String getBody();

    public void setBody(String body);

    public void updatePost() throws SQLException, UnsupportedFunction;

    public void createPost() throws SQLException, UnsupportedFunction;
}
