package fan.control.daemon

class ShellCommandInvoker {

    ShellCommandResult invokeCommand(String command) {
        def stdout = new StringBuilder()
        def stderr = new StringBuilder()

        def processBuilder = new ProcessBuilder()
        processBuilder.command("bash", "-c", command)
        def process = processBuilder.start()
        process.waitForProcessOutput(stdout, stderr)

        new ShellCommandResult(stdout: stdout.toString(), stderr: stderr.toString(), statusCode: process.exitValue())
    }
}
