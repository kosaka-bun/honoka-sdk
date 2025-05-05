package de.honoka.sdk.util.various;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RuntimeUtil;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class RuntimeUtils {

    public static class Commands {

        private final Map<SystemEnum, String[]> commands = new HashMap<>();

        private Charset charset = CharsetUtil.systemCharset();

        public Commands charset(Charset charset) {
            this.charset = charset;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Commands set(SystemEnum system, String... cmdParts) {
            commands.put(system, cmdParts);
            return this;
        }

        public Commands win(String... cmdParts) {
            set(SystemEnum.WINDOWS, cmdParts);
            return this;
        }

        public Commands linux(String... cmdParts) {
            set(SystemEnum.LINUX, cmdParts);
            return this;
        }

        public Commands mac(String... cmdParts) {
            set(SystemEnum.MACOS, cmdParts);
            return this;
        }

        public Commands other(String... cmdParts) {
            set(SystemEnum.OTHER, cmdParts);
            return this;
        }
    }

    public static String exec(Commands commands) {
        SystemEnum system = SystemEnum.getLocal();
        String[] cmdParts = commands.commands.get(system);
        if(cmdParts == null) {
            throw new RuntimeException("No suitable command.");
        }
        return RuntimeUtil.execForStr(commands.charset, commands.commands.get(system));
    }
}
