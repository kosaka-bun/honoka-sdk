package de.honoka.sdk.util.various;

import cn.hutool.core.util.RuntimeUtil;

public class RuntimeUtils {

    public static class Command {

        private SystemEnum system;

        private String[] cmdParts;

        private Command() {}

        public static Command of(SystemEnum system, String... cmdParts) {
            Command command = new Command();
            command.system = system;
            command.cmdParts = cmdParts;
            return command;
        }

        public static Command win(String... cmdParts) {
            return of(SystemEnum.WINDOWS, cmdParts);
        }

        public static Command linux(String... cmdParts) {
            return of(SystemEnum.LINUX, cmdParts);
        }

        public static Command mac(String... cmdParts) {
            return of(SystemEnum.MACOS, cmdParts);
        }

        public static Command other(String... cmdParts) {
            return of(SystemEnum.OTHER, cmdParts);
        }
    }

    public static String exec(Command... commands) {
        SystemEnum system = SystemEnum.getLocal();
        for(Command command : commands) {
            if(!command.system.equals(system)) continue;
            return RuntimeUtil.execForStr(command.cmdParts);
        }
        throw new RuntimeException("No suitable command.");
    }
}
