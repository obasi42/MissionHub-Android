package com.missionhub.api;

import com.missionhub.api.Api.ApiResponseParser;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ApiOptions {

    protected String method;
    protected String url;
    protected Map<String, String> headers;
    protected Map<String, String> params;
    protected Boolean authenticated;
    protected Set<String> includes;
    protected ApiResponseParser<?> responseParser;
    protected Long since;
    protected Long limit;
    protected Long offset;

    private ApiOptions(final Builder<?> builder) {
        method = builder.method;
        url = builder.url;
        headers = builder.headers;
        params = builder.params;
        authenticated = builder.authenticated;
        includes = builder.includes;
        responseParser = builder.responseParser;
        since = builder.since;
        limit = builder.limit;
        offset = builder.offset;
    }

    public static class Builder<T extends Builder<T>> {

        private String method;
        private String url;
        private Map<String, String> headers;
        private Map<String, String> params;
        private Boolean authenticated;
        private Set<String> includes;
        private ApiResponseParser<?> responseParser;
        private Long since;
        private Long limit;
        private Long offset;

        @SuppressWarnings("unchecked")
        protected T self() {
            return (T) this;
        }

        public T method(final String method) {
            this.method = method;
            return self();
        }

        public T url(final String url) {
            this.url = url;
            return self();
        }

        public T headers(final Map<String, String> headers) {
            this.headers = headers;
            return self();
        }

        public T params(final Map<String, String> params) {
            this.params = params;
            return self();
        }

        public T authenticated(final boolean authenticated) {
            this.authenticated = authenticated;
            return self();
        }

        public T responseParser(final ApiResponseParser<?> parser) {
            this.responseParser = parser;
            return self();
        }

        public T includes(final Set<String> includes) {
            this.includes = includes;
            return self();
        }

        public T include(final Object include) {
            if (includes == null) {
                includes = new HashSet<String>();
            }
            if (include instanceof Enum) {
                includes.add(((Enum<?>) include).name());
            } else {
                includes.add(include.toString());
            }
            return self();
        }

        public T merge(final ApiOptions options) {
            if (options != null) {
                if (options.method != null) {
                    method = options.method;
                }
                if (options.url != null) {
                    url = options.url;
                }
                if (options.headers != null) {
                    if (headers == null) {
                        headers = options.headers;
                    } else {
                        headers.putAll(options.headers);
                    }
                }
                if (options.params != null) {
                    if (params == null) {
                        params = options.params;
                    } else {
                        params.putAll(options.params);
                    }
                }
                if (options.authenticated != null) {
                    authenticated = options.authenticated;
                }
                if (options.includes != null) {
                    if (includes == null) {
                        includes = options.includes;
                    } else {
                        includes.addAll(options.includes);
                    }
                }
                if (options.responseParser != null) {
                    responseParser = options.responseParser;
                }
                if (options.since != null) {
                    since = options.since;
                }
                if (options.offset != null) {
                    offset = options.offset;
                }
                if (options.limit != null) {
                    limit = options.limit;
                }
            }

            return self();
        }

        public T since(final Long since) {
            this.since = since;
            return self();
        }

        public T offset(final Long offset) {
            this.offset = offset;
            return self();
        }

        public T limit(final Long limit) {
            this.limit = limit;
            return self();
        }

        public ApiOptions build() {
            return new ApiOptions(this);
        }


    }

    /**
     * Returns a builder for this object
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Builder<?> builder() {
        return new Builder();
    }

}