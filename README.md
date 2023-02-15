# ChatGPT on Messenger - SpringBoot

This project is an application for a Facebook Messenger OpenAI Bot using Java, Spring Boot, and messenger4j.
You can use it to quickly bootstrap your ChatBot projects and write your first Messenger ChatBot within minutes.

It's a pre-configured Maven project containing a sample chatbot application and all required dependencies.

The sample application is a port of the official Messenger Platform [NodeJS Showcase][1].
For information on how to setup your chatbot you can follow along with the [Setup Guide][2].

## Getting Started

### Prerequisites
* Git
* JDK 8 or later
* Maven 3.0 or later

### Configuration
In order to get your chatbot working you have to provide the following settings:
```
messenger4j.appSecret = ${MESSENGER_APP_SECRET}
messenger4j.verifyToken = ${MESSENGER_VERIFY_TOKEN}
messenger4j.pageAccessToken = ${MESSENGER_PAGE_ACCESS_TOKEN}
```
The configuration is located in `src/resources/application.properties`.

With the default configuration you can provide these values through the environment variables `MESSENGER_APP_SECRET`, `MESSENGER_VERIFY_TOKEN`,
and `MESSENGER_PAGE_ACCESS_TOKEN`.

### Build an executable JAR
You can run the application from the command line using:
```
mvn spring-boot:run
```
Or you can build a single executable JAR file that contains all the necessary dependencies, classes, and resources with:
```
mvn clean package
```
Then you can run the JAR file with:
```
java -jar target/*.jar
```

*Instead of `mvn` you can also use the maven-wrapper `./mvnw` to ensure you have everything necessary to run the Maven build.*

### Create and Configure Facebook App
1. enter the generated Verify Token, e.g. `retgdkfjsjklsklj34qdfs`
2. select the following Subscription Fields: `messages`, `messaging_postbacks`, `messaging_optins`, `message_deliveries`, `message_reads`, `messaging_account_linking`, `message_echoes`
3. click the 'Verify and Save' button
4. Section 'Webhooks': Select your created FB Page to subscribe your webhook to the page events, e.g. `Messenger4j Demo`
5. click the 'Subscribe' button

### Test your new Chatbot
1. open `https://www.messenger.com`
2. search for your Chatbot using the name of your created FB Page, e.g. `The Bear`
3. send a message, e.g. `Hello Chatbot`. 

## License
This project is licensed under the terms of the [MIT license](LICENSE).

[1]: https://github.com/fbsamples/messenger-platform-samples
[2]: https://developers.facebook.com/docs/messenger-platform/guides/setup