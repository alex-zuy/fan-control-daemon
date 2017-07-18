package fan.control.daemon

import fan.control.daemon.FanControlApi.FanSpeed
import groovy.transform.CompileStatic

@CompileStatic
class Daemon {

    private FanControlApi fanControlApi;

    private SensorsApi sensorsApi;

    private SettingsProvider settingsProvider;

    Daemon(FanControlApi fanControlApi, SensorsApi sensorsApi, SettingsProvider settingsProvider) {
        this.fanControlApi = fanControlApi
        this.sensorsApi = sensorsApi
        this.settingsProvider = settingsProvider
    }

    void run() {
        def settingValues = settingsProvider.settingValues

        while (true) {
            def sensorsData = sensorsApi.pullSensorsData()
            def maxCoresTemperature = sensorsData.coreTemperatures.max()
            def currentFanSpeed = fanControlApi.pullFanSpeed()

            FanSpeed nextFanSpeed
            switch (currentFanSpeed) {
                case FanSpeed.HIGH && maxCoresTemperature < settingValues.highFanSpeedStopTemperature:
                    nextFanSpeed = FanSpeed.LOW
                    break
                case (FanSpeed.LOW || FanSpeed.STOPPED) && maxCoresTemperature > settingValues.highFanSpeedStartTemperature:
                    nextFanSpeed = FanSpeed.HIGH
                    break
                default:
                    fanControlApi.setFanSpeed(FanSpeed.HIGH)
                    throw new IllegalArgumentException("Unhandled value of fan speed: " + currentFanSpeed)
            }

            fanControlApi.setFanSpeed(nextFanSpeed)

            Thread.sleep(settingValues.checkIntervalMs)
        }
    }
}
