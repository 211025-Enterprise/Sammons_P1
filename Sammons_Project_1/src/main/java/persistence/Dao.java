package persistence;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

import connect.SQLConnection;

public class Dao<T> {

    public T createTable(Class<T> clazz, T t){
        StringBuilder table = new StringBuilder();
        StringBuilder column = new StringBuilder();
        StringBuilder values = new StringBuilder();
        String creation = "CREATE TABLE IF NOT EXISTS ";
        table.append("projorm.").append(clazz.getSimpleName().toLowerCase());
        table.append(" (");
        int counter = 1;
        for(Field field: clazz.getDeclaredFields()){
            table.append("\"").append(field.getName()).append("\" ");
            column.append(field.getName());
            values.append(("?")).append(",");
            if(field.getType().getSimpleName().equals("byte") || field.getType().getSimpleName().equals("short") ){
                table.append("int");
            }
            else if(field.getType().getSimpleName().equals("int") || field.getType().getSimpleName().equals("long") ){
                table.append("int");
            }
            else if(field.getType().getSimpleName().equals("float") || field.getType().getSimpleName().equals("double") ){
                table.append("double");
            }
            else if(field.getType().getSimpleName().equals("String") || field.getType().getSimpleName().equals("char") ){
                table.append("varchar");
            }
            else if(field.getType().getSimpleName().equals("boolean")){
                table.append("boolean");
            }
            table.append(counter == clazz.getDeclaredFields().length? ") " : ", ");
           column.append(counter == clazz.getDeclaredFields().length? ") " : ", ");
            counter++;
        }values.deleteCharAt(values.length()-1);
        String sql = creation+ table.toString();
        String sqlC = column.toString();
        String insertsql = "INSERT INTO projorm." + clazz.getSimpleName()
                .toLowerCase()+ " (" +sqlC+ "VALUES ("+values+")";
        try {
            SQLConnection q = new SQLConnection();
            PreparedStatement stmnt = q.getInstance().prepareStatement(sql);
            stmnt.executeUpdate();
            int i = 1;
            PreparedStatement stmntdos = q.getInstance().prepareStatement(insertsql);
            for(Field field: clazz.getDeclaredFields()){
                field.setAccessible(true);
                stmntdos.setObject(i++,field.get(t));
            }if(stmntdos.executeUpdate() >0)return t;
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
//----------------------------------------------------------------------------------------------------------------
    public List<T> readAll(Class<T> clazz) {
        List<T> output = new ArrayList<>();
        StringBuilder reader = new StringBuilder();
        String tableName = clazz.getSimpleName().toLowerCase();
        Field[] fields = clazz.getDeclaredFields();
        reader.append("SELECT * FROM projorm.").append(clazz.getSimpleName().toLowerCase());
        String sql = reader.toString();
        try {
            SQLConnection q = new SQLConnection();
            PreparedStatement stmt = q.getInstance().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
              Constructor<?> constructor = Arrays.stream(clazz.getDeclaredConstructors()).filter(c -> c.getParameterCount() ==0).findFirst().orElse(null);
              constructor.setAccessible(true);
              T t = (T) constructor.newInstance();
              int i = 1;
              for(Field field : fields){
                  field.setAccessible(true);
                  field.set(t,rs.getObject(i));
                  i++;
              }output.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return output;

    }
    //---------------------------------------------------------------------------------------------------------

    public T update(Class<?> clazz, T t) {
        String tableName = clazz.getSimpleName().toLowerCase();
        Field[] fields = clazz.getDeclaredFields();
        String updateSQL = "UPDATE projorm." + tableName + " SET (";
        StringBuilder updater = new StringBuilder();
        StringBuilder values = new StringBuilder();
        updater.append(updateSQL);
        for (Field field : fields) {
            updater.append(field.getName()).append(", ");
            values.append(("?")).append(",");
        }
        updater.deleteCharAt(updater.length() - 2);
        values.deleteCharAt((values.length()-1));
        updater.append(") = (").append(values).append(")");
        String sqal = updater.toString();

        try { SQLConnection q = new SQLConnection();
            PreparedStatement stmnt = q.getInstance().prepareStatement(sqal);
            int i = 1;
              for (Field field : clazz.getDeclaredFields()) {
        field.setAccessible(true);
                  stmnt.setObject(i++,field.get(t));
              }if(stmnt.executeUpdate() >0)return t;
        }catch (SQLException | IllegalAccessException throwables) {
            throwables.printStackTrace();
        }
        String sql = updater.toString();
        System.out.println(sql);
        return null;
    }
//-------------------------------------------------------------------------------------

    public void deleteTable(Class<?> clazz) {
        String sql = "DROP TABLE projorm." + clazz.getSimpleName() + ";";
        try {
            SQLConnection q = new SQLConnection();
            PreparedStatement stmnt = q.getInstance().prepareStatement(sql);
            stmnt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) throws IllegalAccessException {
//        testerclass o = new testerclass();
//        SQLConnection u = new SQLConnection();
//      //  String[] values = {o.getName(),o.getAge(),o.isAlive()};
//        // u.getInstance();
//        Dao m = new Dao();
//        //m.deleteTable(testerclass.class);
//       // m.createTable(testerclass.class,o);
//          m.update(testerclass.class,o);
//       // m.deleteTable(testerclass.class);
//
//    }

}






