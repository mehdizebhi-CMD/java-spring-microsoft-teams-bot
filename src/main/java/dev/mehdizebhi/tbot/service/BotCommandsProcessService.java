package dev.mehdizebhi.tbot.service;

import dev.mehdizebhi.tbot.core.Command;
import dev.mehdizebhi.tbot.core.CommandHandler;
import dev.mehdizebhi.tbot.dto.NewChatMessageNotification;
import dev.mehdizebhi.tbot.util.Utilities;
import com.microsoft.graph.logger.DefaultLogger;
import com.microsoft.graph.models.ChangeNotification;
import com.microsoft.graph.models.ChatMessage;
import com.microsoft.graph.serializer.DefaultSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.*;

@Service
public class BotCommandsProcessService {

    private final List<CommandHandler> commandHandlers;
    private final CertificateStoreService certificateStore;
    private final Map<String, Method> commandMethods = new HashMap<>();

    @Autowired
    public BotCommandsProcessService(List<CommandHandler> commandHandlers, CertificateStoreService certificateStore) {
        this.commandHandlers = commandHandlers;
        this.certificateStore = certificateStore;
        initializeCommandMethods();
    }

    private void initializeCommandMethods() {
        for (CommandHandler handler : commandHandlers) {
            Class<?> handlerClass = handler.getClass();
            for (Method method : handlerClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Command.class)) {
                    Command commandAnnotation = method.getAnnotation(Command.class);
                    String commandValue = commandAnnotation.value();
                    commandMethods.put(commandValue, method);
                }
            }
        }
    }

    public void executeCommand(String command, String additionalText) {
        Method method = commandMethods.get(command);
        if (method != null) {
            try {
                for (CommandHandler handler : commandHandlers) {
                    int parameterCount = method.getParameterCount();
                    if (parameterCount == 0) {
                        method.invoke(handler);
                    } else if (parameterCount == 1) {
                        method.invoke(handler, additionalText);
                    }
                }
            } catch (Exception e) {
                // Handle exception appropriately
                e.printStackTrace();
            }
        }
    }

    /**
     * Processes a new channel message notification by decrypting the included resource data
     *
     * @param notification the new channel message notification
     */
    public Optional<NewChatMessageNotification> processNewChannelMessageNotification(
            @NonNull final ChangeNotification notification) {
        // Decrypt the encrypted key from the notification
        final var decryptedKey = Objects.requireNonNull(certificateStore
                .getEncryptionKey(Objects.requireNonNull(notification.encryptedContent).dataKey));

        // Validate the signature
        if (certificateStore.isDataSignatureValid(decryptedKey,
                Objects.requireNonNull(notification.encryptedContent).data,
                Objects.requireNonNull(notification.encryptedContent).dataSignature)) {
            // Decrypt the data using the decrypted key
            final var decryptedData = certificateStore.getDecryptedData(decryptedKey,
                    Objects.requireNonNull(notification.encryptedContent).data);

            // Deserialize the decrypted JSON into a ChatMessage
            final var serializer = new DefaultSerializer(new DefaultLogger());
            final var chatMessage = Objects.requireNonNull(serializer
                    .deserializeObject(Utilities.ensureNonNull(decryptedData), ChatMessage.class));

            return Optional.of(new NewChatMessageNotification(chatMessage));
        }
        return Optional.empty();
    }
}
