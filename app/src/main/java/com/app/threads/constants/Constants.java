package com.app.threads.constants;

public interface Constants {

    // Attention! You can only change the values of the following constants:

    // YOUTUBE_API_KEY, EMOJI_KEYBOARD, WEB_SITE_AVAILABLE, GOOGLE_PAY_TEST_BUTTON, MY_AD_AFTER_ITEM_NUMBER,
    // APP_TEMP_FOLDER, VIDEO_FILE_MAX_SIZE, BILLING_KEY, WEB_SITE, CLIENT_ID, API_DOMAIN,
    // GHOST_MODE_COST, VERIFIED_BADGE_COST, DISABLE_ADS_COST
    // POST_CHARACTERS_LIMIT, HASHTAGS_COLOR

    // It is forbidden to change the value of constants, which are not indicated above !!!

    Boolean FACEBOOK_AUTHORIZATION = true; // Allow login, signup with Facebook and "Services" section in Settings
    Boolean UPGRADES_FEATURE = true; // Allow marketplace upgrades feature
    Boolean MARKETPLACE_FEATURE = true; // Allow marketplace feature

    Boolean EMOJI_KEYBOARD = true; // false = Do not display your own Emoji keyboard | true = allow display your own Emoji keyboard

    Boolean WEB_SITE_AVAILABLE = true; // false = Do not show menu items (Open in browser, Copy profile link) in profile page | true = show menu items (Open in browser, Copy profile link) in profile page

    Boolean GOOGLE_PAY_TEST_BUTTON = true; // false = Do not show google pay test button in section upgrades

    int MY_AD_AFTER_ITEM_NUMBER = 0;  //After first item

    String APP_TEMP_FOLDER = "network"; //directory for temporary storage of images from the camera

    int VIDEO_FILE_MAX_SIZE = 7340035; //Max size for video file in bytes | For example 7mb = 7*1024*1024

    // Your API KEY for playing youtube video in app | See here: http://ifsoft.co.uk/help/how_to_using_youtube_video_in_android_app/

    String YOUTUBE_API_KEY = "AIzaSyAz8TvzJtYkvsNNRsQz2T9atmQZOyc3OTk";

    // Google Pay settings | Settings for In-App Purchasing for Android | See here: http://ifsoft.co.uk/help/how_to_add_google_in_app_purchase/

    String BILLING_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAn6iTaSxTJhwjspvU2am8rrikr5lgb62lla1XOXy7gMqAqu7jbjxpSe/OT5ej0/H4FiCNnfuFT4eOuhWmeGyHpLid1ehPVC8JfoKBJ0IaO+uUcOnzW1XJ/NfqPwsV2L3CsfiJMHri593b8Uva+U1456JvCurFVOGyMkVvuAggDMcNcK2WpdEXWIYI6NfT5V4ycwugnk36zmby822CpIwdxZem781WbNHD0d/8Sdh1fhd0TFuJonSVDx2wYejty7JjVugYcNC/kC8VDz9o2R91RLhCIwKneVGsY+VNlQuqm7fsSGF0nyzxogpbzrYdsLLuuQlAACSHYuN2zOQnPLtuRwIDAQAB";

    String WEB_SITE = "https://mysocialnet.me/";  //web site url address

    // Client ID For identify the application | Must be the same with CLIENT_ID from server config: db.inc.php

    String CLIENT_ID = "1";  // Correct example: 12567 | Incorrect example: 0987

    // Client Secret | Text constant | Must be the same with CLIENT_SECRET from server config: db.inc.php

    String CLIENT_SECRET = "f*Hk86&_Hrfv7cjnf-I=yT";    // Example: "f*Hk86&_Hrfv7cjnf-I=yT"

    String API_DOMAIN = "http://35.154.181.95/";  // url address to which the application sends requests || http://10.0.2.2/ - for test on emulator in localhost [XAMPP]

