package com.obama69.newz.commands;

import java.io.IOException;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.obama69.newz.Newz;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class NewzCommand {

	private static final DynamicCommandExceptionType UNKNOWN_ERROR = new DynamicCommandExceptionType(arg -> {
		return new TextComponent("Something went wrong").withStyle(ChatFormatting.RED);
	});

	public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {		
		dispatcher.register(
				Commands.literal("news")
				.requires(s -> s.hasPermission(0))
				.then(
						Commands.literal("set")
						.requires(s -> s.hasPermission(3))
						.then(
								Commands.argument("newsNbt", ComponentArgument.textComponent())
								.executes(NewzCommand::setNewsText)
						)
				)
				.then(
						Commands.literal("clear")
						.requires(s -> s.hasPermission(3))
						.executes(NewzCommand::clearNews)
				)
				.then(
						Commands.literal("preview")
						.requires(s -> s.hasPermission(3))
						.executes(NewzCommand::previewNews)
				)
				.then(
						Commands.literal("hide")
						.executes(NewzCommand::hideNews)
				)
				.then(
						Commands.literal("show")
						.executes(NewzCommand::showNews)
				)
		);
	}
	
	private static int showNews(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		final ServerPlayer player = context.getSource().getPlayerOrException();

		try {
			Newz.session.includeUser(player.getGameProfile().getName());
		} catch (IOException e) {
			e.printStackTrace();
			throw UNKNOWN_ERROR.create("");
		}
		
		final MutableComponent message = new TextComponent("You will see news again. To undo this, run '/news hide'").withStyle(ChatFormatting.GREEN);
		player.sendMessage(message, player.getUUID());
		
		return 1;
	}

	private static int hideNews(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		final ServerPlayer player = context.getSource().getPlayerOrException();

		try {
			Newz.session.excludeUser(player.getGameProfile().getName());
		} catch (IOException e) {
			e.printStackTrace();
			throw UNKNOWN_ERROR.create("");
		}
		
		final MutableComponent message = new TextComponent("You won't see news anymore. To undo this, run '/news show'").withStyle(ChatFormatting.GREEN);
		player.sendMessage(message, player.getUUID());
		
		return 1;
	}
	
	private static int previewNews(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		final ServerPlayer player = context.getSource().getPlayerOrException();
		
		Newz.session.showNews(player);
		
		return 1;
	}
	
	private static int clearNews(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		final Entity source = context.getSource().getEntity();

		try {
			Newz.session.disableNews();
		} catch (IOException e) {
			e.printStackTrace();
			throw UNKNOWN_ERROR.create("");
		}
		
		final MutableComponent message = new TextComponent("Cleared news text").withStyle(ChatFormatting.GREEN);
		source.sendMessage(message, source.getUUID());
		
		return 1;
	}
	
	private static int setNewsText(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		final Entity source = context.getSource().getEntity();
		final Component newsText = ComponentArgument.getComponent(context, "newsNbt");
		try {
			Newz.session.changeNews(newsText);
		} catch (IOException e) {
			e.printStackTrace();
			throw UNKNOWN_ERROR.create(newsText);
		}
		
		final MutableComponent message = new TextComponent("Changed news text").withStyle(ChatFormatting.GREEN);
		source.sendMessage(message, source.getUUID());
		
		return 1;
	}
}
