package fan.control.daemon

import groovy.transform.CompileStatic

@CompileStatic
class ShellCommandResult {

    int statusCode

    String stdout

    String stderr
}
