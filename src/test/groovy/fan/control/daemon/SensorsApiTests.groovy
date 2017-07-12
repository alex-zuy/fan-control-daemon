package fan.control.daemon

import spock.lang.Specification

class SensorsApiTests extends Specification {

    private ShellCommandInvoker commandInvoker = Mock()

    private ShellCommandResult commandResult = Mock()

    private SensorsApi sensorsApi

    def setup() {
        sensorsApi = new SensorsApi(commandInvoker)
    }

    def "should parse cores temperatures and return sensors data"() {
        given:
        givenSensorsCommandOutputIs("sensors-u-output.txt")
        when:
        def sensorsData = sensorsApi.pullSensorsData()
        then:
        with(sensorsData) {
            coreTemperatures == [52, 48]
        }
    }

    private def givenSensorsCommandOutputIs(String outputSnapshotFileName) {
        def snapshotOutput = getClass().getResource(outputSnapshotFileName).text
        commandInvoker.invokeCommand("sensors -u") >> commandResult
        commandResult.getStdout() >> snapshotOutput
    }
}
