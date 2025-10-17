package org.oogp;

import java.util.ArrayList;
import java.util.List;

public record UserCacheInfo(String tmUserId, String geoHash, List<String> info) {

	private UserCacheInfo(Builder builder) {
		this(builder.tmUserId, builder.geoHash, builder.info);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		private String tmUserId;

		private String geoHash;

		private List<String> info = new ArrayList<>();

		private Builder() {
			// empty
		}

		public UserCacheInfo build() {
			return new UserCacheInfo(this);
		}

		public Builder tmUserId(String tmUserId) {
			this.tmUserId = tmUserId;
			return this;
		}

		public Builder geoHash(String geoHash) {
			this.geoHash = geoHash;
			return this;
		}

		public Builder info(List<String> info) {
			this.info.clear();
			this.info.addAll(info);
			return this;
		}

		public String getGeoHash() {
			return geoHash;
		}
	}
}
