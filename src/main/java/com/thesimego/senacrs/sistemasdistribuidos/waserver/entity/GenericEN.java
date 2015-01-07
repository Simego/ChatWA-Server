package com.thesimego.senacrs.sistemasdistribuidos.waserver.entity;

/**
 *
 * @author drafaelli
 */
public abstract class GenericEN {

    public abstract Integer getId();
    @Override
    public abstract String toString();
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof GenericEN)) {
            return false;
        }
        GenericEN other = (GenericEN) object;
        return (this.getId() != null || other.getId() == null) && (this.getId() == null || this.getId().equals(other.getId()));
    }
    
}
