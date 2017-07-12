package fan.control.daemon

import java.util.regex.Pattern

class SensorsApi {

    private static class Patterns {

        static final String VALUE = '[0-9.]'

        static final String IDENTIFIER = '[a-zA-Z0-9_-]'

        static final String NAME = "(?:${IDENTIFIER}|\\s)"
    }

    private static class SensorMatching {

        static final String GROUP_NAME = 'sensorName'

        static final Pattern REGEX = ~$/^(?<${GROUP_NAME}>${Patterns.NAME}+):$$/$
    }

    private static class CharacteristicMatching {

        static final String GROUP_NAME = 'characteristicName'

        static final String GROUP_VALUE = 'characteristicValue'

        static final Pattern REGEX = ~$/^
            |\s+(?<${GROUP_NAME}>${Patterns.IDENTIFIER}+):
            |\s+(?<${GROUP_VALUE}>${Patterns.VALUE}+)
        |$$/$.stripMargin().replaceAll('\n', '')
    }

    private static class CoreMatching {

        static final Pattern REGEX = ~/Core \d+/
    }

    private static final String COMMAND_SENSORS = "sensors -u"

    private final ShellCommandInvoker commandInvoker

    SensorsApi(ShellCommandInvoker commandInvoker) {
        this.commandInvoker = commandInvoker
    }

    SensorsData pullSensorsData() {
        def commandResult = commandInvoker.invokeCommand(COMMAND_SENSORS)

        def commandOutputLines = commandResult.stdout.readLines()
        def coreTemperatures = []
        def lineIdx = 0;

        while (lineIdx < commandOutputLines.size()) {
            def line = commandOutputLines[lineIdx]
            if (isMatchesCoreSensor(line)) {
                Map<String, Float> sensorCharacteristics = matchSensorCharacteristics(commandOutputLines.drop(lineIdx + 1))
                def temperature = sensorCharacteristics.find { it.key.endsWith('_input') }?.value
                if (temperature) {
                    coreTemperatures << temperature
                }
                lineIdx += sensorCharacteristics.size()
            } else {
                ++lineIdx
            }
        }

        new SensorsData(coreTemperatures: coreTemperatures)
    }

    private static boolean isMatchesCoreSensor(String line) {
        def matcher = SensorMatching.REGEX.matcher(line)
        if (matcher.matches()) {
            def sensorName = matcher.group(SensorMatching.GROUP_NAME)
            def coreMatcher = CoreMatching.REGEX.matcher(sensorName)
            coreMatcher.matches()
        } else {
            false
        }
    }

    private static Map<String, Float> matchSensorCharacteristics(List<String> unprocessedLines) {
        unprocessedLines.collect { CharacteristicMatching.REGEX.matcher(it) }
                .takeWhile { it.matches() }
                .collectEntries { matcher ->
            def name = matcher.group(CharacteristicMatching.GROUP_NAME)
            def value = Float.parseFloat(matcher.group(CharacteristicMatching.GROUP_VALUE))
            [(name): value]
        }
    }
}
