package com.thesimego.senacrs.sistemasdistribuidos.waserver.dao;

import com.thesimego.senacrs.sistemasdistribuidos.waserver.entity.GenericEN;
import com.thesimego.senacrs.sistemasdistribuidos.waserver.util.Util;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Classe genérica para acesso ao SQLite
 *
 * @author drafaelli
 * @param <T>
 */
public class GenericDAO<T> {

    @Autowired
    private SQLiteConnection sqliteConnection;

    private final ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
    private final Class<T> cls = (Class<T>) (type).getActualTypeArguments()[0];
    private final String table = Util.getTableNameByAnnotation(cls);

    /**
     * Contagem de registros na tabela
     *
     * @return
     */
    public Integer count() {
        try {
            String query = "SELECT count(1) FROM " + table;
            if (Util.isDebugQuery()) {
                outputQuery(query, "count");
            }
            PreparedStatement stmt = sqliteConnection.getConnection().prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            Integer count = 0;
            while (rs.next()) {
                count = rs.getInt(1);
            }
            return count;
        } catch (SQLException ex) {
            Logger.getLogger(cls.getName()).log(Level.SEVERE, "count (Object='" + cls.getSimpleName() + "'", ex);
            return null;
        }
    }

    /**
     * Busca por objeto único baseado no id da tabela
     *
     * @param id
     * @return
     */
    public T find(Long id) {
        return (T) find(cls, id);
    }

