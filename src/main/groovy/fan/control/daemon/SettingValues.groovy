package fan.control.daemon

import groovy.transform.CompileStatic
import groovy.transform.Immutable

@CompileStatic
@Immutable
class SettingValues {

    int checkIntervalMs

    int highFanSpeedStartTemperature

    int highFanSpeedStopTemperature
}
