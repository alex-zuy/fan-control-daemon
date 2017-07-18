package fan.control.daemon

import spock.lang.Specification

class FanControlApiTests extends Specification {

    private ShellCommandInvoker invoker = Mock()

    private ShellCommandResult commandResult = Mock()

    private FanControlApi fanControlApi

    def setup() {
        fanControlApi = new FanControlApi(invoker)
    }

    def "pullFanSpeed: should return fan speed if single fan present"() {
        given:
        givenI8KfanCommandOutputIs("-1 1")
        when:
        def fanSpeed = fanControlApi.pullFanSpeed()
        then:
        fanSpeed == FanControlApi.FanSpeed.LOW
    }

    def "pullFanSpeed: should return minimal of speeds if two fans present"() {
        given:
        givenI8KfanCommandOutputIs("2 1")
        when:
        def fanSpeed = fanControlApi.pullFanSpeed()
        then:
        fanSpeed == FanControlApi.FanSpeed.LOW
    }

    def "pullFanSpeed: should handle fan idle state correctly"() {
        given:
        givenI8KfanCommandOutputIs("0 0")
        when:
        def fanSpeed = fanControlApi.pullFanSpeed()
        then:
        fanSpeed == FanControlApi.FanSpeed.STOPPED
    }

    def "setFanSpeed: should execute fan speed update command"() {
        when:
        fanControlApi.setFanSpeed(FanControlApi.FanSpeed.LOW)
        then:
        1 * invoker.invokeCommandEnsuringSuccess("i8kfan 1 1")
    }

    def givenI8KfanCommandOutputIs(String output) {
        invoker.invokeCommandEnsuringSuccess("i8kfan") >> commandResult
        commandResult.getStdout() >> output
    }
}
