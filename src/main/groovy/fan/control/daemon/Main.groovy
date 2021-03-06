package fan.control.daemon

class Main {

    static void main(String... args) {
        def settings = resolveSettings()

        def settingsProvider = new SettingsProvider(settings)
        def shellCommandInvoker = new ShellCommandInvoker()
        def fanControlApi = new FanControlApi(shellCommandInvoker)
        def sensorsApi = new SensorsApi(shellCommandInvoker)

        def daemon = new Daemon(fanControlApi, sensorsApi, settingsProvider)

        // for some reason i8kfan utility may report invalid values for fan speeds at startup (e.g. fan speed 3)
        // to avoid crashed due to this behavior we set fan speed to some valid value
        fanControlApi.setFanSpeed(FanControlApi.FanSpeed.HIGH)

        daemon.run()
    }

    private static Properties resolveSettings() {
        def settingsProperties = new Properties()
        def settingsPath = System.getenv("FAN_CONTROL_CONFIG_PATH")
        if (settingsPath) {
            def file = new File(settingsPath)
            if (file.exists()) {
                file.withInputStream { settingsProperties.load(it) }
            }
        }
        settingsProperties
    }
}
