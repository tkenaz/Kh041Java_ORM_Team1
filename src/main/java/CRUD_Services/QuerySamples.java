package CRUD_Services;

import annotations.Entity;
import annotations.FieldName;
import annotations.Id;

import java.lang.reflect.Field;

public class QuerySamples {
     static String forInsert(OurORM object) throws IllegalAccessException {
         String columnName;
         String tableName = object.getClass().getAnnotation(Entity.class).tableName();
         Field[] fields = object.getClass().getDeclaredFields();
         StringBuilder query = new StringBuilder("INSERT INTO ");
         StringBuilder valuesForQuery = new StringBuilder(" VALUES (");
         query.append(tableName).append(" ( id,");
         valuesForQuery.append(object.getId());
         for(int i = 0; i < fields.length; i++){
             if(!fields[i].isAccessible()){
                 fields[i].setAccessible(true);
             }
             if(fields[i].isAnnotationPresent(FieldName.class) && !fields[i].isAnnotationPresent(Id.class)){
                 columnName = fields[i].getAnnotation(FieldName.class).name();
                 query.append(" ").append(columnName);
                 if(fields[i].get(object) instanceof Number){
                     valuesForQuery.append(" ").append(fields[i].get(object).toString());
                 }
                 else {
                     valuesForQuery.append(" '").append(fields[i].get(object).toString()).append("'");
                 }
                 if(i < fields.length - 1){
                     query.append(",");
                     valuesForQuery.append(",");
                 }
             }
         }
         valuesForQuery.append(");");
         query.append(")").append(valuesForQuery);
         return query.toString();
    }

    static String forUpdate(OurORM object) throws IllegalAccessException {
        String columnName;
        String tableName = object.getClass().getAnnotation(Entity.class).tableName();
        Field[] fields = object.getClass().getDeclaredFields();
        StringBuilder query = new StringBuilder("UPDATE ");
        query.append(tableName).append(" SET");
        for(int i = 0; i < fields.length; i++){
            if(!fields[i].isAccessible()){
                fields[i].setAccessible(true);
            }
            if(fields[i].isAnnotationPresent(FieldName.class) && !fields[i].isAnnotationPresent(Id.class)){
                columnName = fields[i].getAnnotation(FieldName.class).name();
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