    String API_FILE_EXTENSION = "";     // Attention! Do not change the value for this constant!
    String API_VERSION = "v2";          // Attention! Do not change the value for this constant!

    // Attention! Do not change values for next constants!

    String METHOD_NOTIFICATIONS_CLEAR = API_DOMAIN + "api/" + API_VERSION + "/method/notifications.clear" + API_FILE_EXTENSION;
    String METHOD_GUESTS_CLEAR = API_DOMAIN + "api/" + API_VERSION + "/method/guests.clear" + API_FILE_EXTENSION;

    String METHOD_GROUP_SEARCH_PRELOAD = API_DOMAIN + "api/" + API_VERSION + "/method/group.searchPreload" + API_FILE_EXTENSION;

    String METHOD_ACCOUNT_PRIVACY = API_DOMAIN + "api/" + API_VERSION + "/method/account.privacy" + API_FILE_EXTENSION;

    String METHOD_FRIENDS_REQUEST = API_DOMAIN + "api/" + API_VERSION + "/method/friends.sendRequest" + API_FILE_EXTENSION;
    String METHOD_FRIENDS_ACCEPT = API_DOMAIN + "api/" + API_VERSION + "/method/friends.acceptRequest" + API_FILE_EXTENSION;
    String METHOD_FRIENDS_REJECT = API_DOMAIN + "api/" + API_VERSION + "/method/friends.rejectRequest" + API_FILE_EXTENSION;
    String METHOD_FRIENDS_REMOVE = API_DOMAIN + "api/" + API_VERSION + "/method/friends.remove" + API_FILE_EXTENSION;
    String METHOD_FRIENDS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/friends.get" + API_FILE_EXTENSION;

    String METHOD_ACCOUNT_GET_SETTINGS = API_DOMAIN + "api/" + API_VERSION + "/method/account.getSettings" + API_FILE_EXTENSION;
    String METHOD_DIALOGS_NEW_GET = API_DOMAIN + "api/" + API_VERSION + "/method/dialogs_new.get" + API_FILE_EXTENSION;
    String METHOD_CHAT_UPDATE = API_DOMAIN + "api/" + API_VERSION + "/method/chat.update" + API_FILE_EXTENSION;

    String METHOD_ACCOUNT_LOGIN = API_DOMAIN + "api/" + API_VERSION + "/method/account.signIn" + API_FILE_EXTENSION;
    String METHOD_ACCOUNT_SIGNUP = API_DOMAIN + "api/" + API_VERSION + "/method/account.signUp" + API_FILE_EXTENSION;
    String METHOD_ACCOUNT_AUTHORIZE = API_DOMAIN + "api/" + API_VERSION + "/method/account.authorize" + API_FILE_EXTENSION;
    String METHOD_ACCOUNT_SET_GCM_TOKEN = API_DOMAIN + "api/" + API_VERSION + "/method/account.setGcmToken" + API_FILE_EXTENSION;
    String METHOD_ACCOUNT_LOGINBYFACEBOOK = API_DOMAIN + "api/" + API_VERSION + "/method/account.signInByFacebook" + API_FILE_EXTENSION;
    String METHOD_ACCOUNT_RECOVERY = API_DOMAIN + "api/" + API_VERSION + "/method/account.recovery" + API_FILE_EXTENSION;
    String METHOD_ACCOUNT_SETPASSWORD = API_DOMAIN + "api/" + API_VERSION + "/method/account.setPassword" + API_FILE_EXTENSION;
    String METHOD_ACCOUNT_DEACTIVATE = API_DOMAIN + "api/" + API_VERSION + "/method/account.deactivate" + API_FILE_EXTENSION;
    String METHOD_ACCOUNT_SAVE_SETTINGS = API_DOMAIN + "api/" + API_VERSION + "/method/account.saveSettings" + API_FILE_EXTENSION;
    String METHOD_ACCOUNT_CONNECT_TO_FACEBOOK = API_DOMAIN + "api/" + API_VERSION + "/method/account.connectToFacebook" + API_FILE_EXTENSION;
    String METHOD_ACCOUNT_DISCONNECT_FROM_FACEBOOK = API_DOMAIN + "api/" + API_VERSION + "/method/account.disconnectFromFacebook" + API_FILE_EXTENSION;
    String METHOD_ACCOUNT_LOGOUT = API_DOMAIN + "api/" + API_VERSION + "/method/account.logOut" + API_FILE_EXTENSION;
    String METHOD_ACCOUNT_SET_ALLOW_COMMENTS = API_DOMAIN + "api/" + API_VERSION + "/method/account.setAllowComments" + API_FILE_EXTENSION;
    String METHOD_ACCOUNT_SET_ALLOW_MESSAGES = API_DOMAIN + "api/" + API_VERSION + "/method/account.setAllowMessages" + API_FILE_EXTENSION;

