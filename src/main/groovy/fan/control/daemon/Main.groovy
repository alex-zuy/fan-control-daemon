package fan.control.daemon

class Main {

    static void main(String... args) {
        def settings = resolveSettings()

        def settingsProvider = new SettingsProvider(settings)
        def shellCommandInvoker = new ShellCommandInvoker()
        def fanControlApi = new FanControlApi(shellCommandInvoker)
        def sensorsApi = new SensorsApi(shellCommandInvoker)

        def daemon = new Daemon(fanControlApi, sensorsApi, settingsProvider)
        daemon.run()
    }

    private static Properties resolveSettings() {
        def settingsPath = System.getenv("FAN_CONTROL_CONFIG_PATH")
        def settings = new Properties()
        if (settingsPath) {
            new File(settingsPath).withInputStream { settings.load(it) }
        }
        settings
    }
}
