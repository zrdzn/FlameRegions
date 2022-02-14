/*
 * Copyright (c) 2022 zrdzn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.slf4j.Logger;

import java.util.Locale;
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

        Player player = event.getClicker();

        SpeechController speechController = npc.getDefaultSpeechController();

        SpeechContext speechContext = new SpeechContext();
        speechContext.setTalker(npc.getEntity());
        speechContext.addRecipient((Entity) player);

        Locale locale = player.locale();

        speechContext.setMessage(this.service.getRawString(locale,
                "npc.dialog.before_travel_" + ThreadLocalRandom.current().nextInt(1, 4),
                npc.getName()));

        speechController.speak(speechContext);

        if (!this.menu.show(player.getUniqueId(), true, npc)) {
            this.logger.warn("Could not show location-menu to {}.", player.getName());
            speechContext.setMessage(this.service.getRawString(locale, "menu.open_error"));
            speechController.speak(speechContext);
        }
    }

}
