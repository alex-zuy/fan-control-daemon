package fan.control.daemon

import fan.control.daemon.FanControlApi.FanSpeed
import spock.lang.Specification
import spock.lang.Unroll

class DaemonTests extends Specification {

    private static final int HIGH_SPEED_START_TEMPERATURE = 45

    private static final int HIGH_SPEED_STOP_TEMPERATURE = 40

    private static final int LOW_TEMPERATURE = HIGH_SPEED_STOP_TEMPERATURE - 5

    private static final int MEDIUM_TEMPERATURE = (HIGH_SPEED_START_TEMPERATURE + HIGH_SPEED_STOP_TEMPERATURE).intdiv(2)

    private static final int HIGH_TEMPERATURE = HIGH_SPEED_START_TEMPERATURE + 5

    private FanControlApi fanControlApi = Mock()

    private SensorsApi sensorsApi = Mock()

    private SensorsData sensorsData = Mock()

    private SettingsProvider settingsProvider = Mock()

    private SettingValues settingValues = new SettingValues(50, HIGH_SPEED_START_TEMPERATURE, HIGH_SPEED_STOP_TEMPERATURE)

    private Daemon daemon

    def setup() {
        settingsProvider.getSettingValues() >> settingValues

        sensorsApi.pullSensorsData() >> sensorsData

        daemon = new Daemon(fanControlApi, sensorsApi, settingsProvider)
    }

    @Unroll
    def "fan speed transitions"() {
        given:
        givenFanSpeedIs currentSpeed
        givenCpuTemperatureIs cpuTemperature
        when:
        whenOneLoopRun()
        then:
        interaction {
            thenFanSpeedShouldBe(expectedNewSpeed)
        }

        where:
        currentSpeed  | cpuTemperature     || expectedNewSpeed
        FanSpeed.LOW  | LOW_TEMPERATURE    || FanSpeed.LOW
        FanSpeed.LOW  | MEDIUM_TEMPERATURE || FanSpeed.LOW
        FanSpeed.LOW  | HIGH_TEMPERATURE   || FanSpeed.HIGH
        FanSpeed.HIGH | HIGH_TEMPERATURE   || FanSpeed.HIGH
        FanSpeed.HIGH | MEDIUM_TEMPERATURE || FanSpeed.HIGH
        FanSpeed.HIGH | LOW_TEMPERATURE    || FanSpeed.LOW
    }

    private void givenFanSpeedIs(FanSpeed speed) {
        fanControlApi.pullFanSpeed() >> speed
    }

    private void givenCpuTemperatureIs(int temperature) {
        sensorsData.getCoreTemperatures() >> [temperature]
    }

    private void whenOneLoopRun() {
        def thread = new Thread(daemon.&run)
        thread.start()
        Thread.sleep(25)
        thread.interrupt()
    }

    private void thenFanSpeedShouldBe(FanSpeed speed) {
        1 * fanControlApi.setFanSpeed(speed)
    }
}
