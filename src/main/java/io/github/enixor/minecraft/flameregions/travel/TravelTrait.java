package io.github.enixor.minecraft.flameregions.travel;

import io.github.enixor.minecraft.flameregions.FlameRegionsPlugin;
import net.citizensnpcs.api.ai.speech.SpeechContext;
import net.citizensnpcs.api.ai.speech.SpeechController;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class TravelTrait extends Trait {

    public TravelTrait() {
        super("fr-trait-travel");
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        FlameRegionsPlugin plugin = FlameRegionsPlugin.getInstance();
        SpeechContext speechContext = new SpeechContext();

        NPC npc = event.getNPC();
        if (npc == null) {
            return;
        }

        if (npc != this.getNPC()) {
            return;
        }

        SpeechController speechController = npc.getDefaultSpeechController();

        speechContext.setTalker(npc.getEntity());

        Player player = event.getClicker();
        speechContext.addRecipient((Entity) player);

        UUID playerId = player.getUniqueId();

        speechContext.setMessage(plugin.translateToString(player.getLocale(),
                "npc.dialog.before_travel_" + ThreadLocalRandom.current().nextInt(1, 4),
                npc.getName()));
        speechController.speak(speechContext);

        if (!plugin.getMenu().show(playerId, true, npc)) {
            plugin.getLogger().warning("Could not show location-menu to " + player.getName());
            speechContext.setMessage(plugin.translateToString(player.getLocale(), "menu.open_error"));
            speechController.speak(speechContext);
        }
    }
}