    /**
     * Busca por objeto único baseado no id da tabela por classe
     *
     * @param <X>
     * @param clazz
     * @param id
     * @return
     */
    private <X> Object find(Class<X> clazz, Long id) {
        try {
            String query = "SELECT * FROM " + Util.getTableNameByAnnotation(clazz) + " WHERE id = " + id;
            if (Util.isDebugQuery()) {
                outputQuery(query, "findOne");
            }
            PreparedStatement stmt = sqliteConnection.getConnection().prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                return getObjectFromResultSet(clazz, rs);
            }
        } catch (SQLException | SecurityException | IllegalArgumentException ex) {
            Logger.getLogger(cls.getName()).log(Level.SEVERE, "find (Object='" + cls.getSimpleName() + "'", ex);
        }
        return null;
    }

    /**
     * Consulta baseada em campos para montar a cláusula WHERE
     *
     * @param dbFields
     * @return
     */
    protected T find(DBField... dbFields) {
        updateDBFields(dbFields, cls);
        try {
            // Método para gerar o PreparedStatement automaticamente baseando-se nos campos
            PreparedStatement stmt = prepareSelectFromDBFields(null, dbFields);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                return getObjectFromResultSet(rs);
            }
        } catch (SQLException | SecurityException | IllegalArgumentException ex) {
            Logger.getLogger(cls.getName()).log(Level.SEVERE, "find (Object='" + cls.getSimpleName() + "'", ex);
        }
        return null;
    }

    /**
     * Listagem baseada em campos para montar a cláusula WHERE
     *
     * @param dbFields
     * @return
     */
    protected List<T> list(DBField... dbFields) {
        return list(null, dbFields);
    }

    protected List<T> list(DBJoin dbJoin, DBField... dbFields) {
        List<T> list = new ArrayList<>();
        updateDBFields(dbFields, cls);
        try {
            // Método para gerar o PreparedStatement automaticamente baseando-se nos campos
            PreparedStatement stmt = prepareSelectFromDBFields(dbJoin, dbFields);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(getObjectFromResultSet(rs));
            }
        } catch (SQLException | SecurityException | IllegalArgumentException ex) {
            Logger.getLogger(cls.getName()).log(Level.SEVERE, "list (Object='" + cls.getSimpleName() + "'", ex);
        }
        return list;
    }

    /**
     * Inclui novo registro baseando-se nos dbFields
     *
     * @param dbFields
     */
    protected void insert(DBField... dbFields) {
        updateDBFields(dbFields, cls);
        try {
            // Método para gerar o PreparedStatement automaticamente baseando-se nos campos
            PreparedStatement stmt = prepareInsertFromDBFields(dbFields);
            stmt.executeUpdate();
        } catch (SQLException | SecurityException | IllegalArgumentException ex) {
            Logger.getLogger(cls.getName()).log(Level.SEVERE, "insert (Object='" + cls.getSimpleName() + "'", ex);
            try {
                sqliteConnection.getConnection().rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(GenericDAO.class.getName()).log(Level.SEVERE, "Rollback failed", ex1);
            }
        }
    }

    /**
     * Deleta os registros baseando-se nos dbFields
     *
     * @param dbFields
     */
    protected void delete(DBField... dbFields) {
        updateDBFields(dbFields, cls);
        try {
            // Método para gerar o PreparedStatement automaticamente baseando-se nos campos
            PreparedStatement stmt = prepareDeleteFromDBFields(dbFields);
            stmt.executeUpdate();
        } catch (SQLException | SecurityException | IllegalArgumentException ex) {
            Logger.getLogger(cls.getName()).log(Level.SEVERE, "delete (Object='" + cls.getSimpleName() + "'", ex);
            try {
                sqliteConnection.getConnection().rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(GenericDAO.class.getName()).log(Level.SEVERE, "Rollback failed", ex1);
            }
        }
    }

    public void deleteAll(List<T> list) {
        try {
            // Método para gerar o PreparedStatement automaticamente baseando-se nos campos
            StringBuilder query = new StringBuilder();
            query.append("DELETE FROM ").append(table);
            query.append(" WHERE id in(");
            for (int i = 0; i < list.size(); i++) {
                query.append("?");
                if (i < list.size() - 1) {
                    query.append(",");
                }
            }
            query.append(");");

            PreparedStatement stmt = sqliteConnection.getConnection().prepareStatement(query.toString());
            int field = 1;
            for (T t : list) {
                GenericEN en = (GenericEN)t;
                stmt.setInt(field++, en.getId());
            }
            stmt.executeUpdate();
        } catch (SQLException | SecurityException | IllegalArgumentException ex) {
            Logger.getLogger(cls.getName()).log(Level.SEVERE, "deleteAll (Object='" + cls.getSimpleName() + "'", ex);
            try {
                sqliteConnection.getConnection().rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(GenericDAO.class.getName()).log(Level.SEVERE, "Rollback failed", ex1);
            }
        }
    }

    /**
     * Executa uma query de update baseada na string passada.
     *
     * @param query
     */
    public void executeUpdateByQuery(String query) {
        try {
            if (Util.isDebugQuery()) {
                outputQuery(query, "executeUpdateByQuery");
            }
            PreparedStatement stmt = sqliteConnection.getConnection().prepareStatement(query);
            stmt.executeUpdate();
        } catch (SQLException | SecurityException | IllegalArgumentException ex) {
            Logger.getLogger(cls.getName()).log(Level.SEVERE, "executeUpdateByQuery (Object='" + cls.getSimpleName() + "'", ex);
            try {
                sqliteConnection.getConnection().rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(GenericDAO.class.getName()).log(Level.SEVERE, "Rollback failed", ex1);
            }
        }
    }

    /**
     * Cria um PreparedStatement de Select
     *
     * @param dbFields
     * @return
     * @throws SQLException
     */
    private PreparedStatement prepareSelectFromDBFields(DBJoin dbJoin, DBField[] dbFields) throws SQLException {
        StringBuilder query = new StringBuilder();
        if (dbJoin != null) {
            // Monta o INNER JOIN da consulta
            createInnerJoinQuery(query, dbJoin);
        } else {
            query.append("SELECT * FROM ").append(table);
        }

        // Monta o WHERE da consulta
        createWhereByFields(query, dbFields);

        if (Util.isDebugQuery()) {
            outputQuery(query.toString(), "prepareSelectFromDBFields");
        }
        PreparedStatement stmt = sqliteConnection.getConnection().prepareStatement(query.toString());

        // Adiciona os valores ao PreparedStatement
        int field = 1;
        for (DBField dbField : dbFields) {
            stmt.setObject(field++, dbField.getValue());
        }

        return stmt;
    }

    /**
     * Cria um PreparedStatement de Insert
     *
     * @param dbFields
     * @return
     * @throws SQLException
     */
    private PreparedStatement prepareInsertFromDBFields(DBField[] dbFields) throws SQLException {

        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        // Adiciona os campos que serão usados no insert
        int fieldCount = 1;
        for (DBField dbField : dbFields) {
            if (fieldCount == dbFields.length) {
                columns.append(dbField.column);
                values.append("?");
            } else {
                columns.append(dbField.column).append(", ");
                values.append("?, ");
            }
            fieldCount++;
        }

        StringBuilder query = new StringBuilder().append("INSERT INTO ").append(table);
        query.append(" ( ").append(columns.toString()).append(" ) ");
        query.append(" VALUES ( ").append(values.toString()).append(" ) ");

        if (Util.isDebugQuery()) {
            outputQuery(query.toString(), "prepareInsertFromDBFields");
        }
        PreparedStatement stmt = sqliteConnection.getConnection().prepareStatement(query.toString());

        // Adiciona os valores ao PreparedStatement
        int field = 1;
        for (DBField dbField : dbFields) {
            stmt.setObject(field++, dbField.getValue());
        }

        return stmt;
    }

    /**
     * Cria um PreparedStatement de Delete
     *
     * @param dbFields
     * @return
     * @throws SQLException
     */
    private PreparedStatement prepareDeleteFromDBFields(DBField[] dbFields) throws SQLException {
        StringBuilder query = new StringBuilder().append("DELETE FROM ").append(table);

        // Monta o WHERE da consulta
        createWhereByFields(query, dbFields);

        if (Util.isDebugQuery()) {
            outputQuery(query.toString(), "prepareDeleteFromDBFields");
        }
        PreparedStatement stmt = sqliteConnection.getConnection().prepareStatement(query.toString());

        // Adiciona os valores ao PreparedStatement
        int field = 1;
        for (DBField dbField : dbFields) {
            stmt.setObject(field++, dbField.getValue());
        }
        return stmt;
    }

    /**
     * Cria o objeto a partir do ResultSet
     *
     * @param rs
     * @return
     */
    private T getObjectFromResultSet(ResultSet rs) {
        return (T) getObjectFromResultSet(cls, rs);
    }

    /**
     * Cria o objeto a partir do ResultSet por classe
     *
     * @param <X>
     * @param clazz
     * @param rs
     * @return
     */
    private <X> Object getObjectFromResultSet(Class<X> clazz, ResultSet rs) {
        try {
            Object o = clazz.newInstance();
            for (Field field : Util.getColumnsByAnnotation(clazz)) {
                //System.out.println(field.getType().getSimpleName() + " " + field.getName());
                Object colResultObj = getValueFromResultSet(rs, field, clazz);
                if (colResultObj != null) {
                    for (Method m : clazz.getMethods()) {
                        if (m.getName().equalsIgnoreCase("set" + field.getName())) {
                            m.invoke(o, field.getType().cast(colResultObj));
                        }
                    }
                }
            }
            return o;
        } catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(cls.getName()).log(Level.SEVERE, "getObjectFromResultSet (Object='" + clazz.getSimpleName() + "'", ex);
            return null;
        }
    }

    /**
     * Busca o valor para o tipo específico no ResultSet
     *
     * @param rs
     * @param field
     * @return
     */
    private Object getValueFromResultSet(ResultSet rs, Field field, Class clazz) {
        try {
            Classes classesType = Classes.parse(field.getType().getSimpleName());
            if (classesType == null) {
                return null;
            }
            switch (classesType) {
                case STRING:
                    return rs.getString(Util.getColumnFromField(field, clazz));
                case INTEGER:
                    return rs.getInt(Util.getColumnFromField(field, clazz));
                case LONG:
                    return rs.getLong(Util.getColumnFromField(field, clazz));
                case DOUBLE:
                    return rs.getDouble(Util.getColumnFromField(field, clazz));
                case ENTITY:
                    if (field.getType().getSimpleName().contains("EN")) {
                        return find(field.getType(), rs.getLong(Util.getJoinColumnFromField(field, clazz)));
                    }
                    return null;
                default:
                    return null;
            }
        } catch (SQLException ex) {
            return null;
        }
    }

    /**
     * Monta o INNER JOIN da consulta baseando-se no DBJoin
     *
     * @param query
     * @param dbJoin
     */
    private void createInnerJoinQuery(StringBuilder query, DBJoin dbJoin) {
        query.append("SELECT ");
        List<Field> fields = Util.getColumnsByAnnotation(cls);
        for (int i = 0; i < fields.size(); i++) {
            query.append(table).append(".").append(Util.getColumnFromField(fields.get(i), cls));
            if (i < fields.size() - 1) {
                query.append(", ");
            }
        }

        query.append(" FROM ").append(table);

        String joinTable = Util.getTableNameByAnnotation(dbJoin.joinClass);
        query.append(" INNER JOIN ").append(joinTable).append(" ON ");

        query.append(joinTable).append(".").append(dbJoin.joinTableField);
        query.append(" = ");
        query.append(table).append(".").append(dbJoin.sourceTableField);
    }

    /**
     * Monta o WHERE da consulta baseando-se nos DBFields
     *
     * @param query
     * @param dbFields
     */
    private void createWhereByFields(StringBuilder query, DBField dbFields[]) {
        // Adiciona os campos na string da query
        for (DBField dbField : dbFields) {
            if (!query.toString().contains("WHERE")) {
                query.append(" WHERE ").append(dbField.getColumn()).append(" = ? ");
            } else {
                query.append(" AND ").append(dbField.getColumn()).append(" = ? ");
            }

        }
    }

    /**
     * Log das queries
     *
     * @param query
     * @param method
     */
    private void outputQuery(String query, String method) {
        Logger.getLogger("SQL Output").log(Level.INFO, "[{0}] {1}", new Object[]{method, query});
    }

    /**
     * Atualiza as colunas com os seus nomes na anotação
     *
     * @param dbFields
     * @param clazz
     */
    private void updateDBFields(DBField[] dbFields, Class clazz) {
        for (DBField dbField : dbFields) {
            Field field = Util.findFieldByName(dbField.getColumn(), clazz);
            if (field == null) {
                continue;
            }
            String column = Util.getColumnFromField(field, clazz);
            if (column == null) {
                column = Util.getJoinColumnFromField(field, clazz);
            }
            dbField.setColumn(column);
        }
    }

    /**
     * Enum usado na busca por tipos de classe
     */
    private enum Classes {

        STRING(String.class.getSimpleName()),
        INTEGER(Integer.class.getSimpleName()),
        LONG(Long.class.getSimpleName()),
        DOUBLE(Double.class.getSimpleName()),
        ENTITY("EN");

        private final String value;

        private Classes(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Classes parse(String value) {
            for (Classes classes : Classes.values()) {
                if (value.contains(classes.getValue())) {
                    return classes;
                }
            }
            return null;
        }

    }

    /**
     * Classe para ajudar a montar as cláusulas WHERE
     */
    public class DBField {

        private String column;
        private Object value;

        public DBField(String column, Object value) {
            this.column = column;
            this.value = value;
        }

        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    /**
     * Classe para ajudar a montar Join
     */
    public class DBJoin {

        private String joinTableField;
        private String sourceTableField;
        private Class joinClass;

        public DBJoin(String joinTableField, String sourceTableField, Class joinClass) {
            this.joinTableField = joinTableField;
            this.sourceTableField = sourceTableField;
            this.joinClass = joinClass;
        }

        public String getJoinTableField() {
            return joinTableField;
        }

        public void setJoinTableField(String joinTableField) {
            this.joinTableField = joinTableField;
        }

        public String getSourceTableField() {
            return sourceTableField;
        }

        public void setSourceTableField(String sourceTableField) {
            this.sourceTableField = sourceTableField;
        }

        public Class getJoinClass() {
            return joinClass;
        }

        public void setJoinClass(Class joinClass) {
            this.joinClass = joinClass;
        }

    }

}
