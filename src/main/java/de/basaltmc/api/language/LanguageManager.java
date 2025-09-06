package de.basaltmc.api.language;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The {@code LanguageManager} class provides functionality to manage application language files
 * and handle language switching at runtime. It allows for language extraction from packaged JAR files
 * and optional end-user modifications to language files.
 *
 * <p>
 * Core features include:
 * <ul>
 *     <li>Setting up language directories and language identifiers.</li>
 *     <li>Allowing or disallowing end-user modification of extracted language files.</li>
 *     <li>Dynamically changing the active language during runtime.</li>
 *     <li>Extracting language resources from a given JAR file.</li>
 * </ul>
 *
 * <p><b>Usage Scenarios:</b></p>
 * <ol>
 *     <li>Applications supporting multiple languages and requiring a centralized way to manage
 *     and switch between them.</li>
 *     <li>Games or tools where translation files (e.g., JSON, XML, or properties) are bundled inside JAR files.</li>
 *     <li>Environments where developers may or may not want end-users to modify localization files.</li>
 * </ol>
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * // Set the language manager up with english selected
 * LanguageManager manager = new LanguageManager();
 * manager.setup(true, "resources/lang", "en", "[LangManager]");
 *
 * // Change language dynamically
 * manager.changeLanguage("de);
 *
 * // Extract languages from a packaged jar
 * LanguageManager.extractLanguages("resources/", "myApp");
 *
 * // Get current settings
 * System.out.println("Current language: " + manager.getLang());
 * }</pre>
 */
public class LanguageManager {
    private static boolean allowEndUserModification;
    private String dir;
    private String lang;
    private boolean isLanguageSet;

    /**
     * Constructs an uninitialized {@code LanguageManager}.
     * <p>
     * Use one of the {@link #setup(boolean, String, String)} or
     * {@link #setup(boolean, String, String)} methods to configure it.
     */
    public LanguageManager() {
    }

    /**
     * Sets up the language manager.
     *
     * @param allowEndUserLanguageModification whether end-users are allowed to modify extracted language files.
     * @param directory                        the base directory where language files will be stored.
     * @param language                         the initial language to set (e.g., "en").
     */
    public void setup(boolean allowEndUserLanguageModification, String directory, String language) {
        allowEndUserModification = allowEndUserLanguageModification;
        dir = directory;
        lang = language;
    }

    /**
     * Attempts to set the language only if no language has been previously set.
     *
     * @param language the new language identifier to apply.
     * @return {@code true} if the language was successfully set, {@code false} if a language was already set.
     */
    private boolean setLanguage(String language) {
        if (!isLanguageSet) {
            lang = language;
            isLanguageSet = true;
            return true;
        }
        return false;
    }

    /**
     * Changes the current language, overriding any previously set language.
     *
     * @param language the new language identifier to apply (e.g., "de").
     */
    public void changeLanguage(String language) {
        isLanguageSet = false;
        setLanguage(language);
    }

    /**
     * Extracts all language files from a specified JAR into a target directory.
     * <p>
     * Files are copied to the base directory. Depending on the configuration,
     * existing files may be replaced or preserved:
     * <ul>
     *     <li>If {@code allowEndUserModification = true}, files are copied only if they do not exist.</li>
     *     <li>If {@code allowEndUserModification = false}, files are replaced if they already exist.</li>
     * </ul>
     *
     * @param path    the path to the directory where extracted files will be placed.
     * @param jarName the name of the JAR file (without ".jar" extension).
     * @throws RuntimeException if the JAR cannot be opened or files cannot be extracted.
     */
    private static void extractLanguages(String path, String jarName) {
        File dir = new File(path);

        try (ZipFile zipFile = new ZipFile(path + jarName + ".jar")) {
            for (ZipEntry entry : Collections.list(zipFile.entries())) {
                if (entry.getName().startsWith("lang/") && !entry.isDirectory()) {
                    File target = new File(dir, entry.getName());
                    target.getParentFile().mkdirs();

                    try (InputStream inputStream = zipFile.getInputStream(entry)) {
                        if (allowEndUserModification) {
                            Files.copy(inputStream, target.toPath());
                        } else {
                            Files.copy(inputStream, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the directory where language files are stored.
     *
     * @return the language directory path.
     */
    public String getDir() {
        return dir;
    }

    /**
     * Gets the currently active language identifier.
     *
     * @return the active language string (e.g., "en_US").
     */
    public String getLang() {
        return lang;
    }
}
