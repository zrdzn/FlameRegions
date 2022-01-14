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