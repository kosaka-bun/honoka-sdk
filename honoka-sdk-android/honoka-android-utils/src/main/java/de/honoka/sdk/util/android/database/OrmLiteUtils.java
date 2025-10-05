package de.honoka.sdk.util.android.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.SneakyThrows;

import java.util.Collection;

public class OrmLiteUtils {

    @SneakyThrows
    public static int dropTable(ConnectionSource connectionSource, Class<?> dataClass, boolean ignoreErrors) {
        return TableUtils.dropTable(connectionSource, dataClass, ignoreErrors);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public static void insertCollection(Dao<?, ?> dao, Collection<?> collection) {
        ((Dao<Object, Object>) dao).create((Collection<Object>) collection);
    }
}
