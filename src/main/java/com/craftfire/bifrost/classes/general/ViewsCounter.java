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
package com.craftfire.bifrost.classes.general;

/**
 * Everything that counts its views.
 */
public interface ViewsCounter {
    /**
     * Returns the view count of the object.
     * 
     * @return the view count
     */
    int getViewsCount();

    /**
     * Sets the view count of the object.
     * 
     * @param views  view count
     */
    void setViewsCount(int views);

}
