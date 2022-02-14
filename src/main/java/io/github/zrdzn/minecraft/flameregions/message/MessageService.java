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

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public interface MessageService {

    /**
     * Returns translated key as raw string.
     *
     * @param locale the locale of the user
     * @param key the key from localization file
     * @param replacements the replacements for optional placeholders
     *
     * @return a translated text
     */
    String getRawString(Locale locale, String key, Object... replacements);

    /**
     * Returns translated key as kyori component.
     *
     * @param locale the locale of the user
     * @param key the key from localization file
     * @param replacements the replacements for optional placeholders
     *
     * @return a translated text
     */
    Component getComponent(Locale locale, String key, Object... replacements);

    /**
     * Returns translated key as kyori components.
     *
     * @param locale the locale of the user
     * @param key the key from localization file
     * @param replacements the replacements for optional placeholders
     *
     * @return translated texts
     */
    List<Component> getComponentList(Locale locale, String key, Object... replacements);

    /**
     * Sends translated component to specified player.
     *
     * @param playerId the id of the player
     * @param key the key from localization file
     * @param replacements the replacements for optional placeholders
     */
    void sendMessage(UUID playerId, String key, Object... replacements);

}