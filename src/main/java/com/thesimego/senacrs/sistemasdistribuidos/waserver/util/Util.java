package com.thesimego.senacrs.sistemasdistribuidos.waserver.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Simego
 */
public class Util {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    /**
     * Verifica se o usuário está logado na sessão
     * @param session
     * @return 
     */
    public static boolean isUserLogged(HttpSession session) {
        String login = (String) session.getAttribute("login");
        String password = (String) session.getAttribute("password");

        return login != null && password != null;
    }

    /**
     * Obtém o hash SHA256 da string passada
     * @param string
     * @return 
     */
    public static String getSha256(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(string.getBytes());

            byte byteData[] = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            System.err.println(ex);
            return "";
        }
    }

    /**
     * Converte de String para Data
     * @param date
     * @return 
     */
    public static String formatDate(Date date) {
        return sdf.format(date);
    }
    
    /**
     * Converte de Data para String
     * @param date
     * @return 
     */
    public static Date parseDate(String date) {
        try {
            return sdf.parse(date);
        } catch (ParseException ex) {
            return null;
        }
    }
    
    /**
     * Busca o nome da coluna pela anotação @Column ou @JoinColumn
     * @param <T>
     * @param cls
     * @return 
     */
    public static <T> List<Field> getColumnsByAnnotation(Class<T> cls) {
        List<Field> fields = new ArrayList<>();
        for (Field field : cls.getDeclaredFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation.annotationType().equals(Column.class) || annotation.annotationType().equals(JoinColumn.class)) {
                    fields.add(field);
                }
            }
        }
        return fields;
    }

    /**
     * Busca o nome da tabela na classe pelo @Table
     * @param <T>
     * @param cls
     * @return 
     */
    public static <T> String getTableNameByAnnotation(Class<T> cls) {
        for (Annotation annotation : cls.getAnnotations()) {
            if (annotation.annotationType().equals(Table.class)) {
                Table tableAnnotation = (Table) annotation;
                return tableAnnotation.name();
            }
        }
//        Logger.getLogger(cls.getName()).log(Level.SEVERE, "Util.getTableNameByAnnotation (Object='" + cls.getSimpleName() + "'",
//                new ReflectiveOperationException("Annotation @Table in class " + cls.getSimpleName() + " could not be found.")
//        );
        return null;
    }

    /**
     * Busca colunas pelo @Column
     * @param field
     * @param cls
     * @return 
     */
    public static String getColumnFromField(Field field, Class cls) {
        for (Annotation annotation : field.getAnnotations()) {
            if (annotation.annotationType().equals(Column.class)) {
                Column column = (Column) annotation;
                return column.name();
            }
        }
//        Logger.getLogger(cls.getName()).log(Level.SEVERE, "Util.getColumnFromField (Object='" + field.getName() + "'",
//                new ReflectiveOperationException("Annotation @Column in field " + field.getName() + " could not be found.")
//        );
        return null;
    }
    
    /**
     * Busca colunas por @JoinColumn
     * @param field
     * @param cls
     * @return 
     */
    public static String getJoinColumnFromField(Field field, Class cls) {
        for (Annotation annotation : field.getAnnotations()) {
            if (annotation.annotationType().equals(JoinColumn.class)) {
                JoinColumn column = (JoinColumn) annotation;
                return column.name();
            }
        }
//        Logger.getLogger(cls.getName()).log(Level.SEVERE, "Util.getJoinColumnFromField (Object='" + field.getName() + "'",
//                new ReflectiveOperationException("Annotation @JoinColumn in field " + field.getName() + " could not be found.")
//        );
        return null;
    }
    
    /**
     * Busca campo da classe por nome
     * @param name
     * @param cls
     * @return 
     */
    public static Field findFieldByName(String name, Class cls) {
        for(Field field : cls.getDeclaredFields()) {
            if(name.equals(field.getName())) {
                return field;
            }
        }
        return null;
    }

    /**
     * Define se faz debug das queries
     * @return 
     */
    public static boolean isDebugQuery() {
        String debugQuery = System.getProperty("com.thesimego.debug.query");
        if (debugQuery != null) {
            Boolean b = Boolean.parseBoolean(debugQuery);
            return b;
        }
        return false;
    }

}
