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
package com.craftfire.bifrost.classes.cms;

import java.sql.SQLException;
import java.util.List;

import com.craftfire.bifrost.classes.general.Cache;
import com.craftfire.bifrost.classes.general.Category;
import com.craftfire.bifrost.classes.general.Message;
import com.craftfire.bifrost.classes.general.MessageParent;
import com.craftfire.bifrost.classes.general.ScriptHandle;
import com.craftfire.bifrost.classes.general.ScriptUser;
import com.craftfire.bifrost.enums.CacheCleanupReason;
import com.craftfire.bifrost.enums.CacheGroup;
import com.craftfire.bifrost.exceptions.ScriptException;

/**
 * This class should only be used with a CMS comment.
 * <p>
 * The second constructor should only be used by the script itself and not by the library user.
 * To update any changed values in the comment, run {@link #update()}.
 * <p>
 * When creating a new CMSComment make sure you use the correct constructor:
 * {@link #CMSComment(CMSHandle, int)}.
 * <p>
 * Remember to run {@link #create()} after creating a comment to insert it into the script.
 */
public class CMSComment extends Message {
    private int articleid, parentid;

    /**
     * This constructor should be used when creating a new comment for the script.
     * <p>
     * Remember to run {@link #create()} after creating a comment to insert it into the script.
     * 
     * @param handle     the handle the comment is created for
     * @param articleid  the ID of the article the comment is on
     */
    public CMSComment(CMSHandle handle, int articleid) {
        super(handle);
        this.articleid = articleid;
    }

    /**
     * This constructor should only be used by the script and <b>not</b> by that library user.
     * 
     * @param script     the script the article comes from
     * @param id         the ID of the comment
     * @param articleid  the ID of the article the comment is on
     */
    public CMSComment(CMSScript script, int id, int articleid) {
        super(script.getHandle(), id);
        this.articleid = articleid;
    }

    @Override
    public CMSHandle getHandle() {
        return (CMSHandle) super.getHandle();
    }

    /**
     * Returns the list of comments replying to this comment.
     * <p>
     * Loads it form database if not cached.
     * 
     * @see MessageParent#getChildMessages(int)
     */
    @Override
    public List<CMSComment> getChildMessages(int limit) throws ScriptException {
        return getHandle().getCommentReplies(getID(), limit);
    }

    /**
     * Returns the ID of the comment this comment replies to.
     * 
     * @return the ID of the parent comment
     */
    public int getParentID() {
        return this.parentid;
    }

    /**
     * Sets the comment this comment replies to.
     * 
     * @param parentid  the ID of the parent comment
     */
    public void setParentID(int parentid) {
        this.parentid = parentid;
    }

    /**
     * Returns the CMSComment object for the comment this comment replies to.
     * 
     * @return the CMSComment object
     */
    @Override
    public MessageParent getParent() throws ScriptException {
        if (this.parentid != 0) {
            return getHandle().getComment(this.parentid);
        } else {
            return getArticle();
        }
    }

    /**
     * Returns the ID of the article this comment is on.
     * 
     * @return the ID of the article this comment is on
     */
    public int getArticleID() {
        return this.articleid;
    }

    /**
     * Sets the article this comment is on.
     * 
     * @param articleid  the ID of the article this comment is on
     */
    public void setArticleID(int articleid) {
        this.articleid = articleid;
    }

    /**
     * Returns the CMSArticle object of the article this comment is on.
     * 
     * @return                    the CMSArticle object
     * @throws ScriptException  if the method is not supported by the script
     */
    public CMSArticle getArticle() throws ScriptException {
        return getHandle().getArticle(this.articleid);
    }

