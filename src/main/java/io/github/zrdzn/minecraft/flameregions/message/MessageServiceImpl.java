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
package io.github.zrdzn.minecraft.flameregions.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

public class MessageServiceImpl implements MessageService {

    private final Logger logger;
    private final Server server;
    private final Map<Locale, ResourceBundle> bundleMap;

    public MessageServiceImpl(Logger logger, Server server, Map<Locale, ResourceBundle> bundleMap) {
        this.server = server;
        this.logger = logger;
        this.bundleMap = bundleMap;
    }

    public String getRawString(Locale locale, String key, Object... replacements) {
        return String.format(this.getResourceBundle(locale).getString(key), replacements);
    }

    public Component getComponent(Locale locale, String key, Object... replacements) {
        return MiniMessage.get().parse(this.getRawString(locale, key, replacements));
    }

    public List<Component> getComponentList(Locale locale, String key, Object... replacements) {
        List<Component> componentList = new ArrayList<>();

        Arrays.asList(this.getResourceBundle(locale).getStringArray(key)).forEach(string ->
            componentList.add(MiniMessage.get().parse(String.format(string, replacements))));

        return componentList;
    }

    public void sendMessage(UUID playerId, String key, Object... replacements) {
        Player player = this.server.getPlayer(playerId);
        if (player == null) {
            this.logger.warn("There is not any online player with {} uuid.", playerId);
            return;
        }

        player.sendMessage(this.getComponent(player.locale(), key, replacements));
    }

    private ResourceBundle getResourceBundle(Locale locale) {
        return locale == null || this.bundleMap.get(locale) == null ?
            ResourceBundle.getBundle("locale/locale", Locale.US) :
            this.bundleMap.get(locale);
    }

}