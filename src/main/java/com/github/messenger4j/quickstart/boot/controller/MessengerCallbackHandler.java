package com.github.messenger4j.quickstart.boot.controller;

import static com.github.messenger4j.Messenger.CHALLENGE_REQUEST_PARAM_NAME;
import static com.github.messenger4j.Messenger.MODE_REQUEST_PARAM_NAME;
import static com.github.messenger4j.Messenger.SIGNATURE_HEADER_NAME;
import static com.github.messenger4j.Messenger.VERIFY_TOKEN_REQUEST_PARAM_NAME;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import com.github.messenger4j.Messenger;
import com.github.messenger4j.exception.MessengerVerificationException;
import com.github.messenger4j.quickstart.boot.dto.User;
import com.github.messenger4j.quickstart.boot.repo.MessageMap;
import com.github.messenger4j.quickstart.boot.service.ChatGPTService;
import com.github.messenger4j.send.MessagePayload;
import com.github.messenger4j.send.MessagingType;
import com.github.messenger4j.send.NotificationType;
import com.github.messenger4j.send.message.TextMessage;
import com.github.messenger4j.send.recipient.IdRecipient;
import com.github.messenger4j.webhook.event.TextMessageEvent;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is the main class for inbound and outbound communication with the Facebook Messenger Platform. The callback
 * handler is responsible for the webhook verification and processing of the inbound messages and events. It showcases
 * the features of the Messenger Platform.
 *
 * @author Max Grabenhorst
 */
@RestController
@RequestMapping("/callback")
public class MessengerCallbackHandler {
    ChatGPTService chatGPT;

    MessageMap messageMap;

    Messenger messenger;

    private static final String RESOURCE_URL = "https://raw.githubusercontent.com/fbsamples/messenger-platform-samples/master/node/public";

    private static final Logger logger = LoggerFactory.getLogger(MessengerCallbackHandler.class);

    public MessengerCallbackHandler(final Messenger messenger, ChatGPTService chatGPT, MessageMap messageMap) {
        this.messenger = messenger;
        this.chatGPT = chatGPT;
        this.messageMap = messageMap;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/health")
    public ResponseEntity<String> healthCheck(){
        return ResponseEntity.ok("Health ok: ");
    }

    @RequestMapping(method = RequestMethod.GET, path = "/gpt", params = "message")
    public ResponseEntity<String> testGPT(@RequestParam("message") String message) {
        try {
            return ResponseEntity.ok(chatGPT.sendMessage(message));
        } catch (Exception e) {
            return ResponseEntity.ok("There is an error");
        }
    }

    /**
     * Webhook verification endpoint. <p> The passed verification token (as query parameter) must match the configured
     * verification token. In case this is true, the passed challenge string must be returned by this endpoint.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> verifyWebhook(@RequestParam(MODE_REQUEST_PARAM_NAME) final String mode,
                                                @RequestParam(VERIFY_TOKEN_REQUEST_PARAM_NAME) final String verifyToken, @RequestParam(CHALLENGE_REQUEST_PARAM_NAME) final String challenge) {
        logger.info("Received Webhook verification request - mode: {} | verifyToken: {} | challenge: {}", mode, verifyToken, challenge);
        try {
            this.messenger.verifyWebhook(mode, verifyToken);
            return ResponseEntity.ok(challenge);
        } catch (MessengerVerificationException e) {
            logger.warn("Webhook verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    /**
     * Callback endpoint responsible for processing the inbound messages and events.
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> handleCallback(@RequestBody final String payload, @RequestHeader(SIGNATURE_HEADER_NAME) final String signature) {
        logger.info("Received Messenger Platform callback - payload: {} | signature: {}", payload, signature);
        try {
            TextMessageEvent[] newEvent = new TextMessageEvent[1];
            this.messenger.onReceiveEvents(payload, of(signature), event -> {
                if (event.isTextMessageEvent()) {
                    newEvent[0] = event.asTextMessageEvent();
                }
            });
            handleTextMessageEvent(newEvent[0]);
            logger.info("Processed callback payload successfully");
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (MessengerVerificationException e) {
            logger.warn("Processing of callback payload failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            logger.error("Error happened in handleCallback: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.OK).build();
        }
    }

    private void handleTextMessageEvent(TextMessageEvent event) throws Exception {
        logger.info("Received TextMessageEvent: {}", event);

        final String messageId = event.messageId();
        final String messageText = event.text();
        final String senderId = event.senderId();
        final Instant timestamp = event.timestamp();

        logger.info("Received message '{}' with text '{}' from user '{}' at '{}'", messageId, messageText, senderId, timestamp);
        String previousMessage = messageMap.getMessage(senderId); // included the intro
        String nowMessage = "\n"+ User.USER_NAME_CONST  + messageText + "\n" + User.BOT_NAME_CONST;
        String gptRes = chatGPT.sendMessage(previousMessage + nowMessage);
        sendTextMessage(senderId, gptRes);
        messageMap.saveMessage(senderId, nowMessage + gptRes);
    }

    private void sendTextMessage(String recipientId, String text) throws Exception{
            final IdRecipient recipient = IdRecipient.create(recipientId);
            final NotificationType notificationType = NotificationType.REGULAR;
            final String metadata = "DEVELOPER_DEFINED_METADATA";

            final TextMessage textMessage = TextMessage.create(text, empty(), of(metadata));
            final MessagePayload messagePayload = MessagePayload.create(recipient, MessagingType.RESPONSE, textMessage,
                    of(notificationType), empty());
            this.messenger.send(messagePayload);
    }

    private void handleSendException(Exception e) {
        logger.error("Message could not be sent. An unexpected error occurred.", e);
    }
}