    /*
     * (non-Javadoc)
     * 
     * @see Message#getCategoryID()
     */
    @Override
    public int getCategoryID() {
        try {
            return getArticle().getCategoryID();
        } catch (ScriptException e) {
            return 0;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see Message#getCategory()
     */
    @Override
    public Category getCategory() throws ScriptException, SQLException {
        return getArticle().getCategory();
    }

    /**
     * This method should be run after changing any comment values.
     * <p>
     * It should <b>not</b> be run when creating a new comment, only when editing an already existing comment.
     *
     * @throws SQLException       if a SQL error concurs
     * @throws ScriptException  if the method is not supported by the script
     */
    @Override
    public void update() throws SQLException, ScriptException {
        getHandle().updateComment(this);
    }

    /**
     * This method should be run after creating a new comment.
     * <p>
     * It should <b>not</b> be run when updating a comment, only when creating a new comment.
     *
     * @throws SQLException       if a SQL error concurs
     * @throws ScriptException  if the method is not supported by the script
     */
    @Override
    public void create() throws SQLException, ScriptException {
        getHandle().createComment(this);
    }

    /**
     * Returns {@code true} if the handle contains a comment cache with the given id parameter,
     * {@code false} if not.
     *
     * @param  handle  the script handle
     * @param  id      the id of the object to look for
     * @return         {@code true} if contains, {@code false} if not
     */
    public static boolean hasCache(ScriptHandle handle, int id) {
        return handle.getCache().contains(CacheGroup.COMMENT, id);
    }

    /**
     * Adds a CMSComment to the cache with the given script handle
     *
     * @param handle   the script handle
     * @param comment  the CMSComment object
     */
    public static void addCache(ScriptHandle handle, CMSComment comment) {
        handle.getCache().putMetadatable(CacheGroup.COMMENT, comment.getID(), comment);
        handle.getCache().setMetadata(CacheGroup.COMMENT, comment.getID(), "bifrost-cache.old-article", comment.getArticleID());
        handle.getCache().setMetadata(CacheGroup.COMMENT, comment.getID(), "bifrost-cache.old-parent", comment.getParentID());
        if (comment.getAuthor() != null) {
            handle.getCache().setMetadata(CacheGroup.COMMENT, comment.getID(), "bifrost-cache.old-author", comment.getAuthor().getUsername());
        } else {
            handle.getCache().removeMetadata(CacheGroup.COMMENT, comment.getID(), "bifrost-cache.old-author");
        }
    }

    /**
     * Returns the ForumPost object by the given id if found, returns {@code null} if no cache was found.
     *
     * @param  handle  the script handle
     * @param  id      the id of the post
     * @return         ForumPost object if cache was found, {@code null} if no cache was found
     */
    public static CMSComment getCache(ScriptHandle handle, int id) {
        if (handle.getCache().contains(CacheGroup.COMMENT, id)) {
            return (CMSComment) handle.getCache().get(CacheGroup.COMMENT, id);
        }
        return null;
    }

    /**
     * Removes outdated cache elements related to given {@code comment} from cache.
     * <p>
     * The method should be called when updating or creating a {@link CMSComment}, but before calling {@link #addCache}.
     * Only {@link ScriptHandle} and derived classes need to call this method.
     * 
     * @param handle   the handle the method is called from
     * @param comment  the comment to cleanup related cache
     * @param reason   the reason of cache cleanup, {@link CacheCleanupReason#OTHER} causes full cleanup
     * @see            Cache
     */
    public static void cleanupCache(ScriptHandle handle, CMSComment comment, CacheCleanupReason reason) {
        handle.getCache().remove(CacheGroup.ARTICLE_COMMENTS, comment.getArticleID());
        handle.getCache().remove(CacheGroup.COMMENT_COUNT, comment.getArticleID());
        handle.getCache().remove(CacheGroup.COMMENT_LAST_ARTICLE, comment.getArticleID());
        if (comment.getAuthor() != null) {
            String username = comment.getAuthor().getUsername();
            handle.getCache().remove(CacheGroup.COMMENT_LIST_USER, username);
            handle.getCache().remove(CacheGroup.COMMENT_COUNT_USER, username);
            handle.getCache().remove(CacheGroup.COMMENT_LAST_USER, username);
        }
        handle.getCache().remove(CacheGroup.COMMENT_REPLIES, comment.getParentID());
        handle.getCache().remove(CacheGroup.COMMENT_REPLY_COUNT, comment.getParentID());
        switch (reason) {
        case CREATE:
            handle.getCache().clear(CacheGroup.COMMENT_COUNT_TOTAL);
            handle.getCache().clear(CacheGroup.COMMENT_LIST);
            break;
        case OTHER:
            handle.getCache().clear(CacheGroup.COMMENT_COUNT_TOTAL);
            handle.getCache().clear(CacheGroup.COMMENT_LIST);
            /* Passes through */
        case UPDATE:
            Object oldArticle = handle.getCache().getMetadata(CacheGroup.COMMENT, comment.getID(), "bifrost-cache.old-article");
            Object oldUsername = handle.getCache().getMetadata(CacheGroup.COMMENT, comment.getID(), "bifrost-cache.old-author");
            Object oldParent = handle.getCache().getMetadata(CacheGroup.COMMENT, comment.getID(), "bifrost-cache.old-parent");
            handle.getCache().remove(CacheGroup.ARTICLE_COMMENTS, oldArticle);
            handle.getCache().remove(CacheGroup.COMMENT_COUNT, oldArticle);
            handle.getCache().remove(CacheGroup.COMMENT_LAST_ARTICLE, oldArticle);
            handle.getCache().remove(CacheGroup.COMMENT_LIST_USER, oldUsername);
            handle.getCache().remove(CacheGroup.COMMENT_COUNT_USER, oldUsername);
            handle.getCache().remove(CacheGroup.COMMENT_LAST_USER, oldUsername);
            handle.getCache().remove(CacheGroup.COMMENT_REPLIES, oldParent);
            handle.getCache().remove(CacheGroup.COMMENT_REPLY_COUNT, oldParent);
            break;
        }
    }

    /* (non-Javadoc)
     * @see Message#getAuthor()
     */
    @Override
    public CMSUser getAuthor() {
        return (CMSUser) super.getAuthor();
    }

    /* (non-Javadoc)
     * @see Message#setAuthor(ScriptUser)
     */
    @Override
    public void setAuthor(ScriptUser author) {
        if (author instanceof CMSUser) {
            super.setAuthor(author);
        }
    }
}
