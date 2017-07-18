package fan.control.daemon

import groovy.transform.CompileStatic

@CompileStatic
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

    ShellCommandResult invokeCommandEnsuringSuccess(String command) {
        def result = invokeCommand(command)
        if (result.statusCode != 0) {
            throw new RuntimeException("""
                |Command execution failed!
                |Command: '$command'
                |Status code: $result.statusCode
                |Error output: $result.stderr
                |Standard output: $result.stdout
            """.stripMargin())
        } else {
            result
        }
    }
}
