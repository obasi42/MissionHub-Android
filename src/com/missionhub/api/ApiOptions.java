package com.missionhub.api;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import com.missionhub.api.Api.ApiResponseParser;
import com.missionhub.network.HttpClient.HttpMethod;
import com.missionhub.network.HttpClient.ResponseType;
import com.missionhub.network.HttpHeaders;
import com.missionhub.network.HttpParams;

public class ApiOptions {

	protected Object id;
	protected HttpMethod method;
	protected String url;
	protected HttpHeaders headers;
	protected HttpParams params;
	protected Boolean authenticated;
	protected ResponseType responseType;
	protected Set<String> includes;
	protected ExecutorService executor;
	protected ApiResponseParser<?> responseParser;
	protected Long since;
	protected Long limit;
	protected Long offset;

	private ApiOptions(final Builder<?> builder) {
		id = builder.id;
		method = builder.method;
		url = builder.url;
		headers = builder.headers;
		params = builder.params;
		authenticated = builder.authenticated;
		responseType = builder.responseType;
		includes = builder.includes;
		executor = builder.executor;
		responseParser = builder.responseParser;
		since = builder.since;
		limit = builder.limit;
		offset = builder.offset;
	}

	public static class Builder<T extends Builder<T>> {

		private Object id;
		private HttpMethod method;
		private String url;
		private HttpHeaders headers;
		private HttpParams params;
		private Boolean authenticated;
		private ResponseType responseType;
		private Set<String> includes;
		private ExecutorService executor;
		private ApiResponseParser<?> responseParser;
		private Long since;
		private Long limit;
		private Long offset;

		@SuppressWarnings("unchecked")
		protected T self() {
			return (T) this;
		}

		public T id(final Object id) {
			this.id = id;
			return self();
		}

		public T method(final HttpMethod method) {
			this.method = method;
			return self();
		}

		public T url(final String url) {
			this.url = url;
			return self();
		}

		public T headers(final HttpHeaders headers) {
			this.headers = headers;
			return self();
		}

		public T params(final HttpParams params) {
			this.params = params;
			return self();
		}

		public T authenticated(final boolean authenticated) {
			this.authenticated = authenticated;
			return self();
		}

		public T responseType(final ResponseType type) {
			this.responseType = type;
			return self();
		}

		public T executor(final ExecutorService executor) {
			this.executor = executor;
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
				if (options.id != null) {
					id = options.id;
				}
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
						headers.addAll(options.headers);
					}
				}
				if (options.params != null) {
					if (params == null) {
						params = options.params;
					} else {
						params.addAll(options.params);
					}
				}
				if (options.authenticated != null) {
					authenticated = options.authenticated;
				}
				if (options.responseType != null) {
					responseType = options.responseType;
				}
				if (options.includes != null) {
					if (includes == null) {
						includes = options.includes;
					} else {
						includes.addAll(options.includes);
					}
				}
				if (options.executor != null) {
					executor = options.executor;
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