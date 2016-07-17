package pushbullet;

import logbook.internal.Config;

import java.util.function.Supplier;

/**
 * クラスローダーの違いでClassNotFoundExceptionが発生するのを防止するためのユーティリティ
 */
public class ConfigLoader {
    public static <T> T load(Class<T> clazz, Supplier<T> def) {
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader newLoader = clazz.getClassLoader();
        Thread.currentThread().setContextClassLoader(newLoader);
        try {
            return Config.getDefault().get(clazz, def);
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
    }
}
