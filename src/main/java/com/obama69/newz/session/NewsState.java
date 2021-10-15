package com.obama69.newz.session;

import javax.annotation.Nullable;

public class NewsState {
	/**
	 * News NBT string to show to users who log in
	 */
	public final String newsNbt;
	
	/**
	 * List of usernames who don't want to see the news
	 */
	public final String[] excludedUsers;
	
	public NewsState(@Nullable final String _newsNbt, final String[] _excludedUsers) {
		newsNbt = _newsNbt;
		excludedUsers = _excludedUsers;
	}
}
