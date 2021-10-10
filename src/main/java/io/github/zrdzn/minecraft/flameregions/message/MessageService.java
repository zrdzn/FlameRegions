package io.github.zrdzn.minecraft.flameregions.message;

import net.kyori.adventure.text.Component;
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

public class MessageService {

    private final Logger logger;
    private final Server server;
    private final Map<Locale, ResourceBundle> bundleMap;

    public MessageService(Logger logger, Server server, Map<Locale, ResourceBundle> bundleMap) {
        this.server = server;
        this.logger = logger;
        this.bundleMap = bundleMap;
    }

    public String getString(Locale locale, String key, Object... replacements) {
        return String.format(this.getResourceBundle(locale).getString(key), replacements);
    }

    public Component getComponent(Locale locale, String key, Object... replacements) {
        return Component.text(this.getString(locale, key, replacements));
    }

    public List<Component> getComponentList(Locale locale, String key, Object... replacements) {
        List<Component> componentList = new ArrayList<>();

        Arrays.asList(this.getResourceBundle(locale).getStringArray(key)).forEach(string ->
                componentList.add(Component.text(String.format(string, replacements))));

        return componentList;
    }

    public void sendMessage(UUID playerId, String key, Object... replacements) {
        Player player = this.server.getPlayer(playerId);
        if (player == null) {
            this.logger.warn("There is not any online player with {} uuid.", playerId);
            return;
        }

        player.sendMessage(Component.text(this.getString(player.locale(), key, replacements)));
    }

    private ResourceBundle getResourceBundle(Locale locale) {
        ResourceBundle bundle = this.bundleMap.get(locale);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle("locale/locale", Locale.ENGLISH);
        }

        return bundle;
    }

}
