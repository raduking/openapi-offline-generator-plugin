package org.oogp;

import java.util.ArrayList;
import java.util.List;

public record UserCacheInfo(String userId, String geoHash, List<String> info) {

	private UserCacheInfo(final Builder builder) {
		this(builder.userId, builder.geoHash, builder.info);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		private String userId;

		private String geoHash;

		private List<String> info = new ArrayList<>();

		private Builder() {
			// empty
		}

		public UserCacheInfo build() {
			return new UserCacheInfo(this);
		}

		public Builder userId(final String userId) {
			this.userId = userId;
			return this;
		}

		public Builder geoHash(final String geoHash) {
			this.geoHash = geoHash;
			return this;
		}

		public Builder info(final List<String> info) {
			this.info.clear();
			this.info.addAll(info);
			return this;
		}

		public String getGeoHash() {
			return geoHash;
		}
	}
}
