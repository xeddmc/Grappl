package io.grappl.client.impl.stable.commands;

import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;
import io.grappl.client.impl.Application;
import io.grappl.client.impl.stable.commands.impl.*;
import io.grappl.client.impl.stable.commands.impl.dummy.DummyServerCommand;

import java.util.*;

public class CommandHandler {

    private ApplicationState state;

    private boolean commandThreadStarted = false;

    private Map<String, Command> commandMap = new HashMap<String, Command>();
    private PriorityQueue<String> commandsAlphabetized = new PriorityQueue<String>();

    @SuppressWarnings("SpellCheckingInspection")
    public CommandHandler(ApplicationState state) {
        this.state = state;

        /* Add default commands */
        addCommand(new HelpCommand(), "help");
        addCommand(new VersionCommand(), "version");
        addCommand(new ClearCommand(), "clear");
        addCommand(new SaveLogCommand(), "savelog");
        addCommand(new QuitCommand(), "quit");

        /* Account related */
        addCommand(new AccountCommand(), "account");
        addCommand(new RegisterCommand(), "register");
        addCommand(new LoginCommand(), "login");
        addCommand(new LogoutCommand(), "logout");
        addCommand(new WhoAmICommand(), "whoami");
        // TODO: put as subcommand to account, maybe?
        addCommand(new SetStaticPortCommand(), "setstaticport", "setport");

        addCommand(new GrapplCommand(), "grappl");

        addCommand(new DummyServerCommand(), "dummy");
        addCommand(new DirectoryCommand(), "directory");

        addCommand(new MultiTestCommand(), "multitest");
    }

    public void addCommand(Command command, String... names) {

        for(String name : names) {
            commandMap.put(name, command);
            commandsAlphabetized.add(name);
        }
    }

    public void handleCommand(String command) {
        // If no command was actually entered, return.
        if(command.isEmpty())
            return;

        // Split command string into words. args[0] is the commands name, all other are args.
        String[] words = command.split("\\s+");
        String commandName = words[0].toLowerCase();

//        String[] args = Arrays.copyOfRange(words, 1, words.length);

        if (commandMap.containsKey(commandName)) {
            getCommand(commandName).runCommand(state, words);
        } else {
            Application.getLog().log("Unknown command '" + command + "'");
        }
    }

    public void createConsoleCommandListenThread() {

        if(!commandThreadStarted) {
            Thread commandThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    Application.getLog().log(Application.APP_NAME + " Command Line (type a command, or type 'help' or 'help [command]' for ideas!)");

                    Scanner scanner = new Scanner(System.in);

                    while (scanner.hasNextLine()) {
                        try {
                            String line = scanner.nextLine();
                            String[] args = line.split("\\s+");

                            String commandName = args[0].toLowerCase();

                            if (commandMap.containsKey(commandName)) {
                                getCommand(commandName).runCommand(state, args);
                            } else {
                                Application.getLog().log("Unknown command '" + commandName + "'");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            commandThread.setName("Grappl Console Command Thread");
            commandThread.start();
            commandThreadStarted = true;
        }
    }

    public boolean hasCommand(String name) {
        return commandMap.containsKey(name);
    }

    public Command getCommand(String name) {
        return commandMap.get(name);
    }

    public PriorityQueue<String> getCommandsAlphabetized() {
        return commandsAlphabetized;
    }
}