    String METHOD_GIFTS_REMOVE = API_DOMAIN + "api/" + API_VERSION + "/method/gifts.remove" + API_FILE_EXTENSION;
    String METHOD_GIFTS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/gifts.get" + API_FILE_EXTENSION;
    String METHOD_GIFTS_SELECT = API_DOMAIN + "api/" + API_VERSION + "/method/gifts.select" + API_FILE_EXTENSION;
    String METHOD_GIFTS_SEND = API_DOMAIN + "api/" + API_VERSION + "/method/gifts.send" + API_FILE_EXTENSION;

    String METHOD_ACCOUNT_SET_GEO_LOCATION = API_DOMAIN + "api/" + API_VERSION + "/method/account.setGeoLocation" + API_FILE_EXTENSION;

    String METHOD_PROFILE_PEOPLE_NEARBY_GET = API_DOMAIN + "api/" + API_VERSION + "/method/profile.getPeopleNearby" + API_FILE_EXTENSION;

    String METHOD_GUESTS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/guests.get" + API_FILE_EXTENSION;

    String METHOD_SUPPORT_SEND_TICKET = API_DOMAIN + "api/" + API_VERSION + "/method/support.sendTicket" + API_FILE_EXTENSION;

    String METHOD_PROFILE_GET = API_DOMAIN + "api/" + API_VERSION + "/method/profile.get" + API_FILE_EXTENSION;
    String METHOD_PROFILE_FOLLOWINGS = API_DOMAIN + "api/" + API_VERSION + "/method/profile.followings" + API_FILE_EXTENSION;
    String METHOD_PROFILE_FOLLOWERS = API_DOMAIN + "api/" + API_VERSION + "/method/profile.followers" + API_FILE_EXTENSION;
    String METHOD_PROFILE_FOLLOW = API_DOMAIN + "api/" + API_VERSION + "/method/profile.follow" + API_FILE_EXTENSION;
    String METHOD_WALL_GET = API_DOMAIN + "api/" + API_VERSION + "/method/wall.get" + API_FILE_EXTENSION;

    String METHOD_GROUP_CREATE = API_DOMAIN + "api/" + API_VERSION + "/method/group.create" + API_FILE_EXTENSION;
    String METHOD_GROUP_SAVE_SETTINGS = API_DOMAIN + "api/" + API_VERSION + "/method/group.saveSettings" + API_FILE_EXTENSION;
    String METHOD_GROUP_GET = API_DOMAIN + "api/" + API_VERSION + "/method/group.get" + API_FILE_EXTENSION;
    String METHOD_GROUP_GET_MY_GROUPS = API_DOMAIN + "api/" + API_VERSION + "/method/group.getMyGroups" + API_FILE_EXTENSION;
    String METHOD_GROUP_GET_MANAGED_GROUPS = API_DOMAIN + "api/" + API_VERSION + "/method/group.getManagedGroups" + API_FILE_EXTENSION;
    String METHOD_GROUP_GET_FOLLOWERS = API_DOMAIN + "api/" + API_VERSION + "/method/group.getFollowers" + API_FILE_EXTENSION;
    String METHOD_GROUP_FOLLOW = API_DOMAIN + "api/" + API_VERSION + "/method/group.follow" + API_FILE_EXTENSION;
    String METHOD_GROUP_SEARCH = API_DOMAIN + "api/" + API_VERSION + "/method/group.search" + API_FILE_EXTENSION;
    String METHOD_GROUP_UPLOADPHOTO = API_DOMAIN + "api/" + API_VERSION + "/method/group.uploadPhoto" + API_FILE_EXTENSION;
    String METHOD_GROUP_GET_WALL = API_DOMAIN + "api/" + API_VERSION + "/method/group.getWall" + API_FILE_EXTENSION;

