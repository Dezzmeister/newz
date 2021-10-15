package com.obama69.newz.session;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class WorldNewsSession {
	private static final String NEWS_FILE = "newz.json";
	
	private Optional<Component> newsText;
	private List<String> excludedUsers;
	
	public WorldNewsSession() throws FileNotFoundException {
		final NewsState state = loadState();
		excludedUsers = new ArrayList<String>();
		
		for (final String user : state.excludedUsers) {
			excludedUsers.add(user);
		}
		
		newsText = getNewsText(state.newsNbt);
	}
	
	public void changeNews(final Component newNews) throws IOException {
		newsText = Optional.of(newNews);
		
		persist();
	}
	
	public void excludeUser(final String username) throws IOException {
		excludedUsers.add(username);
		
		persist();
	}
	
	public boolean includeUser(final String username) throws IOException {
		if (!excludedUsers.contains(username)) {
			return false;
		}
		
		excludedUsers.remove(username);
		persist();
		return true;
	}
	
	public void disableNews() throws IOException {
		newsText = Optional.empty();
		persist();
	}
	
	public void showNews(final ServerPlayer player) {
		if (newsText.isEmpty() || excludedUsers.contains(player.getGameProfile().getName())) {
			return;
		}
		
		
		
		player.sendMessage(newsText.get(), player.getUUID());
	}
	
	private <T> T toNullable(final Optional<T> o) {
		if (o.isEmpty()) {
			return null;
		}
		
		return o.get();
	}
	
	private void persist() throws IOException {
		final Optional<String> nbtString = (newsText.isEmpty()) ? Optional.empty() : Optional.of(Component.Serializer.toJson(newsText.get()));
		final String[] excluded = new String[excludedUsers.size()];
		
		for (int i = 0; i < excludedUsers.size(); i++) {
			excluded[i] = excludedUsers.get(i);
		}
		
		final NewsState state = new NewsState(toNullable(nbtString), excluded);
		final Gson gson = new Gson();
		final String json = gson.toJson(state);
		
		Files.writeString(Paths.get(NEWS_FILE), json);
	}
	
	private Optional<Component> getNewsText(final String nbtString) {
		if (nbtString == null) {
			return Optional.empty();
		}
		
		return Optional.of(Component.Serializer.fromJson(nbtString));
	}
	
	
	private static NewsState loadState() throws FileNotFoundException {
		final File potentialStateFile = new File(NEWS_FILE);
		
		if (potentialStateFile.exists()) {
			return loadFromFile(NEWS_FILE);
		}
		
		return new NewsState(null, new String[0]);
	}
	
	private static NewsState loadFromFile(final String path) throws FileNotFoundException {
		final Gson gson = new Gson();
		final JsonReader reader = new JsonReader(new FileReader(path));
		
		return gson.fromJson(reader, NewsState.class);
	}
}
