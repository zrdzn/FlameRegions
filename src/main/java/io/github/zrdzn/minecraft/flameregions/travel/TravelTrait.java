package io.github.zrdzn.minecraft.flameregions.travel;

import io.github.zrdzn.minecraft.flameregions.location.LocationMenu;
import io.github.zrdzn.minecraft.flameregions.message.MessageService;
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

    private final Logger logger;
    private final MessageService service;
    private final LocationMenu menu;

    public TravelTrait(Logger logger, MessageService service, LocationMenu menu) {
        super("fr-trait-travel");
        this.logger = logger;
        this.service = service;
        this.menu = menu;
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        NPC npc = event.getNPC();
        if (npc == null) {
            return;
        }

        if (npc != this.getNPC()) {
            return;
        }

        SpeechController speechController = npc.getDefaultSpeechController();

        SpeechContext speechContext = new SpeechContext();

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