    String METHOD_BLACKLIST_GET = API_DOMAIN + "api/" + API_VERSION + "/method/blacklist.get" + API_FILE_EXTENSION;
    String METHOD_BLACKLIST_ADD = API_DOMAIN + "api/" + API_VERSION + "/method/blacklist.add" + API_FILE_EXTENSION;
    String METHOD_BLACKLIST_REMOVE = API_DOMAIN + "api/" + API_VERSION + "/method/blacklist.remove" + API_FILE_EXTENSION;

    String METHOD_NOTIFICATIONS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/notifications.get" + API_FILE_EXTENSION;
    String METHOD_HASHTAGS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/hashtags.get" + API_FILE_EXTENSION;
    String METHOD_FEEDS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/feeds.get" + API_FILE_EXTENSION;

    String UPDATE_LAT_LONG = API_DOMAIN + "api/" + API_VERSION + "/method/account.setGeoLocation" + API_FILE_EXTENSION; //update lat long

    String METHOD_ITEM_GET = API_DOMAIN + "api/" + API_VERSION + "/method/item.get" + API_FILE_EXTENSION;
    String METHOD_STREAM_GET = API_DOMAIN + "api/" + API_VERSION + "/method/stream.get" + API_FILE_EXTENSION;
    String METHOD_POPULAR_GET = API_DOMAIN + "api/" + API_VERSION + "/method/popular.get" + API_FILE_EXTENSION;

    String METHOD_APP_CHECKUSERNAME = API_DOMAIN + "api/" + API_VERSION + "/method/app.checkUsername" + API_FILE_EXTENSION;
    String METHOD_APP_TERMS = API_DOMAIN + "api/" + API_VERSION + "/method/app.terms" + API_FILE_EXTENSION;
    String METHOD_APP_THANKS = API_DOMAIN + "api/" + API_VERSION + "/method/app.thanks" + API_FILE_EXTENSION;
    String METHOD_APP_SEARCH = API_DOMAIN + "api/" + API_VERSION + "/method/app.search" + API_FILE_EXTENSION;

    String METHOD_APP_SEARCH_PRELOAD = API_DOMAIN + "api/" + API_VERSION + "/method/app.searchPreload" + API_FILE_EXTENSION;

    String METHOD_ITEMS_REMOVE = API_DOMAIN + "api/" + API_VERSION + "/method/items.remove" + API_FILE_EXTENSION;
    String METHOD_ITEMS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/items.get" + API_FILE_EXTENSION;
    String METHOD_ITEMS_UPLOAD_IMG = API_DOMAIN + "api/" + API_VERSION + "/method/items.uploadImg" + API_FILE_EXTENSION;
    String METHOD_ITEMS_NEW = API_DOMAIN + "api/" + API_VERSION + "/method/items.new" + API_FILE_EXTENSION;
    String METHOD_ITEMS_EDIT = API_DOMAIN + "api/" + API_VERSION + "/method/items.edit" + API_FILE_EXTENSION;

    String METHOD_FAVORITES_GET = API_DOMAIN + "api/" + API_VERSION + "/method/favorites.get" + API_FILE_EXTENSION;

