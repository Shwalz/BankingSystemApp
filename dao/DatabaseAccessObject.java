package dao;

import models.Account;

import java.io.IOException;
import java.util.List;

abstract class DatabaseAccessObject<T> {

    public abstract List<T> load() throws IOException;

    public abstract void save(T t) throws IOException;

    public abstract T parseFromLine(String line);

    public abstract String convertToLine(T t);
}
