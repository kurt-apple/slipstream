package slipstream.untidy.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class UI extends slipstream.tidy.utils.UI {
    /*
    appendDoneList(String message)
    logs completed tasks to a file
    note: INCOMPLETE. Can currently only do hardcoded URLs. Ideally, it would store in the task database.
     */
    //TODO: finish TaskListGenerator TODOs
    //TODO: pass TaskListGenerator tests
    //TODO: consider placing done list at bottom of tdl file
    public static void appendDoneList(String message) {
        try {
            final Path path = Paths.get("C:\\slipstreamlog\\done.list");
            Files.write(path, Arrays.asList("COMPLETED: " + message), StandardCharsets.UTF_8, Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        } catch (final IOException ioe) {
            ioe.printStackTrace(System.err);
        }
    }
}