    String METHOD_CHAT_GET = API_DOMAIN + "api/" + API_VERSION + "/method/chat.get" + API_FILE_EXTENSION;
    String METHOD_CHAT_REMOVE = API_DOMAIN + "api/" + API_VERSION + "/method/chat.remove" + API_FILE_EXTENSION;
    String METHOD_CHAT_GET_PREVIOUS = API_DOMAIN + "api/" + API_VERSION + "/method/chat.getPrevious" + API_FILE_EXTENSION;

    String METHOD_MSG_NEW = API_DOMAIN + "api/" + API_VERSION + "/method/msg.new" + API_FILE_EXTENSION;
    String METHOD_MSG_UPLOAD_IMG = API_DOMAIN + "api/" + API_VERSION + "/method/msg.uploadImg" + API_FILE_EXTENSION;

    String METHOD_REFERRALS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/referrals.get" + API_FILE_EXTENSION;

    String METHOD_MARKET_SEARCH = API_DOMAIN + "api/" + API_VERSION + "/method/market.search" + API_FILE_EXTENSION;
    String METHOD_MARKET_SEARCH_PRELOAD = API_DOMAIN + "api/" + API_VERSION + "/method/market.preload" + API_FILE_EXTENSION;
    String METHOD_MARKET_NEW_ITEM = API_DOMAIN + "api/" + API_VERSION + "/method/market.newItem" + API_FILE_EXTENSION;
    String METHOD_MARKET_REMOVE_ITEM = API_DOMAIN + "api/" + API_VERSION + "/method/market.removeItem" + API_FILE_EXTENSION;
    String METHOD_MARKET_UPLOAD_IMG = API_DOMAIN + "api/" + API_VERSION + "/method/market.uploadImg" + API_FILE_EXTENSION;
    String METHOD_MARKET_GET_MY_ITEMS = API_DOMAIN + "api/" + API_VERSION + "/method/market.getMyItems" + API_FILE_EXTENSION;

    // added for version 3.9

    String METHOD_GET_STICKERS = API_DOMAIN + "api/" + API_VERSION + "/method/stickers.get" + API_FILE_EXTENSION;

    // added for version 4.1

    String METHOD_CHAT_NOTIFY = API_DOMAIN + "api/" + API_VERSION + "/method/chat.notify" + API_FILE_EXTENSION;

    // added for version 4.3

    String METHOD_ACCOUNT_SET_PRO_MODE = API_DOMAIN + "api/" + API_VERSION + "/method/account.setProMode" + API_FILE_EXTENSION;

    // added for version 4.6

    String METHOD_FEELINGS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/feelings.get" + API_FILE_EXTENSION;
    String METHOD_ACCOUNT_SET_FEELING = API_DOMAIN + "api/" + API_VERSION + "/method/account.setMood" + API_FILE_EXTENSION;

    // added for version 4.9

    String METHOD_APP_CHECK_EMAIL = API_DOMAIN + "api/" + API_VERSION + "/method/app.checkEmail" + API_FILE_EXTENSION;
    String METHOD_ITEM_GET_IMAGES = API_DOMAIN + "api/" + API_VERSION + "/method/item.getImages" + API_FILE_EXTENSION;
    String METHOD_ITEM_GET_COMMENTS = API_DOMAIN + "api/" + API_VERSION + "/method/item.getComments" + API_FILE_EXTENSION;
    String METHOD_PROFILE_UPLOAD_IMAGE = API_DOMAIN + "api/" + API_VERSION + "/method/profile.uploadImg" + API_FILE_EXTENSION;

    // added for version 5.0

    String METHOD_ACCOUNT_SET_ALLOW_GALLERY_COMMENTS = API_DOMAIN + "api/" + API_VERSION + "/method/account.setAllowGalleryComments" + API_FILE_EXTENSION;

