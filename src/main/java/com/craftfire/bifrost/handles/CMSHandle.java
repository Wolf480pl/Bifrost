package com.craftfire.bifrost.handles;

import java.util.List;

import com.craftfire.bifrost.classes.cms.CMSArticle;
import com.craftfire.bifrost.classes.cms.CMSCategory;
import com.craftfire.bifrost.classes.cms.CMSComment;
import com.craftfire.bifrost.classes.general.Ban;
import com.craftfire.bifrost.classes.general.Group;
import com.craftfire.bifrost.enums.CacheGroup;
import com.craftfire.bifrost.enums.Scripts;
import com.craftfire.bifrost.exceptions.UnsupportedMethod;
import com.craftfire.bifrost.exceptions.UnsupportedVersion;
import com.craftfire.bifrost.script.CMSScript;
import com.craftfire.commons.managers.DataManager;

public class CMSHandle extends ScriptHandle {

    public CMSHandle(Scripts script, String version, DataManager dataManager) throws UnsupportedVersion {
        super(script, version, dataManager);
    }

    public CMSScript getCMSScript() {
        return (CMSScript) this.script;
    }

    public Ban newBan(String name, String email, String ip) {
        return new Ban(this.script, name, email, ip);
    }

    public Group newGroup(String groupname) {
        return new Group(this.script, groupname);
    }

    public CMSComment newComment(int articleid) {
        return new CMSComment(this.script, articleid);
    }

    public CMSArticle newArticle(int catid) {
        return new CMSArticle(this.script, catid);
    }

    public CMSCategory newCategory(String name, int parentid) {
        return new CMSCategory(this.script, name, parentid);
    }

