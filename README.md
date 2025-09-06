# Language API

The **Language API** is a Java-based library designed to manage multilingual support for applications. It allows developers to load, switch, and retrieve localized messages dynamically at runtime. The API is flexible, allowing developers to define the format of language files (JSON, XML, YAML, or any custom format that can be parsed into key-value pairs).

---

## Features

- **Dynamic Language Management**: Set and switch application languages at runtime.
- **Flexible Language Files**: Support for any format that can be read into a key-value map (default example uses JSON).
- **End-User Modification Support**: Optionally allow end-users to modify language files without overwriting.
- **Automatic Extraction**: Extract language files from a packaged JAR.
- **Logging**: Optional logging of language operations with customizable prefixes.
- **Message Formatting**: Supports regex replacement and multiple key-value replacements for dynamic messages.

---

## Installation

Include the classes `LanguageManager.java` and `Message.java` in your project. Ensure that your project includes [Gson](https://github.com/google/gson) (or any JSON parser if you prefer) for parsing language files:

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>
```

## Usage
### Setting up LanguageManager
```java
LanguageManager langManager = new LanguageManager();
langManager.setup(false, "resources/lang", "en_US");
```
### Changing Language at Runtime
```java
// Switch to English
langManager.changeLanguage("en_US");

// Switch to German
langManager.changeLanguage("german");
```

## Retrieving Messages
### Using a Message object with LanguageManager
```java
Message message = new Message(langManager);

// Simple retrieval
String welcome = message.get("welcome.message");

// Singular replacement
String customMessage = message.get("welcome.message", "%username%", "Alice");

// Multiple replacements
HashMap<String, String> replacements = new HashMap<>();
replacements.put("%username%", "TheBurgCrafter");
replacements.put("%errorID%", "418");
String formattedMessage = message.get("errors.last", replacements);

System.out.println(formattedMessage);
```
### Using explicit language and directory
```java
Message message = new Message("fr_FR", "resources/lang");

String goodbye = message.get("goodbye.message");
```

## Language File Format
### The Language API is format-agnostic. You can choose any format as long as it can be converted into a key-value map.
#### JSON (default example):
```json
{
    "welcome.message": "Welcome, %username%!",
    "goodbye.message": "Goodbye!"
}
```
## Example Project Structure
```css
resources/
  lang/
    en_US.json
    fr_FR.json
src/
  main/
    java/
      LanguageManager.java
      Message.java
```
## Notes
  - If **allowEndUserModification** is true, **language files will not be overwritten** if they already exist when extracting from the JAR.
  - If **allowEndUserModification** is false, **language files will be replaced** during extraction.
  - The API is designed to be **simple and flexible**, giving developers **full control** over language management.

## Contributing
Contributions are welcome! Feel free to submit issues, suggest improvements, or extend the library to support additional file formats.
