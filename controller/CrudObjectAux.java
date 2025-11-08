package controller;

import model.CrudObjectInterface;

import java.util.ArrayList;

public final class CrudObjectAux<T extends CrudObjectInterface> {

    private ArrayList<T> allObjects = new ArrayList<>();

    public ArrayList<T> getAllObjects() {
        return allObjects;
    }


    public void addObject(T object) {
        if (object != null && !allObjects.contains(object)) allObjects.add(object);
    }


    public T findById(int id) {
        for (T object : allObjects) if (object.getId() == id) return object;
        return null;
    }


    public boolean delete(T object) {
        return allObjects.remove(object);
    }

    public void setObjects(ArrayList<T> loadedList) {
        this.allObjects = loadedList;
    }
}
