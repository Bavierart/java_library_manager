package controller;

import java.util.List;

public interface Controller<T>{
    T create(Object... args);
    void update(int id, Object... args);
    List<T> listAll();
    T findById(int id);
    boolean deleteById(int id);
    void saveAll();
    void loadAll();
}
