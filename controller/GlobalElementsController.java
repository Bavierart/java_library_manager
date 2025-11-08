package controller;

import model.Category;
import model.CrudObjectInterface;
import model.DataManager;
import java.util.ArrayList;
import java.util.List;

public abstract class GlobalElementsController<T extends CrudObjectInterface> implements Controller<T> {
    protected final CrudObjectAux<T> crudAux;
    protected final String dataFile;

    // O construtor permanece o mesmo que você enviou
    protected GlobalElementsController(String dataFile) {
        this.crudAux = new CrudObjectAux<T>();
        this.dataFile = dataFile;
        new java.io.File("data").mkdirs();
    }

    protected abstract T build(Object... args);
    protected abstract void applyUpdate(T obj, Object... args);
    protected abstract void resetIdCounter(int nextId);

    @Override
    public T create(Object... args) {
        T obj = build(args);
        if (obj != null) crudAux.addObject(obj);
        return obj;
    }

    @Override
    public void update(int id, Object... args) {
        T obj = crudAux.findById(id);
        if (obj != null) applyUpdate(obj, args);
    }

    @Override
    public List<T> listAll() { return new ArrayList<>(crudAux.getAllObjects()); }

    @Override
    public T findById(int id) { return crudAux.findById(id); }

    @Override
    public boolean deleteById(int id) {
        T obj = crudAux.findById(id);
        return crudAux.delete(obj);
    }

    @Override
    public void saveAll() {
        List<T> all = new ArrayList<>(crudAux.getAllObjects());
        if (all.isEmpty()) {
            resetIdCounter(1);
        } else {
            int maxId = all.stream()
                    .mapToInt(CrudObjectInterface::getId)
                    .max()
                    .orElse(0);
            resetIdCounter(maxId + 1);
        }
        DataManager.save(all, dataFile);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadAll() {
        crudAux.getAllObjects().clear();

        List<T> loaded = DataManager.load(dataFile);

        if (loaded == null || loaded.isEmpty()) {
            resetIdCounter(1);
            return;
        }
        for (T obj : loaded) crudAux.addObject(obj);

        List<T> all = crudAux.getAllObjects();

        int maxId = all.stream()
                .mapToInt(CrudObjectInterface::getId)
                .max()
                .orElse(0);

        resetIdCounter(maxId + 1);
    }
}