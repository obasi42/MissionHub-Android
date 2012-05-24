package com.missionhub.config;

public final class Config {

        public static enum Market {
                ANDROID, AMAZON, NOOK
        }

        public static final boolean debug = @config.mh.debug@;
        public static final boolean useCache = @config.mh.use_cache@;
        public static final String autoLoginToken = "@config.mh.auto_login_token@";

        public static final Market market = @config.mh.market@;

        public static final String baseUrl = "@config.mh.base_url@";
        public static final String oauthUrl = baseUrl + "/oauth";
        public static final String apiUrl = baseUrl + "/api";
        public static final String apiVersion = "2";
        public static final String oauthClientId = "@config.mh.api.client_id@";
        public static final String oauthClientSecret = "@config.mh.api.client_secret@";
        public static final String oauthScope = "userinfo,contacts,contact_assignment,followup_comments,roles,organization_info,meta,groups,communication";
        public static final String gAnalyticsKey = "@config.mh.ganalytics_key@";

}