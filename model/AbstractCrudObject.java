package model;

import java.io.Serial;
import java.io.Serializable;

public abstract class AbstractCrudObject implements Serializable, CrudObjectInterface {
    @Serial
    private static final long serialVersionUID = 1L;
    protected final int id;

    protected AbstractCrudObject(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