    String METHOD_GALLERY_UPLOAD_IMAGE = API_DOMAIN + "api/" + API_VERSION + "/method/gallery.uploadImg" + API_FILE_EXTENSION;
    String METHOD_GALLERY_UPLOAD_VIDEO = API_DOMAIN + "api/" + API_VERSION + "/method/gallery.uploadVideo" + API_FILE_EXTENSION;
    String METHOD_GALLERY_ITEM_NEW = API_DOMAIN + "api/" + API_VERSION + "/method/gallery.add" + API_FILE_EXTENSION;
    String METHOD_GALLERY_ITEM_REMOVE = API_DOMAIN + "api/" + API_VERSION + "/method/gallery.remove" + API_FILE_EXTENSION;
    String METHOD_GALLERY_ITEM_INFO = API_DOMAIN + "api/" + API_VERSION + "/method/gallery.info" + API_FILE_EXTENSION;
    String METHOD_GALLERY_GET = API_DOMAIN + "api/" + API_VERSION + "/method/gallery.get" + API_FILE_EXTENSION;

    String METHOD_REPORT_NEW = API_DOMAIN + "api/" + API_VERSION + "/method/reports.new" + API_FILE_EXTENSION;

    String METHOD_COMMENTS_REMOVE = API_DOMAIN + "api/" + API_VERSION + "/method/comments.remove" + API_FILE_EXTENSION;
    String METHOD_COMMENTS_NEW = API_DOMAIN + "api/" + API_VERSION + "/method/comments.new" + API_FILE_EXTENSION;

    String METHOD_LIKES_LIKE = API_DOMAIN + "api/" + API_VERSION + "/method/likes.like" + API_FILE_EXTENSION;
    String METHOD_LIKES_GET = API_DOMAIN + "api/" + API_VERSION + "/method/likes.get" + API_FILE_EXTENSION;


    String METHOD_VIDEO_UPLOAD = API_DOMAIN + "api/" + API_VERSION + "/method/gallery.uploadVideo" + API_FILE_EXTENSION;

    // added for version 5.1

    String METHOD_ACCOUNT_UPGRADE = API_DOMAIN + "api/" + API_VERSION + "/method/account.upgrade" + API_FILE_EXTENSION;

    String METHOD_PAYMENTS_NEW = API_DOMAIN + "api/" + API_VERSION + "/method/payments.new" + API_FILE_EXTENSION;
    String METHOD_PAYMENTS_GET = API_DOMAIN + "api/" + API_VERSION + "/method/payments.get" + API_FILE_EXTENSION;

    // added for version 5.7

    String METHOD_ITEMS_RESTORE = API_DOMAIN + "api/" + API_VERSION + "/method/items.restore" + API_FILE_EXTENSION;
    String METHOD_ITEMS_GET_RECENTLY_DELETED = API_DOMAIN + "api/" + API_VERSION + "/method/items.getRecentlyDeleted" + API_FILE_EXTENSION;

    // Other Constants

    int APP_TYPE_ALL = -1;
    int APP_TYPE_MANAGER = 0;
    int APP_TYPE_WEB = 1;
    int APP_TYPE_ANDROID = 2;
    int APP_TYPE_IOS = 3;


    int IMAGE_TYPE_PROFILE_PHOTO = 0;
    int IMAGE_TYPE_PROFILE_COVER = 1;

    int GALLERY_ITEM_TYPE_IMAGE = 0;
    int GALLERY_ITEM_TYPE_VIDEO = 1;

    int REPORT_TYPE_ITEM = 0;
    int REPORT_TYPE_PROFILE = 1;
    int REPORT_TYPE_MESSAGE = 2;
    int REPORT_TYPE_COMMENT = 3;
    int REPORT_TYPE_GALLERY_ITEM = 4;
    int REPORT_TYPE_MARKET_ITEM = 5;
    int REPORT_TYPE_COMMUNITY = 6;

    int VOLLEY_REQUEST_SECONDS = 15; //SECONDS TO REQUEST

