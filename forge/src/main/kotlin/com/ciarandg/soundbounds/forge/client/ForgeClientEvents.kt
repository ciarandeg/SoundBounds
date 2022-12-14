package com.ciarandg.soundbounds.forge.client

class ForgeClientEvents {
    // @SubscribeEvent
    // fun render(event: RenderLevelLastEvent) {
    //     val player = MinecraftClient.getInstance().player ?: return
    //     if (!player.itemsHand.any { it.item is Baton }) return

    //     // Offset by camera position, since it is reset by RenderWorldLastEvent
    //     val matrixStack = event.poseStack
    //     val cameraPos = MinecraftClient.getInstance().gameRenderer.camera.pos
    //     matrixStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z)

    //     // Render player's bounds selection
    //     MarkerSelectionRenderer.renderPlayerMarkerSelection(matrixStack)

    //     // Render region visualization
    //     val visualizationRegion = ClientWorldRegions[ClientPlayerModel.visualizationRegion]
    //     if (visualizationRegion == null) ClientPlayerModel.visualizationRegion = null
    //     else RegionVisualizationRenderer.renderRegionVisualization(matrixStack, visualizationRegion)
    // }
}