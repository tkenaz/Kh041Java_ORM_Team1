package crud_services;

import annotations.Column;
import annotations.Id;
import annotations.Table;

import java.lang.reflect.Field;

public class QuerySamples {

     static String forInsert(SimpleORMInterface object) throws IllegalAccessException {
         String columnName;
         String tableName = object.getClass().getAnnotation(Table.class).name();
         Field[] fields = object.getClass().getDeclaredFields();
         StringBuilder query = new StringBuilder("INSERT INTO ");
         StringBuilder valuesForQuery = new StringBuilder(" VALUES (");
         query.append(tableName).append(" ( id,");
         valuesForQuery.append(object.getId()).append(",");
         for(int i = 0; i < fields.length; i++){
             if(!fields[i].isAccessible()){
                 fields[i].setAccessible(true);
             }
             if(fields[i].isAnnotationPresent(Column.class) && !fields[i].isAnnotationPresent(Id.class)){
                 columnName = fields[i].getAnnotation(Column.class).name();
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
         String valueString = valuesForQuery.toString();
         String queryString = query.toString();
         String sql = queryString.substring(0, queryString.length()-1) + ") " +
                 valueString.substring(0, valueString.length()-1) + ");";

         //valuesForQuery.append(");");
         //query.append(")").append(valuesForQuery);

         System.out.println("Q in Query sample: " + sql);
         return sql;   //query.toString();
    }


    static String forUpdate(SimpleORMInterface object) throws IllegalAccessException {
        String columnName;
        String tableName = object.getClass().getAnnotation(Table.class).name();
        Field[] fields = object.getClass().getDeclaredFields();
        StringBuilder query = new StringBuilder("UPDATE ");
        query.append(tableName).append(" SET");
        for(int i = 0; i < fields.length; i++){
            if(!fields[i].isAccessible()){
                fields[i].setAccessible(true);
            }
            if(fields[i].isAnnotationPresent(Table.class) && !fields[i].isAnnotationPresent(Id.class)){
                columnName = fields[i].getAnnotation(Table.class).name();
                query.append(" ").append(columnName);
                if(fields[i].get(object) instanceof Number){
                    query.append(" = ").append(fields[i].get(object).toString()).append(",");
                }
                else {
                   query.append(" = '").append(fields[i].get(object).toString()).append("',");
                }
            }
        }
        //query.deleteCharAt(query.length() - 1);
        query.append(" WHERE id = ").append(object.getId()).append(";");
        return query.toString();
    }
}