    public CMSComment getComment(int commentid) throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.COMMENT, commentid)) {
            return (CMSComment) getCache().get(CacheGroup.COMMENT, commentid);
        }
        CMSComment cmt = getCMSScript().getComment(commentid);
        getCache().put(CacheGroup.COMMENT, commentid, cmt);
        return cmt;
    }

    @SuppressWarnings("unchecked")
    public List<CMSComment> getComments(int limit) throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.COMMENT_LIST)) {
            return (List<CMSComment>) getCache().get(CacheGroup.COMMENT_LIST);
        }
        List<CMSComment> cmts = getCMSScript().getComments(limit);
        getCache().put(CacheGroup.COMMENT, cmts);
        return cmts;
    }

    public int getCommentTotalCount() throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.COMMENT_COUNT_TOTAL)) {
            return (Integer) getCache().get(CacheGroup.COMMENT_COUNT_TOTAL);
        }
        int cmts = getCMSScript().getCommentTotalCount();
        getCache().put(CacheGroup.COMMENT_COUNT_TOTAL, cmts);
        return cmts;
    }

    @SuppressWarnings("unchecked")
    public List<CMSComment> getCommentsOnArticle(int articleid, int limit) throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.ARTICLE_COMMENTS, articleid)) {
            return (List<CMSComment>) getCache().get(CacheGroup.ARTICLE_COMMENTS, articleid);
        }
        List<CMSComment> cmts = getCMSScript().getCommentsOnArticle(articleid, limit);
        getCache().put(CacheGroup.ARTICLE_COMMENTS, articleid, cmts);
        return cmts;
    }

    public int getCommentCount(int articleid) throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.COMMENT_COUNT, articleid)) {
            return (Integer) getCache().get(CacheGroup.COMMENT_COUNT, articleid);
        }
        int cmts = getCMSScript().getCommentCount(articleid);
        getCache().put(CacheGroup.COMMENT_COUNT, articleid, cmts);
        return cmts;
    }

    @SuppressWarnings("unchecked")
    public List<CMSComment> getCommentReplies(int commentid, int limit) throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.COMMENT_REPLIES, commentid)) {
            return (List<CMSComment>) getCache().get(CacheGroup.COMMENT_REPLIES, commentid);
        }
        List<CMSComment> cmts = getCMSScript().getCommentReplies(commentid, limit);
        getCache().put(CacheGroup.COMMENT_REPLIES, commentid, cmts);
        return cmts;
    }

    public int getCommentReplyCount(int commentid) throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.COMMENT_REPLY_COUNT, commentid)) {
            return (Integer) getCache().get(CacheGroup.COMMENT_REPLY_COUNT, commentid);
        }
        int cmts = getCMSScript().getCommentReplyCount(commentid);
        getCache().put(CacheGroup.COMMENT_REPLY_COUNT, commentid, cmts);
        return cmts;
    }

    @SuppressWarnings("unchecked")
    public List<CMSComment> getUserComments(String username, int limit) throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.COMMENT_LIST_USER, username)) {
            return (List<CMSComment>) getCache().get(CacheGroup.COMMENT_LIST_USER, username);
        }
        List<CMSComment> cmts = getCMSScript().getUserComments(username, limit);
        getCache().put(CacheGroup.COMMENT_LIST_USER, username, cmts);
        return cmts;
    }

    public int getUserCommentCount(String username) throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.COMMENT_USER_COUNT, username)) {
            return (Integer) getCache().get(CacheGroup.COMMENT_USER_COUNT, username);
        }
        int cmts = getCMSScript().getUserCommentCount(username);
        getCache().put(CacheGroup.COMMENT_USER_COUNT, username, cmts);
        return cmts;
    }

    public CMSComment getLastComment() throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.COMMENT_LAST)) {
            return (CMSComment) getCache().get(CacheGroup.COMMENT_LAST);
        }
        CMSComment cmt = getCMSScript().getLastComment();
        getCache().put(CacheGroup.COMMENT_LAST, cmt);
        return cmt;
    }

    public CMSComment getLastUserComment(String username) throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.COMMENT_LAST_USER, username)) {
            return (CMSComment) getCache().get(CacheGroup.COMMENT_LAST_USER, username);
        }
        CMSComment cmt = getCMSScript().getLastUserComment(username);
        getCache().put(CacheGroup.COMMENT_LAST_USER, username, cmt);
        return cmt;
    }

    public CMSComment getLastCommentOnArticle(int articleid) throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.COMMENT_LAST_ARTICLE, articleid)) {
            return (CMSComment) getCache().get(CacheGroup.COMMENT_LAST_ARTICLE, articleid);
        }
        CMSComment cmt = getCMSScript().getLastCommentOnArticle(articleid);
        getCache().put(CacheGroup.COMMENT_LAST_ARTICLE, articleid, cmt);
        return cmt;
    }

    public void updateComment(CMSComment comment) throws UnsupportedMethod {
        getCMSScript().updateComment(comment);
        CMSComment.addCache(this, comment);
    }

    public void createComment(CMSComment comment) throws UnsupportedMethod {
        getCMSScript().createComment(comment);
        CMSComment.addCache(this, comment);
    }

    public CMSArticle getArticle(int articleid) throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.ARTICLE, articleid)) {
            return (CMSArticle) getCache().get(CacheGroup.ARTICLE, articleid);
        }
        CMSArticle art = getCMSScript().getArticle(articleid);
        getCache().put(CacheGroup.ARTICLE, articleid, art);
        return art;
    }

    @SuppressWarnings("unchecked")
    public List<CMSArticle> getArticles(int limit) throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.ARTICLE_LIST)) {
            return (List<CMSArticle>) getCache().get(CacheGroup.ARTICLE_LIST);
        }
        List<CMSArticle> arts = getCMSScript().getArticles(limit);
        getCache().put(CacheGroup.ARTICLE_LIST, arts);
        return arts;
    }

    public int getArticleTotalCount() throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.ARTICLE_COUNT_TOTAL)) {
            return (Integer) getCache().get(CacheGroup.ARTICLE_COUNT_TOTAL);
        }
        int arts = getCMSScript().getArticleTotalCount();
        getCache().put(CacheGroup.ARTICLE_COUNT_TOTAL, arts);
        return arts;
    }

    @SuppressWarnings("unchecked")
    public List<CMSArticle> getArticlesFromCategory(int catid, int limit) throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.CMSCAT_ARTICLES, catid)) {
            return (List<CMSArticle>) getCache().get(CacheGroup.CMSCAT_ARTICLES, catid);
        }
        List<CMSArticle> arts = getCMSScript().getArticlesFromCategory(catid, limit);
        getCache().put(CacheGroup.CMSCAT_ARTICLES, catid, arts);
        return arts;
    }

    public int getArticleCount(int catid) throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.ARTICLE_COUNT, catid)) {
            return (Integer) getCache().get(CacheGroup.ARTICLE_COUNT, catid);
        }
        int arts = getCMSScript().getArticleCount(catid);
        getCache().put(CacheGroup.ARTICLE_COUNT, catid, arts);
        return arts;
    }

    @SuppressWarnings("unchecked")
    public List<CMSArticle> getUserArticles(String username, int limit) throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.ARTICLE_LIST_USER, username)) {
            return (List<CMSArticle>) getCache().get(CacheGroup.ARTICLE_LIST_USER, username);
        }
        List<CMSArticle> arts = getCMSScript().getUserArticles(username, limit);
        getCache().put(CacheGroup.ARTICLE_LIST_USER, username, arts);
        return arts;
    }

    public int getUserArticleCount(String username) throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.ARTICLE_COUNT_USER, username)) {
            return (Integer) getCache().get(CacheGroup.ARTICLE_COUNT_USER, username);
        }
        int arts = getCMSScript().getUserArticleCount(username);
        getCache().put(CacheGroup.ARTICLE_COUNT_USER, username, arts);
        return arts;
    }

    public CMSArticle getLastArticle() throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.ARTICLE_LAST)) {
            return (CMSArticle) getCache().get(CacheGroup.ARTICLE_LAST);
        }
        CMSArticle art = getCMSScript().getLastArticle();
        getCache().put(CacheGroup.ARTICLE_LAST, art);
        return art;
    }

    public CMSArticle getLastUserArticle(String username) throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.ARTICLE_LAST_USER, username)) {
            return (CMSArticle) getCache().get(CacheGroup.ARTICLE_LAST_USER, username);
        }
        CMSArticle art = getCMSScript().getLastUserArticle(username);
        getCache().put(CacheGroup.ARTICLE_LAST_USER, username, art);
        return art;
    }

    public CMSArticle getLastArticleFromCategory(int catid) throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.ARTICLE_LAST_CATEGORY, catid)) {
            return (CMSArticle) getCache().get(CacheGroup.ARTICLE_LAST_CATEGORY, catid);
        }
        CMSArticle art = getCMSScript().getLastArticleFromCategory(catid);
        getCache().put(CacheGroup.ARTICLE_LAST_CATEGORY, catid, art);
        return art;
    }

    public void updateArticle(CMSArticle article) throws UnsupportedMethod {
        getCMSScript().updateArticle(article);
        CMSArticle.addCache(this, article);
    }

    public void createArticle(CMSArticle article) throws UnsupportedMethod {
        getCMSScript().createArticle(article);
        CMSArticle.addCache(this, article);
    }

    public CMSCategory getCategory(int catid) throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.CMSCAT, catid)) {
            return (CMSCategory) getCache().get(CacheGroup.CMSCAT, catid);
        }
        CMSCategory cat = getCMSScript().getCategory(catid);
        getCache().put(CacheGroup.CMSCAT, catid, cat);
        return cat;
    }

    @SuppressWarnings("unchecked")
    public List<CMSCategory> getCategories(int limit) throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.CMSCAT_LIST)) {
            return (List<CMSCategory>) getCache().get(CacheGroup.CMSCAT_LIST);
        }
        List<CMSCategory> cats = getCMSScript().getCategories(limit);
        getCache().put(CacheGroup.CMSCAT_LIST, cats);
        return cats;
    }

    public int getCategoryCount() throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.CMSCAT_COUNT)) {
            return (Integer) getCache().get(CacheGroup.CMSCAT_COUNT);
        }
        int cats = getCMSScript().getCategoryCount();
        getCache().put(CacheGroup.CMSCAT_COUNT, cats);
        return cats;
    }

    @SuppressWarnings("unchecked")
    public List<CMSCategory> getSubCategories(int catid, int limit) throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.CMSCAT_SUBS, catid)) {
            return (List<CMSCategory>) getCache().get(CacheGroup.CMSCAT_SUBS, catid);
        }
        List<CMSCategory> cats = getCMSScript().getSubCategories(catid, limit);
        getCache().put(CacheGroup.CMSCAT_SUBS, catid, cats);
        return cats;
    }

    public int getSubCategoryCount(int catid) throws UnsupportedMethod {
        if (getCache().contains(CacheGroup.CMSCAT_SUB_COUNT, catid)) {
            return (Integer) getCache().get(CacheGroup.CMSCAT_SUB_COUNT, catid);
        }
        int cats = getCMSScript().getSubCategoryCount(catid);
        getCache().put(CacheGroup.CMSCAT_SUB_COUNT, catid, cats);
        return cats;
    }

    public void updateCategory(CMSCategory category) throws UnsupportedMethod {
        getCMSScript().updateCategory(category);
        CMSCategory.addCache(this, category);
    }

    public void createCategory(CMSCategory category) throws UnsupportedMethod {
        getCMSScript().createCategory(category);
        CMSCategory.addCache(this, category);
    }

}
