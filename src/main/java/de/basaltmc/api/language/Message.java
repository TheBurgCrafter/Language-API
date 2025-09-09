package de.basaltmc.api.language;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code Message} class provides functionality to retrieve localized messages
 * from JSON language files. It supports simple retrieval, regex-based replacement,
 * and multiple key-value replacements for dynamic message formatting.
 *
 * <p>
 * This class works in conjunction with {@link LanguageManager}, using its configured
 * language and directory paths to locate language files.
 *
 * <p><b>Core Features:</b></p>
 * <ul>
 *     <li>Retrieve localized messages by key/path from JSON files.</li>
 *     <li>Perform regex-based replacements on messages.</li>
 *     <li>Format messages with multiple key-value replacements.</li>
 *     <li>Dynamic support for switching languages through {@link LanguageManager}.</li>
 * </ul>
 *
 * <p><b>Usage Scenarios:</b></p>
 * <ol>
 *     <li>Applications requiring runtime message localization.</li>
 *     <li>Dynamic templates where placeholders in messages need to be replaced with runtime values.</li>
 *     <li>Games or tools where messages are stored in JSON files for different languages.</li>
 * </ol>
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * LanguageManager langManager = new LanguageManager();
 * langManager.setup(true, "resources/lang", "en");
 *
 * Message message = new Message(langManager);
 *
 * // Simple retrieval
 * String welcome = message.get("welcome.message");
 *
 * // Regex-based replacement
 * String customMessage = message.get("welcome.message", "%username%", "Alice");
 *
 * // Multiple replacements
 * HashMap<String, String> replacements = new HashMap<>();
 * replacements.put("%username%", "TheBurgCrafter");
 * replacements.put("%errorID%", "418");
 * String formatted = message.get("game.over", replacements);
 * }</pre>
 */
public class Message {

    /** The currently active language (e.g., "en"). */
    private String lang;

    /** Directory path where language files are stored. */
    private String dir;

    /**
     * Constructs a {@code Message} instance using a {@link LanguageManager}.
     * The language and directory are automatically retrieved from the manager.
     *
     * @param languageManager the LanguageManager instance providing language configuration.
     */
    public Message(LanguageManager languageManager) {
        this.lang = languageManager.getLang();
        this.dir = languageManager.getDir();
    }

    /**
     * Constructs a {@code Message} instance with explicit language and directory values.
     *
     * @param lang the language identifier (e.g., "en").
     * @param dir  the directory path where language files are stored.
     */
    public Message(String lang, String dir) {
        this.lang = lang;
        this.dir = dir;
    }

    /**
     * Retrieves a message for the specified path/key from the language JSON file.
     *
     * @param path the key of the message in the JSON file.
     * @return the localized message as a {@link String}.
     * @throws RuntimeException if the language file cannot be read or parsed.
     */
    public String get(String path) {
        return getNewLangMap().get(path).toString();
    }

    /**
     * Retrieves a message for the specified path/key and performs a regex-based replacement.
     *
     * @param path        the key of the message in the JSON file.
     * @param regex       the regular expression to match in the message.
     * @param replacement the string to replace matches of the regex.
     * @return the formatted message as a {@link String}.
     */
    public String get(String path, String regex, String replacement) {
        return getNewLangMap().get(path).toString().replaceAll(regex, replacement);
    }

    /**
     * Retrieves a message for the specified path/key and performs multiple key-value replacements.
     *
     * @param path         the key of the message in the JSON file.
     * @param replacements a {@link HashMap} containing placeholders and their corresponding replacement values.
     * @return the formatted message as a {@link String}.
     */
    public String get(String path, HashMap<String, String> replacements) {
        return format(getNewLangMap().get(path).toString(), replacements);
    }

    /**
     * Reads the current language JSON file and returns its contents as a {@link Map}.
     *
     * @return a {@link Map} representing the key-value pairs from the JSON file.
     * @throws RuntimeException if the file cannot be read or parsed.
     */
    private Map<?, ?> getNewLangMap() {
        if (dir == null || lang == null) {
            throw new IllegalStateException("LanguageManager not set up correctly. dir=" + dir + " lang=" + lang);
        }

        File file = new File(dir + "/lang/" + lang + ".json");

        Reader reader;
        try {
            reader = Files.newBufferedReader(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read language file: " + file.getAbsolutePath(), e);
        }

        return new Gson().fromJson(reader, Map.class);
    }

    /**
     * Formats a message by replacing all occurrences of keys in the given replacements map
     * with their corresponding values.
     *
     * @param text         the original message text.
     * @param replacements a {@link HashMap} of keys and replacement values.
     * @return the formatted message with all replacements applied.
     */
    private String format(String text, HashMap<String, String> replacements) {
        String result = text;

        for (String key : replacements.keySet().toArray(new String[replacements.size()])) {
            result = result.replaceAll(key, replacements.get(key));
        }
        return result;
    }
}