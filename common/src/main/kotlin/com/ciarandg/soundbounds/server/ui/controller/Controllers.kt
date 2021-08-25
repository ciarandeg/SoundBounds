package com.ciarandg.soundbounds.server.ui.controller

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld

object PlayerControllers : LazyMap<ServerPlayerEntity, PlayerController>({ PlayerController(it) })
object WorldControllers : LazyMap<ServerWorld, WorldController>({ WorldController(it) })
