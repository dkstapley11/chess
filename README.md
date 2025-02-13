# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

[Sequence Diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=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-YlHKEAVI2QM-Wa0eiEMOSgkhsqHFnO+gwy6tc1xAA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
