package fan.control.daemon

import groovy.transform.CompileStatic

import java.util.regex.Pattern

@CompileStatic
class FanControlApi {

    private static final Pattern PATTERN_WHITESPACE = ~/\s+/

    private static final int COOLER_VALUE_INVALID = -1

    private static final String COMMAND_I8KFAN = 'i8kfan';

    enum FanSpeed {

        STOPPED(0),

        LOW(1),

        HIGH(2)

        private int speedValue;

        FanSpeed(int speedValue) {
            this.speedValue = speedValue
        }

        int getSpeedValue() {
            return speedValue
        }

        private static Optional<FanSpeed> valueForSpeedValue(int speed) {
            Arrays.stream(values())
                    .filter { it.speedValue == speed }
                    .findFirst()
        }
    }

    private final ShellCommandInvoker commandInvoker

    FanControlApi(ShellCommandInvoker commandInvoker) {
        this.commandInvoker = commandInvoker
    }

    void setFanSpeed(FanSpeed speed) {
        commandInvoker.invokeCommandEnsuringSuccess("${COMMAND_I8KFAN} ${speed.speedValue} ${speed.speedValue}")
    }

    FanSpeed pullFanSpeed() {
        def commandResult = commandInvoker.invokeCommandEnsuringSuccess(COMMAND_I8KFAN)
        def allCoolerValues = PATTERN_WHITESPACE.split(commandResult.stdout).findAll().collect(Integer.&parseInt)
        def validCoolerValues = allCoolerValues - COOLER_VALUE_INVALID
        int coolerValue = validCoolerValues.min()
        FanSpeed.valueForSpeedValue(coolerValue)
                .orElseThrow {
            new IllegalStateException("No ${FanSpeed.class.getTypeName()} value exist for speed value $coolerValue")
        }
    }
}
