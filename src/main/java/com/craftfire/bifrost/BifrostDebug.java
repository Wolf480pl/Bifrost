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
package com.craftfire.bifrost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.craftfire.bifrost.classes.Ban;
import com.craftfire.bifrost.classes.Group;
import com.craftfire.bifrost.classes.Post;
import com.craftfire.bifrost.classes.PrivateMessage;
import com.craftfire.bifrost.classes.ScriptUser;
import com.craftfire.bifrost.classes.Thread;
import com.craftfire.bifrost.enums.Scripts;
import com.craftfire.bifrost.exceptions.UnsupportedFunction;
import com.craftfire.bifrost.exceptions.UnsupportedScript;
import com.craftfire.bifrost.exceptions.UnsupportedVersion;
import com.craftfire.commons.enums.DataType;
import com.craftfire.commons.managers.DataManager;

public class BifrostDebug {
    static Bifrost bifrost;
    static Scripts script;
    static String version;
    static DataManager dataManager;
    static HashMap<String, String> data = new HashMap<String, String>();
    static String newline = System.getProperty("line.separator");
    static String seperate = newline + "|------------------------------------------------------------------|" +
                             newline;

    public static void main(String[] args) {
        int count = 1;
        HashMap<Integer, Scripts> scriptsh = new HashMap<Integer, Scripts>();
        for (Scripts s : Scripts.values()) {
            System.out.println("#" + count + " - " + s.toString() + newline);
            scriptsh.put(count, s);
            count++;
        }
        System.out.println(newline + "Please select a number for which script you wish to use." + newline);
        InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader buf_reader = new BufferedReader(reader);
        int tmp;
        try {
            String s = buf_reader.readLine();
            tmp = Integer.parseInt(s.trim());
            Scripts ss = scriptsh.get(tmp);
            script = ss;
            System.out.println(newline + "Selected " + ss.toString() + " as script." + newline);
            System.out.println(seperate);
            System.out.println("Please continue by typing the script version number (e.g. 1.0.1)" + newline);
            String line = null;
            boolean valid = false;
            do {
                if (line != null) {
                    System.out.println(newline + "That was not a valid version number, please try again." + newline);
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                line = br.readLine();
                if (line.contains(".")) {
                    String[] split = line.split("\\.");
                    if (split.length > 1) {
                        version = line;
                        valid = true;
                    }
                } else {
                    version = "1.0.0";
                    System.out.println(version);
                    valid = true;
                }
            } while (! valid);
            System.out.println(newline + script.toString() + " version set to " + version + "." + newline);
            System.out.println(seperate);
            ask("MySQL keepalive", "mysql_keepalive", "true");
            ask("MySQL timeout", "mysql_timeout", "0");
            ask("MySQL host", "mysql_host", "localhost");
            ask("MySQL port", "mysql_port", "3306");
            ask("MySQL database", "mysql_database", "wordpress"); //TODO: Default should be "craftfire"
            ask("MySQL username", "mysql_username", "root"); //TODO: Default should be "craftfire"
            ask("MySQL password", "mysql_password", "AuthAPI"); //TODO: Default should be "craftfire"
            ask("MySQL prefix", "mysql_prefix", "wp_"); //TODO: Default should be "smf__202__"
            ask("Script user username", "script_username", "ttt"); //TODO: Default should be "Contex"
            boolean keepalive = false;
            if (data.get("mysql_keepalive").equalsIgnoreCase("true") ||
                data.get("mysql_keepalive").equalsIgnoreCase("1")) {
                keepalive = true;
            }
            int port = 3306;
            int tempport = Integer.parseInt(data.get("mysql_port"));
            if (tempport > 0) {
                port = tempport;
            }
            int timeout = 0;
            int temptimeout = Integer.parseInt(data.get("mysql_timeout"));
            if (temptimeout > 0) {
                timeout = temptimeout;
            }
            dataManager = new DataManager(DataType.MYSQL, data.get("mysql_username"), data.get("mysql_password"));
            dataManager.setHost(data.get("mysql_host"));
            dataManager.setPort(port);
            dataManager.setDatabase(data.get("mysql_database"));
            dataManager.setPrefix(data.get("mysql_prefix"));
            dataManager.setTimeout(timeout);
            dataManager.setKeepAlive(keepalive);
            try {
                bifrost = new Bifrost();
                bifrost.getScriptAPI().addHandle(script, version, dataManager);
            } catch (UnsupportedVersion unsupportedVersion) {
                unsupportedVersion.printStackTrace();
            } catch (UnsupportedScript unsupportedScript) {
                unsupportedScript.printStackTrace();
            }
            runTests();
        } catch (IOException ioe) {
            System.out.println("IO exception = " + ioe);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void ask(String name, String key, String defaultvalue) {
        String line = null;
        boolean valid = false;
        try {
            do {
                System.out.println(newline + name + ": ");
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                line = br.readLine();
                if (line != null && ! line.isEmpty()) {
                    data.put(key, line);
                    valid = true;
                } else {
                    data.put(key, defaultvalue);
                    System.out.println(defaultvalue);
                    valid = true;
                }
            } while (! valid);
        } catch (IOException ioe) {
            System.out.println("IO exception = " + ioe);
        }
    }

    public static void runTests() throws SQLException {
        try {
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(1000000);
            String temp = "";
            print(seperate);
            print("DATAMANAGER");
            print("getDatabase = " + dataManager.getDatabase());
            print("getHost = " + dataManager.getHost());
            print("getPassword = " + dataManager.getPassword());
            print("getPrefix = " + dataManager.getPrefix());
            print("getUsername = " + dataManager.getUsername());
            print("getConnection = " + dataManager.getConnection());
            print("getDataType = " + dataManager.getDataType());
            print("getPort = " + dataManager.getPort());
            print("getTimeout = " + dataManager.getTimeout());
            print("isConnected = " + dataManager.isConnected());
            print("isKeepAlive = " + dataManager.isKeepAlive());

            String username = data.get("script_username");

            print(seperate);

            print(script.toString() + " - " + version + " - SCRIPT CLASS");
            ScriptHandle tscript = bifrost.getScriptAPI().getHandle(script);
            printResult("getEncryption", tscript.getEncryption());
            printResult("getLatestVersion", tscript.getLatestVersion());
            printResult("getScriptName", tscript.getScriptName());
            printResult("getScriptShortname", tscript.getScriptShortname());
            printResult("getVersion", tscript.getVersion());
            printResult("getForumURL", tscript.getForumURL());
            printResult("getHomeURL", tscript.getHomeURL());
            printResult("getLastThread", "" + tscript.getLastThread());
            try {
                printResult("getBanCount", "" + tscript.getBanCount());
            } catch (UnsupportedFunction e) {
                printResult("getBanCount", "null");
            }
            try {
                printResult("getBans", "" + tscript.getBans(0));
            } catch (UnsupportedFunction e) {
                printResult("getBans", "null");
            }
            printResult("getGroupCount", "" + tscript.getGroupCount());
            printResult("getGroups", "" + tscript.getGroups(0));
            printResult("getLastThread", "" + tscript.getLastThread());
            printResult("getLastPost", "" + tscript.getLastPost());
            printResult("getLastRegUser", "" + tscript.getLastRegUser());
            printResult("getTotalPostCount", "" + tscript.getTotalPostCount());
            printResult("getTotalThreadCount",
                    "" + tscript.getTotalThreadCount());
            printResult("getThreads", "" + tscript.getThreads(0));
            printResult("getPosts", "" + tscript.getPosts(0));
            printResult("getUserCount", "" + tscript.getUserCount());
            try {
                printResult("isBanned", "" + tscript.isBanned("test"));
            } catch (UnsupportedFunction e) {
                printResult("isBanned", "null");
            }

            print(seperate);

            print(script.toString() + " - " + version + " - USER CLASS - "
                    + username);
            ScriptUser user = bifrost.getScriptAPI().getHandle(script)
                    .getUser(username);
            printResult("getAvatarURL", user.getAvatarURL());
            printResult("getBirthday", "" + user.getBirthday());
            printResult("getEmail", user.getEmail());
            printResult("getFirstName", user.getFirstName());
            printResult("getGender", "" + user.getGender());
            try {
                printResult("getIPs", "" + user.getIPs());
            } catch (UnsupportedFunction e) {
                printResult("getIPs", "null");
            }
            printResult("getLastIP", user.getLastIP());
            printResult("getLastLogin", "" + user.getLastLogin());
            printResult("getLastPost", "" + user.getLastPost());
            printResult("getLastThread", "" + user.getLastThread());
            printResult("getLastName", user.getLastName());
            printResult("getNickname", user.getNickname());
            printResult("getProfileURL", user.getProfileURL());
            printResult("getPassword", user.getPassword());
            printResult("getPasswordSalt", user.getPasswordSalt());
            try {
                printResult("getPMReceivedCount",
                        "" + user.getPMReceivedCount());
            } catch (UnsupportedFunction e) {
                printResult("getPMReceivedCount", "null");
            }
            try {
                printResult("getPMSentCount", "" + user.getPMSentCount());
            } catch (UnsupportedFunction e) {
                printResult("getPMSentCount", "null");
            }
            try {
                printResult("getPMsSent", "" + user.getPMsSent(0));
            } catch (UnsupportedFunction e) {
                printResult("getPMsSent", "null");
            }
            try {
                printResult("getPMsReceived", "" + user.getPMsReceived(0));
            } catch (UnsupportedFunction e) {
                printResult("getPMsReceived", "null");
            }
            printResult("getPostCount", "" + user.getPostCount());
            printResult("getRealName", user.getRealName());
            printResult("getRegIP", user.getRegIP());
            printResult("getRegDate", "" + user.getRegDate());
            printResult("getStatusMessage", user.getStatusMessage());
            printResult("getThreadCount", "" + user.getThreadCount());
            printResult("getUsername", user.getUsername());
            printResult("getUserTitle", user.getUserTitle());
            printResult("getUserID", "" + user.getID());
            printResult("getUserGroups", "" + user.getGroups());
            printResult("isActivated", "" + user.isActivated());
            try {
                printResult("isBanned", "" + user.isBanned());
            } catch (UnsupportedFunction e) {
                printResult("isBanned", "null");
            }
            printResult("isRegistered", "" + user.isRegistered());

            print(seperate);

            print(script.toString() + " - " + version + " - USER UPDATING");
            temp = user.getUsername();
            user.setUsername("Debug");
            user.setPassword("craftfire");
            user.updateUser();
            user.setUsername(temp);
            user.updateUser();

            print(seperate);

            print(script.toString() + " - " + version + " - USER CREATE");
            ScriptUser newUser = bifrost.getScriptAPI().getHandle(script)
                    .newScriptUser("craftfire" + randomInt, "craftfire");
            newUser.setNickname("testing" + randomInt);
            newUser.setUserTitle("title");
            newUser.setRegIP("127.0.0.1");
            newUser.setLastIP("127.0.0.1");
            newUser.setEmail("dev@craftfire.com");
            newUser.createUser();

            print(seperate);

            print(script.toString() + " - " + version + " - BAN CLASS");
            Ban ban = null;
            try {
                if (authAPI.getScript().getBans(1) != null) {
                    ban = bifrost.getScriptAPI().getHandle(script).getBans(1)
                            .get(0);
                }
            } catch (UnsupportedFunction e) {
            }
            if (ban != null) {
                printResult("getEmail", ban.getEmail());
                printResult("getIP", ban.getIP());
                printResult("getID", "" + ban.getID());
                printResult("getNotes", ban.getNotes());
                printResult("getReason", ban.getReason());
                printResult("getUsername", ban.getName());
                printResult("getEndDate", "" + ban.getEndDate());
                printResult("getStartDate", "" + ban.getStartDate());
                printResult("getTimeLength", "" + ban.getTimeLength());
                printResult("getTimeRemaining", "" + ban.getTimeRemaining());
                printResult("getUserID", "" + ban.getUserID());
                printResult("isPermanent", "" + ban.isPermanent());
            } else {
                print("NOT SUPPORTED");
            }

            print(seperate);

            print(script.toString() + " - " + version + " - BAN UPDATING");
            if (ban != null) {
                temp = ban.getReason();
                ban.setReason("Debug");
                ban.updateBan();
                ban.setReason(temp);
                ban.updateBan();
            } else {
                print("NOT SUPPORTED");
            }

            print(seperate);

            print(script.toString() + " - " + version + " - BAN CREATE");
            Ban newBan = bifrost
                    .getScriptAPI()
                    .getHandle(script)
                    .newBan("craftfire-ban-" + randomInt, "dev@craftfire.com",
                            "127.0.0.1");
            newBan.setNotes("Staff notes");
            newBan.setReason("Hello world!");
            try {
                newBan.addBan();
            } catch (UnsupportedFunction e) {
                print("NOT SUPPORTED");
            }

            print(seperate);

            print(script.toString() + " - " + version + " - GROUP CLASS");
            Group group = null;
            if (user.getGroups() != null) {
                group = user.getGroups().get(0);
                printResult("getName", group.getName());
                printResult("getID", "" + group.getID());
                printResult("getDescription", group.getDescription());
                printResult("getUserCount", "" + group.getUserCount());
                printResult("getUsers", "" + group.getUsers());
            }

            print(seperate);

            print(script.toString() + " - " + version + " - GROUP UPDATING");
            if (group != null) {
                try {
                    temp = group.getName();
                    group.setName("Debug");
                    group.updateGroup();
                    group.setName(temp);
                    group.updateGroup();
                } catch (UnsupportedFunction e) {
                    print("NOT SUPPORTED");
                }
            }

            print(seperate);

            print(script.toString() + " - " + version + " - GROUP CREATE");
            Group newGroup = bifrost.getScriptAPI().getHandle(script)
                    .newGroup("craftfire_group_" + randomInt);
            newGroup.setDescription("Description is not needed!");
            try {
                newGroup.createGroup();
            } catch (UnsupportedFunction e) {
                print("NOT SUPPORTED");
            }

            print(seperate);

            print(script.toString() + " - " + version + " - POST CLASS");
            Post post = user.getLastPost();
            if (post != null) {
                printResult("getAuthor", "" + post.getAuthor());
                printResult("getBody", post.getBody());
                printResult("getSubject", post.getSubject());
                printResult("getBoardID", "" + post.getBoardID());
                printResult("getPostDate", "" + post.getPostDate());
                printResult("getPostID", "" + post.getID());
                printResult("getThreadID", "" + post.getThreadID());

                print(seperate);

                print(script.toString() + " - " + version + " - POST UPDATING");
                temp = post.getSubject();
                post.setSubject("Debug");
                post.updatePost();
                post.setSubject(temp);
                post.updatePost();
            }

            print(seperate);

            print(script.toString() + " - " + version + " - POST CREATE");
            Post newPost = bifrost.getScriptAPI().getHandle(script)
                    .newPost(1, 2);
            newPost.setBody("Test: This it the body of the post?!");
            newPost.setAuthor(bifrost.getScriptAPI().getHandle(script)
                    .getUser("craftfire" + randomInt));
            newPost.setSubject("Test " + randomInt
                    + ": This is the subject of the post!");
            newPost.createPost();

            print(seperate);

            print(script.toString() + " - " + version
                    + " - PRIVATEMESSAGE CLASS");
            try {
                PrivateMessage pm = user.getPMsSent(1).get(0);
                printResult("getBody", pm.getBody());
                printResult("getSubject", "" + pm.getSubject());
                printResult("getSubject", pm.getSubject());
                printResult("getRecipients", "" + pm.getRecipients());
                printResult("getDate", "" + pm.getDate());
                printResult("getID", "" + pm.getID());

                print(seperate);

                print(script.toString() + " - " + version
                        + " - PRIVATEMESSAGE UPDATING");
                temp = pm.getBody();
                pm.setBody("Debug");
                pm.updatePrivateMessage();
                pm.setBody(temp);
                pm.updatePrivateMessage();
            } catch (UnsupportedFunction e) {
                print("NOT SUPPORTED");
            }
            print(seperate);

            print(script.toString() + " - " + version
                    + " - PRIVATEMESSAGE CREATE");
            ScriptUser from = bifrost.getScriptAPI().getHandle(script)
                    .getUser("Contex");
            List<ScriptUser> recipients = new ArrayList<ScriptUser>();
            recipients.add(bifrost.getScriptAPI().getHandle(script)
                    .getUser("Craftfire"));
            recipients.add(bifrost.getScriptAPI().getHandle(script)
                    .getUser("craftfire" + randomInt));
            PrivateMessage newPM = bifrost.getScriptAPI().getHandle(script)
                    .newPrivateMessage(from, recipients);
            newPM.setBody("This is an example body: " + randomInt);
            newPM.setSubject("This is an example subject: " + randomInt);
            newPM.setNew(
                    bifrost.getScriptAPI().getHandle(script)
                            .getUser("Craftfire"), true);
            try {
                newPM.createPrivateMessage();
            } catch (UnsupportedFunction e) {
                print("NOT SUPPORTED");
            }

            print(seperate);

            print(script.toString() + " - " + version + " - THREAD CLASS");
            Thread thread = bifrost.getScriptAPI().getHandle(script)
                    .getLastThread();
            printResult("getAuthor", "" + thread.getAuthor());
            printResult("getBody", thread.getBody());
            printResult("getSubject", thread.getSubject());
            printResult("getBoardID", "" + thread.getBoardID());
            printResult("getFirstPost", "" + thread.getFirstPost());
            printResult("getLastPost", "" + thread.getLastPost());
            printResult("getPosts", "" + thread.getPosts(0));
            printResult("getReplies", "" + thread.getReplies());
            printResult("getThreadDate", "" + thread.getThreadDate());
            printResult("getThreadID", "" + thread.getID());
            printResult("getViews", "" + thread.getViews());

            print(seperate);

            print(script.toString() + " - " + version + " - THREAD UPDATING");
            temp = thread.getSubject();
            thread.setSubject("Debug");
            thread.updateThread();
            thread.setSubject(temp);
            thread.updateThread();

            print(seperate);

            print(script.toString() + " - " + version + " - THREAD CREATE");
            Thread newThread = bifrost.getScriptAPI().getHandle(script)
                    .newThread(2);
            newThread.setBody("Test: " + randomInt
                    + " This it the body of the thread?!");
            newThread.setAuthor(bifrost.getScriptAPI().getHandle(script)
                    .getUser("craftfire" + randomInt));
			newThread.setSubject("Test: " + randomInt + " This is the subject of the thread!");
			newThread.createThread();

			print(seperate);
		} catch (UnsupportedFunction e) {
	   		e.printStackTrace();
		}
    }

    public static void print(String string) {
        System.out.println(string);
    }

    public static void printResult(String function, String data) {
        String line = "NOT SUPPORTED";
        String prefix = "-";
        if (data != null && ! data.equalsIgnoreCase("0") && ! data.equalsIgnoreCase("null")) {
            line = data;
            prefix = "+";
        }
        print(prefix + function + "() = " + line);
    }
}
