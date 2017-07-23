package fan.control.daemon

import groovy.transform.CompileStatic

@CompileStatic
class SettingsProvider {

    private static class SettingNames {

        static String CHECK_INTERVAL_MS = 'checkIntervalMs'

        static String HIGH_FAN_SPEED_START_TEMPERATURE = 'highFanSpeedStartTemperature'

        static String HIGH_FAN_SPED_STOP_TEMPERATURE = 'highFanSpeedStopTemperature'
    }

    private static Map<String, String> DEFAULT_SETTINGS = [
            (SettingNames.CHECK_INTERVAL_MS)               : "2000",
            (SettingNames.HIGH_FAN_SPEED_START_TEMPERATURE): "65",
            (SettingNames.HIGH_FAN_SPED_STOP_TEMPERATURE)  : "55"
    ].asImmutable()

    private SettingValues settingValues;

    SettingsProvider(Properties properties) {
        Map<String, String> props = properties.collectEntries {
            [it.key.toString(), it.value.toString()]
        } as Map<String, String>
        settingValues = buildSettingValues(DEFAULT_SETTINGS + props)
    }

    SettingsProvider() {
        settingValues = buildSettingValues(DEFAULT_SETTINGS)
    }

    SettingValues getSettingValues() {
        return settingValues
    }

    private static SettingValues buildSettingValues(Map<String, String> settings) {
        return new SettingValues(
                checkIntervalMs: Integer.parseInt(settings[SettingNames.CHECK_INTERVAL_MS]),
                highFanSpeedStartTemperature: Integer.parseInt(settings[SettingNames.HIGH_FAN_SPEED_START_TEMPERATURE]),
                highFanSpeedStopTemperature: Integer.parseInt(settings[SettingNames.HIGH_FAN_SPED_STOP_TEMPERATURE])
        )
    }
}
