package CRUD_Services;

import annotations.*;

import java.lang.reflect.Field;

public class QuerySamples {

    public static String forInsert(SimpleORMInterface object) throws IllegalAccessException {
        String columnName;
        String tableName = object.getClass().getAnnotation(Table.class).name();
        Field[] fields = object.getClass().getDeclaredFields();
        StringBuilder query = new StringBuilder("INSERT INTO ");
        StringBuilder valuesForQuery = new StringBuilder(" VALUES (");
        query.append(tableName).append(" (id,");
        valuesForQuery.append(object.getId()).append(",");
        for(int i = 0; i < fields.length; i++){
            fields[i].setAccessible(true);
            if(fields[i].isAnnotationPresent(ManyToOne.class)){
                columnName = fields[i].getAnnotation(JoinColumn.class).name();
                query.append(" ").append(columnName).append(",");
                valuesForQuery.append(" ").append(((SimpleORMInterface) fields[i].get(object)).getId()).append(",");
            }
            else if(fields[i].isAnnotationPresent(Column.class) && !fields[i].isAnnotationPresent(Id.class)){
                columnName = fields[i].getAnnotation(Column.class).name();
                query.append(" ").append(columnName).append(",");
                if(fields[i].get(object) instanceof Number){
                    valuesForQuery.append(" ").append(fields[i].get(object).toString()).append(",");
                }
                else {
                    valuesForQuery.append(" '").append(fields[i].get(object).toString()).append("'").append(",");
                }
            }
            fields[i].setAccessible(false);
        }
        query.deleteCharAt(query.length() - 1);
        valuesForQuery.deleteCharAt(valuesForQuery.length() - 1);
        valuesForQuery.append(");");
        query.append(")").append(valuesForQuery);
        return query.toString();
    }

    public static String forUpdate(SimpleORMInterface object) throws IllegalAccessException {
        String columnName;
        String tableName = object.getClass().getAnnotation(Table.class).name();
        Field[] fields = object.getClass().getDeclaredFields();
        StringBuilder query = new StringBuilder("UPDATE ");
        query.append(tableName).append(" SET");
        for(int i = 0; i < fields.length; i++){
            if(!fields[i].isAccessible()){
                fields[i].setAccessible(true);
            }
            if(fields[i].isAnnotationPresent(Column.class) && !fields[i].isAnnotationPresent(Id.class)){
                columnName = fields[i].getAnnotation(Column.class).name();
                query.append(" ").append(columnName);
                if(fields[i].get(object) instanceof Number){
                    query.append(" = ").append(fields[i].get(object).toString()).append(",");
                }
                else {
                    query.append(" = '").append(fields[i].get(object).toString()).append("',");
                }
            }
        }
        query.deleteCharAt(query.length() - 1);
        query.append(" WHERE id = ").append(object.getId()).append(";");
        return query.toString();
    }
}
