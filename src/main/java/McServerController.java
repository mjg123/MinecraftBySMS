import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.stream.Stream;

import static spark.Spark.get;
import static spark.Spark.post;

public class McServerController {

    private static final Logger log = LoggerFactory.getLogger(McServerController.class);

    public static void main(String[] args) throws IOException {
        log.info("Starting MC server...");

        var mcServerController = new McServerController();
        Process mcProcess = mcServerController.createMcProcess();
        var commandSender = new CommandSender(mcProcess);

        addSafeShutdownHook(mcProcess, commandSender);

        // for testing from command line
        get("/mc", (req, res) -> {
            var msg = req.queryParams("msg");
            commandSender.sendMessage(msg);
            return "thanks";
        });


        // for handling webhook requests from Twilio
        post("/mc", (req, res) -> {
            var msg = req.queryParams("Body");

            // lowercase the first word here (MC commands are all lowercase but your phone
            //   probably wants to capitalise the first word of a sentence).
            var lcMsg = msg.substring(0, 1).toLowerCase() + msg.substring(1);
            commandSender.sendMessage(lcMsg);
            return "thanks";
        });


    }

    private static void addSafeShutdownHook(Process mcProcess, CommandSender commandSender) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown MC server");

            try {
                commandSender.sendMessage("stop");

                int tries = 30;
                while (mcProcess.isAlive() && tries-- > 0) {
                    Thread.sleep(1_000);
                }

            } catch (Exception e) {
                // shrug
            }

            mcProcess.destroy();
        }));
    }

    private static class CommandSender {

        private final Process mcProcess;
        private final OutputStreamWriter mcWriter;

        public CommandSender(Process mcProcess) {
            this.mcProcess = mcProcess;
            var mcStdIn = mcProcess.getOutputStream();
            mcWriter = new OutputStreamWriter(mcStdIn);
        }

        public void sendMessage(String msg) throws IOException {
            log.info("Sending command to MC server: {}", msg);
            mcWriter.write(msg + "\n");
            mcWriter.flush();
        }
    }

    private Process createMcProcess() throws IOException {
        var mcProcessBuilder = new ProcessBuilder(
            "java", "-Xmx4096M", "-Xms4096M",
            "-jar", "server.jar", "nogui");

        mcProcessBuilder.directory(new File("/home/mjg/tmp/mc-server"));

        var mcProcess = mcProcessBuilder.start();

        var mcStdOut = mcProcess.getInputStream();

        new Thread(() -> {
            Stream<String> lines = new BufferedReader(new InputStreamReader(mcStdOut)).lines();
            lines.forEach(l -> System.out.println(l));
        }).start();

        return mcProcess;
    }

}