    int POST_TYPE_DEFAULT = 0;
    int POST_TYPE_PHOTO_UPDATE = 1;
    int POST_TYPE_COVER_UPDATE = 2;

    int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO = 1;                  //WRITE_EXTERNAL_STORAGE
    int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_COVER = 2;                  //WRITE_EXTERNAL_STORAGE
    int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 3;                               //ACCESS_COARSE_LOCATION
    int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_VIDEO_IMAGE = 4;            //WRITE_EXTERNAL_STORAGE

    int GHOST_MODE_COST = 100;      //Cost in Credits
    int VERIFIED_BADGE_COST = 150;  //Cost in Credits
    int PRO_MODE_COST = 170;        //Cost in Credits
    int DISABLE_ADS_COST = 200;     //Cost in Credits

    int LIST_ITEMS = 20;

    int POST_CHARACTERS_LIMIT = 1000;

    int ENABLED = 1;
    int DISABLED = 0;

    int GCM_ENABLED = 1;
    int GCM_DISABLED = 0;

    int ADMOB_ENABLED = 1;
    int ADMOB_DISABLED = 0;

    int COMMENTS_ENABLED = 1;
    int COMMENTS_DISABLED = 0;

    int MESSAGES_ENABLED = 1;
    int MESSAGES_DISABLED = 0;

    int ERROR_SUCCESS = 0;

    int SEX_UNKNOWN = 0;
    int SEX_MALE = 1;
    int SEX_FEMALE = 2;

    int NOTIFY_TYPE_LIKE = 0;
    int NOTIFY_TYPE_FOLLOWER = 1;
    int NOTIFY_TYPE_MESSAGE = 2;
    int NOTIFY_TYPE_COMMENT = 3;
    int NOTIFY_TYPE_COMMENT_REPLY = 4;
    int NOTIFY_TYPE_FRIEND_REQUEST_ACCEPTED = 5;
    int NOTIFY_TYPE_GIFT = 6;

    int NOTIFY_TYPE_IMAGE_COMMENT = 7;
    int NOTIFY_TYPE_IMAGE_COMMENT_REPLY = 8;
    int NOTIFY_TYPE_IMAGE_LIKE = 9;

    int NOTIFY_TYPE_VIDEO_COMMENT = 10;
    int NOTIFY_TYPE_VIDEO_COMMENT_REPLY = 11;
    int NOTIFY_TYPE_VIDEO_LIKE = 12;

    int NOTIFY_TYPE_PROFILE_PHOTO_APPROVE = 2003;
    int NOTIFY_TYPE_PROFILE_PHOTO_REJECT = 2004;
    int NOTIFY_TYPE_PROFILE_COVER_APPROVE = 2007;
    int NOTIFY_TYPE_PROFILE_COVER_REJECT = 2008;

    int NOTIFY_TYPE_REFERRAL = 14;

    int GCM_NOTIFY_CONFIG = 0;
    int GCM_NOTIFY_SYSTEM = 1;
    int GCM_NOTIFY_CUSTOM = 2;
    int GCM_NOTIFY_LIKE = 3;
    int GCM_NOTIFY_ANSWER = 4;
    int GCM_NOTIFY_QUESTION = 5;
    int GCM_NOTIFY_COMMENT = 6;
    int GCM_NOTIFY_FOLLOWER = 7;
    int GCM_NOTIFY_PERSONAL = 8;
    int GCM_NOTIFY_MESSAGE = 9;
    int GCM_NOTIFY_COMMENT_REPLY = 10;
    int GCM_FRIEND_REQUEST_INBOX = 11;
    int GCM_FRIEND_REQUEST_ACCEPTED = 12;
    int GCM_NOTIFY_GIFT = 14;
    int GCM_NOTIFY_SEEN = 15;
    int GCM_NOTIFY_TYPING = 16;
    int GCM_NOTIFY_URL = 17;

    int GCM_NOTIFY_IMAGE_COMMENT_REPLY = 18;
    int GCM_NOTIFY_IMAGE_COMMENT = 19;
    int GCM_NOTIFY_IMAGE_LIKE = 20;

    int GCM_NOTIFY_VIDEO_COMMENT_REPLY = 21;
    int GCM_NOTIFY_VIDEO_COMMENT = 22;
    int GCM_NOTIFY_VIDEO_LIKE = 23;

    int GCM_NOTIFY_REFERRAL = 24;

    int GCM_NOTIFY_TYPING_START = 27;
    int GCM_NOTIFY_TYPING_END = 28;

    int GCM_NOTIFY_PROFILE_PHOTO_APPROVE = 1003;
    int GCM_NOTIFY_PROFILE_PHOTO_REJECT = 1004;
    int GCM_NOTIFY_PROFILE_COVER_APPROVE = 1007;
    int GCM_NOTIFY_PROFILE_COVER_REJECT = 1008;


    int ERROR_LOGIN_TAKEN = 300;
    int ERROR_EMAIL_TAKEN = 301;
    int ERROR_FACEBOOK_ID_TAKEN = 302;

    int ERROR_MULTI_ACCOUNT = 500;

    int ERROR_CLIENT_ID = 19100;
    int ERROR_CLIENT_SECRET = 19101;

    int ACCOUNT_STATE_ENABLED = 0;
   int ACCOUNT_STATE_BLOCKED = 2;
    int ACCOUNT_STATE_DEACTIVATED = 3;

    int ACCOUNT_TYPE_USER = 0;
    int ACCOUNT_TYPE_GROUP = 1;

    int ERROR_UNKNOWN = 100;
    int ERROR_ACCESS_TOKEN = 101;

    int ERROR_ACCOUNT_ID = 400;

    int UPLOAD_TYPE_PHOTO = 0;
    int UPLOAD_TYPE_COVER = 1;

    int ACTION_NEW = 1;
    int ACTION_EDIT = 2;
    int SELECT_POST_IMG = 3;
    int VIEW_CHAT = 4;
    int CREATE_POST_IMG = 5;
    int SELECT_CHAT_IMG = 6;
    int CREATE_CHAT_IMG = 7;
    int FEED_NEW_POST = 8;
    int FRIENDS_SEARCH = 9;
    int ITEM_EDIT = 10;
    int STREAM_NEW_POST = 11;
    int ITEM_REPOST = 12;
    int ITEM_ACTIONS_MENU = 14;
    int ITEM_ACTION_REPOST = 15;

    int ITEM_TYPE_IMAGE = 0;
    int ITEM_TYPE_VIDEO = 1;
    int ITEM_TYPE_POST = 2;
    int ITEM_TYPE_COMMENT = 3;
    int ITEM_TYPE_GALLERY = 4;

    int PA_BUY_CREDITS = 0;
    int PA_BUY_GIFT = 1;
    int PA_BUY_VERIFIED_BADGE = 2;
    int PA_BUY_GHOST_MODE = 3;
    int PA_BUY_DISABLE_ADS = 4;
    int PA_BUY_REGISTRATION_BONUS = 5;
    int PA_BUY_REFERRAL_BONUS = 6;
    int PA_BUY_MANUAL_BONUS = 7;
    int PA_BUY_PRO_MODE = 8;
    int PA_BUY_SPOTLIGHT = 9;
    int PA_BUY_MESSAGE_PACKAGE = 10;

    int PT_UNKNOWN = 0;
    int PT_CREDITS = 1;
    int PT_CARD = 2;
    int PT_GOOGLE_PURCHASE = 3;
    int PT_APPLE_PURCHASE = 4;
    int PT_ADMOB_REWARDED_ADS = 5;
    int PT_BONUS = 6;

    String TAG = "TAG";

    String HASHTAGS_COLOR = "#5BCFF2";

    String TAG_UPDATE_BADGES = "update_badges";
}